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

/** ��Ϸ�࣬��ʼ��Ϸ */
public class Shoot extends JPanel {
	// �����ô�д
	public static final int WIDTH = 400;
	public final static int HEIGHT = 654;
	// ����״̬����
	public static final int RUN = 0;
	public static final int PAUSE = 1;
	public static final int START = 2;
	public static final int OVER = 3;
	private int state = START;// ��ϷĬ�Ͽ�ʼ״̬
	/* ״̬�л�
	 * START -> RUN (��굥��)
	 * RUN -> PAUSE (����Ƴ�)
	 *     -> OVER (��Ϸ����,����ֵ<=0)
	 * PAUSE -> RUN (����ƽ�)
	 * OVER -> START (��굥��)
	 */
	// ��̬ͼƬ����ǰ����
	public static BufferedImage hero0 = null;
	public static BufferedImage hero1 = null;
	public static BufferedImage bee = null;
	public static BufferedImage airplane = null;
	public static BufferedImage background = null;
	public static BufferedImage pause = null;
	public static BufferedImage gameover = null;
	public static BufferedImage start = null;
	public static BufferedImage bullet = null;
	// ��̬����飬���ڼ���ͼƬ
	static {
		try {
			// ��ȡ�����أ�����ͼƬ
			// ͨ�����·����ȡͼƬ
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
	Hero hero = new Hero();// ��Ϸ�е�Ӣ�ۻ�
	// �ö��ӵ�
	List<Bullet> bullets = new ArrayList<Bullet>();
	// �ö������
	List<FlyingObject> flyings = new ArrayList<FlyingObject>();
	// Override ��д���໭������
	public void paint(Graphics g) {
		super.paint(g);// ���ø���paint����
		// ������(����)
		g.drawImage(background, 0, 0, null);
		// ������
		// �½������������, �Ӵ�, 20
		Font font = new Font("����", Font.BOLD, 20);
		g.setFont(font);// ��������
		g.setColor(Color.red);// ������ɫ
		g.drawString("SCORE: "+score, 10, 25);
		g.drawString("LIFE: "+hero.life, 10, 55);
		// ��Ӣ��
		g.drawImage(hero.image, hero.x, hero.y, null);
		// ��������л�+�۷䣩
		drawFlyings(g);
		// ���ӵ�
		drawBullets(g);
		if(state == START){
			// ��ʼ
			g.drawImage(start, 0, 0, null);
		}
		if(state == PAUSE){
			// ��ͣ
			g.drawImage(pause, 0, 0, null);
		}
		if(state == OVER) {
			// ����
			g.drawImage(gameover, 0, 0, null);
		}
	}
	/**
	 * ʹ�������ڲ��ഴ������������
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
			 * �õ��������
			 * ����������꣬�ı�Ӣ������
			 */
			if(state == RUN) {
				int mouseX = e.getX();// ���X����
				int mouseY = e.getY();// ���Y����
				hero.x = mouseX - hero.width / 2 ;
				hero.y = mouseY - hero.height / 2;
			}
		}
	};
	/** �������� */
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
	/** ��ʼ��Ϸ */
	Timer timer ;
	public void action() {
		// ��ʱ��
		timer = new Timer();
		// �ƶ�ʱ���
		timer.schedule(new TimerTask(){
			// һֱ�������У��˶������䡢��������롢�ж���ײ���жϽ���
			public void run() {
				if(state == RUN) {
					stepAction();// ����һ���˶�����
					shootAction();// ���
					enterFlyingAction();// ���������
					outOfBoundsAction();// �ж�Խ��
					bangAction();// �ж���ײ(�ӵ�)
					checkGameOver();// �ж���Ϸ����
				}
				repaint();// ˢ��->����paint����
			}
		}, 10, 10);
		// this����ǰ�������
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
	}
	public void checkGameOver() {
		// �ж���Ϸ�Ƿ����
		if(gameOver()){
			state = OVER;
		}
	}
	/** �ж���Ϸ�������������� */
	public boolean gameOver() {
		
		
		Iterator<FlyingObject> it = flyings.iterator();
		while(it.hasNext()) {
			FlyingObject flyings = it.next();
			if(hero.hit(flyings)){ //�ж�Ӣ�ۻ��Ƿ������ײ����
				it.remove();
				hero.subtractLife();   //Ӣ�ۻ�����
				hero.setDoubleFire(0); //Ӣ�ۻ�����ֵ����	
				}
			}
			return(hero.getLife()<=0);//Ӣ�ۻ�����С�ڵ���0����Ϊ��Ϸ����
	

//		return false;
	
	}
		
		
		
		/*
		 * 1��ʱ�����ж�Ӣ����û�б���ײ
		 *      hero.isHit(flyObject);
		 *      �㷨��ʾ���ѵл��̶�������Ӣ�۶�
		 *             �ж�Ӣ�����ĵ�ķ�Χ
		 * 2�������ײ������ֵ-1
		 * 3���ж�����ֵ�Ƿ�<=0
		 * 4�����<=0 return true
		 */
		

	/** �ж���ײ(�ӵ�) */
	public void bangAction() {
		/*
		 * �ж�ÿһ���ӵ�
		 * ����ӵ��͵л���ײ����ô�Ӽ������Ƴ��ӵ��͵л�
		 * ����.remove();-> ѭ���д���remove��һ��Ҫ�õ�����
		 */
		Iterator<Bullet> it = bullets.iterator();
		while(it.hasNext()) {
			Bullet bullet = it.next();
			/*
			 *  �жϷɻ���û�б�bullet����
			 *  ������У����Ƴ�bullet
			 */
			if(bang(bullet)){
				it.remove();
			}
		}
	}
	/** �ж�ÿһ����������û�б���ǰ�ӵ����� */
	public boolean bang(Bullet bullet) {
		Iterator<FlyingObject> it = flyings.iterator();
		while(it.hasNext()) {
			FlyingObject fly = it.next();
			/*
			 * �жϷ����ﱻײ��
			 * fly �� bullet �ж�
			 */
			if(fly instanceof Enemy) {// �ж�fly�ǲ���Enemy���Ͷ���
				Enemy e = (Enemy) fly;
				if(e.shootBy(bullet)){// ���������
					// �ӷ�
					score += e.getScore();
					// �Ӽ������Ƴ�fly
					it.remove();
					return true;
				}
			}
			if(fly instanceof Bee) {
				Bee b = (Bee) fly;
				if(b.shootBy(bullet)){
					// ����
					/*
					 * �ж��۷佱������������
					 * 1�������DOUBLE_FIRE ˫������
					 *      ����Ӣ���Դ�����
					 * 2�������LIFE ������ֵ
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
	/** �ж�Խ�� */
	public void outOfBoundsAction() {
		/*
		 * �ж϶����ӵ�(bullets)���۷䡢�л�(flyings)
		 * ���Խ�磬���Ӽ������Ƴ�
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
	/** ��������� */
	int flyIndex = 0;
	public void enterFlyingAction() {
		flyIndex ++;
		// ÿ��300�������һ��������
		if(flyIndex % 30 == 0) {
			// �������0:Bee  1~19:Enemy
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
		// ��ֹ���(��Ŀ�����)
		flyIndex = (flyIndex == 1000000000 ? 0:flyIndex);
	}
	/** ��� */
	int shootIndex = 0;
	public void shootAction() {
		shootIndex ++;
		/*
		 * 1. �����µ��ӵ�(�ӵ��Ĺ�����)
		 *     �ӵ���x��y���꣬����heroΪ�������
		 * 2. ���µ��ӵ����뵽bullets
		 * 3. ���ӵ�(paint������)
		 * 4. ���ӵ���
		 */
		// ÿ400�������һ���ӵ�
		if(shootIndex % 40 == 0){
			/*
			 * һ���ڵ������������ڵ�
			 */
			Bullet[] bs = hero.shoot();
			for(int i = 0; i < bs.length; i++){
				bullets.add(bs[i]);
			}
		}
		// ��ֹ���(��Ŀ�����)
		shootIndex = (shootIndex == 1000000000 ? 0:shootIndex);
	}
	// �˶�
	public void stepAction() {
		hero.step();
		// �ӵ���
		for (Bullet bullet : bullets) {
			bullet.step();
		}
		// �����ﶯ
		for (FlyingObject flyObject : flyings) {
			flyObject.step();
		}
	}
	int score = 0;// ����
	public static void main(String[] args) {
		// ���� С�� = new ����();
		JFrame window = new JFrame();
		window.setSize(400, 654);// ���ô�С
		// ����Ĭ�Ϲر�ѡ��
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		Shoot shoot = new Shoot();// ����-paint-����
		window.add(shoot);
//		window.show();// ��ʾ����
		window.setVisible(true);
		shoot.action();// ��ʼ��Ϸ
	}
}
