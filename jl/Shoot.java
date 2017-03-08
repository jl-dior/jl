package com.jl;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** 游戏类，开始游戏 */
public class Shoot extends JPanel {
	// 常量用大写
	public static final int WIDTH = 400;
	public final static int HEIGHT = 654;
	// 定义状态常量
	public static final int RUN = 0;
	public static final int PAUSE = 1;
	public static final int START = 2;
	public static final int OVER = 3;
	private int state = START;// 游戏默认开始状态
	/* 状态切换
	 * START -> RUN (鼠标单机)
	 * RUN -> PAUSE (鼠标移出)
	 *     -> OVER (游戏结束,生命值<=0)
	 * PAUSE -> RUN (鼠标移进)
	 * OVER -> START (鼠标单机)
	 */
	// 静态图片，提前加载
	public static BufferedImage hero0 = null;
	public static BufferedImage hero1 = null;
	public static BufferedImage bee = null;
	public static BufferedImage airplane = null;
	public static BufferedImage background = null;
	public static BufferedImage pause = null;
	public static BufferedImage gameover = null;
	public static BufferedImage start = null;
	public static BufferedImage bullet = null;
	// 静态代码块，用于加载图片
	static {
		try {
			// 读取（加载）磁盘图片
			// 通过相对路径获取图片
			hero0 = ImageIO.read(Shoot.class.getResource("hero0.png"));
			hero1 = ImageIO.read(Shoot.class.getResource("hero1.png"));
			bee = ImageIO.read(Shoot.class.getResource("bee.png"));
			airplane = ImageIO.read(Shoot.class.getResource("airplane.png"));
			background = ImageIO.read(Shoot.class.getResource("background.png"));
			pause = ImageIO.read(Shoot.class.getResource("pause.png"));
			gameover = ImageIO.read(Shoot.class.getResource("gameover.png"));
			start = ImageIO.read(Shoot.class.getResource("start.png"));
			bullet = ImageIO.read(Shoot.class.getResource("bullet.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Hero hero = new Hero();// 游戏中的英雄机
	// 好多子弹
	List<Bullet> bullets = new ArrayList<Bullet>();
	// 好多飞行物
	List<FlyingObject> flyings = new ArrayList<FlyingObject>();
	// Override 重写父类画画方法
	public void paint(Graphics g) {
		super.paint(g);// 调用父类paint方法
		// 画背景(不动)
		g.drawImage(background, 0, 0, null);
		// 画分数
		// 新建字体对象：宋体, 加粗, 20
		Font font = new Font("宋体", Font.BOLD, 20);
		g.setFont(font);// 设置字体
		g.setColor(Color.red);// 设置颜色
		g.drawString("SCORE: "+score, 10, 25);
		g.drawString("LIFE: "+hero.life, 10, 55);
		// 画英雄
		g.drawImage(hero.image, hero.x, hero.y, null);
		// 画飞行物（敌机+蜜蜂）
		drawFlyings(g);
		// 画子弹
		drawBullets(g);
		if(state == START){
			// 开始
			g.drawImage(start, 0, 0, null);
		}
		if(state == PAUSE){
			// 暂停
			g.drawImage(pause, 0, 0, null);
		}
		if(state == OVER) {
			// 结束
			g.drawImage(gameover, 0, 0, null);
		}
	}
	/**
	 * 使用匿名内部类创建鼠标监听对象
	 */
	MouseAdapter adapter = new MouseAdapter(){
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if(state == START) {
				state = RUN;
			}
			if(state == OVER) {
				state = START;
			}
		}
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			if(state == PAUSE) {
				state = RUN;
			}
		}
		public void mouseExited(MouseEvent e) {
			if(state == RUN){
				state = PAUSE;
			}
			super.mouseExited(e);
		}
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
			/*
			 * 拿到鼠标坐标
			 * 根据鼠标坐标，改变英雄坐标
			 */
			if(state == RUN) {
				int mouseX = e.getX();// 鼠标X坐标
				int mouseY = e.getY();// 鼠标Y坐标
				hero.x = mouseX - hero.width / 2 ;
				hero.y = mouseY - hero.height / 2;
			}
		}
	};
	/** 画飞行物 */
	public void drawFlyings(Graphics g) {
		for(FlyingObject flyObject : flyings) {
			g.drawImage(flyObject.image, flyObject.x, flyObject.y, null);
		}
		/*for(int i = 0; i < flyings.size(); i ++) {
			FlyingObject flyingObject = flyings.get(i);
			
		}*/
	}
	public void drawBullets(Graphics g) {
		for(Bullet bullet : bullets) {
			g.drawImage(bullet.image, bullet.x, bullet.y, null);
		}
	}
	/** 开始游戏 */
	Timer timer ;
	public void action() {
		// 定时器
		timer = new Timer();
		// 制定时间表
		timer.schedule(new TimerTask(){
			// 一直持续运行：运动、发射、飞行物进入、判断相撞、判断结束
			public void run() {
				if(state == RUN) {
					stepAction();// 定义一个运动方法
					shootAction();// 射击
					enterFlyingAction();// 飞行物进入
					outOfBoundsAction();// 判断越界
					bangAction();// 判断相撞(子弹)
					checkGameOver();// 判断游戏结束
				}
				repaint();// 刷新->调用paint方法
			}
		}, 10, 10);
		// this代表当前画板对象
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
	}
	public void checkGameOver() {
		// 判断游戏是否结束
		if(gameOver()){
			state = OVER;
		}
	}
	/** 判断游戏结束的真正步骤 */
	public boolean gameOver() {
		
		
		Iterator<FlyingObject> it = flyings.iterator();
		while(it.hasNext()) {
			FlyingObject flyings = it.next();
			if(hero.hit(flyings)){ //判断英雄机是否与敌人撞上了
				it.remove();
				hero.subtractLife();   //英雄机减命
				hero.setDoubleFire(0); //英雄机火力值清零	
				}
			}
			return(hero.getLife()<=0);//英雄机的命小于等于0，即为游戏结束
	

//		return false;
	
	}
		
		
		
		/*
		 * 1、时刻在判断英雄有没有被碰撞
		 *      hero.isHit(flyObject);
		 *      算法提示：把敌机固定不动，英雄动
		 *             判定英雄中心点的范围
		 * 2、如果相撞，声明值-1
		 * 3、判断生命值是否<=0
		 * 4、如果<=0 return true
		 */
		

	/** 判断相撞(子弹) */
	public void bangAction() {
		/*
		 * 判断每一个子弹
		 * 如果子弹和敌机相撞，那么从集合中移除子弹和敌机
		 * 集合.remove();-> 循环中存在remove，一定要用迭代器
		 */
		Iterator<Bullet> it = bullets.iterator();
		while(it.hasNext()) {
			Bullet bullet = it.next();
			/*
			 *  判断飞机有没有被bullet击中
			 *  如果击中，则移除bullet
			 */
			if(bang(bullet)){
				it.remove();
			}
		}
	}
	/** 判断每一个飞行物有没有被当前子弹击中 */
	public boolean bang(Bullet bullet) {
		Iterator<FlyingObject> it = flyings.iterator();
		while(it.hasNext()) {
			FlyingObject fly = it.next();
			/*
			 * 判断飞行物被撞击
			 * fly 和 bullet 判断
			 */
			if(fly instanceof Enemy) {// 判断fly是不是Enemy类型对象
				Enemy e = (Enemy) fly;
				if(e.shootBy(bullet)){// 如果被击中
					// 加分
					score += e.getScore();
					// 从集合中移除fly
					it.remove();
					return true;
				}
			}
			if(fly instanceof Bee) {
				Bee b = (Bee) fly;
				if(b.shootBy(bullet)){
					// 奖励
					/*
					 * 判断蜜蜂奖励类型是哪种
					 * 1、如果是DOUBLE_FIRE 双倍火力
					 *      属于英雄自带属性
					 * 2、如果是LIFE 加生命值
					 */
					if(b.award == Bee.DOUBLE_FIRE) {
						hero.double_fire += 40;
					} 
					if(b.award == Bee.LIFE){
						hero.life ++;
					}
					it.remove();
					return true;
				}
			}
		}
		return false;
	}
	/** 判断越界 */
	public void outOfBoundsAction() {
		/*
		 * 判断对象：子弹(bullets)、蜜蜂、敌机(flyings)
		 * 如果越界，将从集合中移除
		 */
		Iterator<Bullet> itb =  bullets.iterator();
		while(itb.hasNext()) {
			Bullet bullet = itb.next();
			if (bullet.isOutOfBounds()){
				itb.remove();
			}
		}
		Iterator<FlyingObject> itf =  flyings.iterator();
		while(itf.hasNext()) {
			FlyingObject flyingObject = itf.next();
			if (flyingObject.isOutOfBounds()){
				itf.remove();
			}
		}
	}
	/** 飞行物进入 */
	int flyIndex = 0;
	public void enterFlyingAction() {
		flyIndex ++;
		// 每隔300毫秒产生一个飞行物
		if(flyIndex % 30 == 0) {
			// 随机产生0:Bee  1~19:Enemy
			FlyingObject flyingObject ;
			int type = (int) (Math.random() * 20);
			switch(type){
			case 0:
				flyingObject = new Bee();
				break;
			default:
				flyingObject = new Enemy();
				break;
			}
			flyings.add(flyingObject);
		}
		// 防止溢出(三目运算符)
		flyIndex = (flyIndex == 1000000000 ? 0:flyIndex);
	}
	/** 射击 */
	int shootIndex = 0;
	public void shootAction() {
		shootIndex ++;
		/*
		 * 1. 产生新的子弹(子弹的构造器)
		 *     子弹的x和y坐标，是以hero为参照物的
		 * 2. 把新的子弹加入到bullets
		 * 3. 画子弹(paint方法中)
		 * 4. 让子弹动
		 */
		// 每400毫秒产生一个子弹
		if(shootIndex % 40 == 0){
			/*
			 * 一发炮弹，还是两发炮弹
			 */
			Bullet[] bs = hero.shoot();
			for(int i = 0; i < bs.length; i++){
				bullets.add(bs[i]);
			}
		}
		// 防止溢出(三目运算符)
		shootIndex = (shootIndex == 1000000000 ? 0:shootIndex);
	}
	// 运动
	public void stepAction() {
		hero.step();
		// 子弹动
		for (Bullet bullet : bullets) {
			bullet.step();
		}
		// 飞行物动
		for (FlyingObject flyObject : flyings) {
			flyObject.step();
		}
	}
	int score = 0;// 分数
	public static void main(String[] args) {
		// 老婆 小丽 = new 老婆();
		JFrame window = new JFrame();
		window.setSize(400, 654);// 设置大小
		// 设置默认关闭选项
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		Shoot shoot = new Shoot();// 画板-paint-画画
		window.add(shoot);
//		window.show();// 显示窗口
		window.setVisible(true);
		shoot.action();// 开始游戏
	}
}
