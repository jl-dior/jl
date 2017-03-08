package com.jl;

import java.awt.image.BufferedImage;

/** 英雄 */
public class Hero extends FlyingObject {
	int life;// 英雄生命值
	// 双倍火力奖励是40发子弹
	int double_fire = 0;// 双倍火力
	/** 构造器，用来初始化属性 */
	public Hero() {
		life = 3;
		x = 150;
		y = 400;
		image = Shoot.hero0;
		width = image.getWidth();
		height = image.getHeight();
	}
	public void addLife(){
		life++;
	}	
	/** 获取命 */
	public int getLife(){
		return life;
	}	
	/** 减命 */
	public void subtractLife(){
		life--;
	}
	
	/** 增火力值 */
	public void addDoubleFire(){
		double_fire += 40;
	}
	/** 设置火力值 */
	public void setDoubleFire(int doubleFire){
		this.double_fire = doubleFire;
	}
	
	public boolean hit(FlyingObject other){
	int x1 = other.x-this.width/2; //x1:敌人的x-1/2英雄机的宽
	int x2 = other.x+other.width+this.width/2; //x2:敌人的x+敌人的宽+1/2英雄机的宽
	int y1 = other.y-this.height/2; //y1:敌人的y-1/2英雄机的高
	int y2 = other.y+other.height+this.height/2; //y2:敌人的y+敌人的高+1/2英雄机的高
	int hx = this.x+this.width/2; //hx:英雄机的x+1/2英雄机的宽
	int hy = this.y+this.height/2; //hy:英雄机的y+1/2英雄机的高
	
	return hx>x1 && hx<x2
		   &&
		   hy>y1 && hy<y2; //hx在x1和x2之间，并且，hy在y1和y2之间，即为撞上了
}
	
	
	
	
	
	
	/** 真正的发射子弹 */
	public Bullet[] shoot() {// 真正的发射子弹
		Bullet[] bullets ;
		if(double_fire > 0){
			bullets = new Bullet[2];
			bullets[0] = new Bullet(this.x + this.width / 4, this.y);
			bullets[1] = new Bullet(this.x + this.width * 3/4,this.y);
			double_fire -= 2;
		} else  {
			bullets = new Bullet[1];
			bullets[0] = new Bullet(this.x + this.width/2, this.y);
		}
		return bullets;
	}
	BufferedImage[] images = {Shoot.hero0, Shoot.hero1};
	int step = 0;
	// 实现的父类抽象方法
	public void step() {
		step ++;
		// 0%2 = 0  1%2 = 1  2%2 = 0 3%2 = 1   
		// 0 1-9 10 11~19 20 21~29 30
		// 1/10 
		image = images[step/10%2];
		// 防止溢出(三目运算符)
		step = (step == 1000000000 ? 0:step);
	}
	@Override
	public boolean isOutOfBounds() {
		// 英雄，永远不越界
		return false;
	}
}
/** 敌人 */
class Enemy extends FlyingObject {
	private int score;
	public Enemy() {
		image = Shoot.airplane;
		width = image.getWidth();
		height = image.getHeight();
		// Math.random() [0,1) * 400
		x = (int) (Math.random() * (400-width));
		y = -height;
		score = 5;
	}
	public int getScore() {
		return score;
	}
	public void step() {
		y+=2;
	}
	@Override
	public boolean isOutOfBounds() {
		return y >= Shoot.HEIGHT;
	}
	/** 判断是否被子弹击落
	 * 把子弹想象成一个点(因为子弹太小，可以忽略误差)
	 * */
	public boolean shootBy(Bullet bullet) {
		int bX = bullet.x;
		int bY = bullet.y;
		return bX > this.x 
				&& bX < this.x + this.width
				&& bY > this.y
				&& bY < this.y + this.height;
	}
}
/** 蜜蜂 */
class Bee extends FlyingObject {
	public static final int DOUBLE_FIRE = 0;
	public static final int LIFE = 1;
	int award;// 定义奖励类型
	public Bee() {
		image = Shoot.bee;
		width = image.getWidth();
		height = image.getHeight();
		// Math.random() [0,1) * 400
		x = (int) (Math.random() * (400-width));
		y = -height;
		award = (int) (Math.random() * 2);// 随即一个奖励类型
	}
	public void step() {
		y+=2;// 加快移动速度
	}
	@Override
	public boolean isOutOfBounds() {
		return y >= Shoot.HEIGHT;
	}
	/** 判断是否被子弹击落*/
	public boolean shootBy(Bullet bullet) {
		int bX = bullet.x;
		int bY = bullet.y;
		return bX > this.x 
				&& bX < this.x + this.width
				&& bY > this.y
				&& bY < this.y + this.height;
	}
}
/** 子弹 */
class Bullet extends FlyingObject {
	public Bullet(int x, int y) {
		image = Shoot.bullet;
		this.x = x;
		this.y = y;
		width = image.getWidth();
		height = image.getHeight();
	}
	public void step() {
		this.y-=3;
	}
	@Override
	public boolean isOutOfBounds() {
		return y <= -height;
	}
}





