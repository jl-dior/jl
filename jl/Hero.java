package com.jl;

import java.awt.image.BufferedImage;

/** Ӣ�� */
public class Hero extends FlyingObject {
	int life;// Ӣ������ֵ
	// ˫������������40���ӵ�
	int double_fire = 0;// ˫������
	/** ��������������ʼ������ */
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
	/** ��ȡ�� */
	public int getLife(){
		return life;
	}	
	/** ���� */
	public void subtractLife(){
		life--;
	}
	
	/** ������ֵ */
	public void addDoubleFire(){
		double_fire += 40;
	}
	/** ���û���ֵ */
	public void setDoubleFire(int doubleFire){
		this.double_fire = doubleFire;
	}
	
	public boolean hit(FlyingObject other){
	int x1 = other.x-this.width/2; //x1:���˵�x-1/2Ӣ�ۻ��Ŀ�
	int x2 = other.x+other.width+this.width/2; //x2:���˵�x+���˵Ŀ�+1/2Ӣ�ۻ��Ŀ�
	int y1 = other.y-this.height/2; //y1:���˵�y-1/2Ӣ�ۻ��ĸ�
	int y2 = other.y+other.height+this.height/2; //y2:���˵�y+���˵ĸ�+1/2Ӣ�ۻ��ĸ�
	int hx = this.x+this.width/2; //hx:Ӣ�ۻ���x+1/2Ӣ�ۻ��Ŀ�
	int hy = this.y+this.height/2; //hy:Ӣ�ۻ���y+1/2Ӣ�ۻ��ĸ�
	
	return hx>x1 && hx<x2
		   &&
		   hy>y1 && hy<y2; //hx��x1��x2֮�䣬���ң�hy��y1��y2֮�䣬��Ϊײ����
}
	
	
	
	
	
	
	/** �����ķ����ӵ� */
	public Bullet[] shoot() {// �����ķ����ӵ�
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
	// ʵ�ֵĸ�����󷽷�
	public void step() {
		step ++;
		// 0%2 = 0  1%2 = 1  2%2 = 0 3%2 = 1   
		// 0 1-9 10 11~19 20 21~29 30
		// 1/10 
		image = images[step/10%2];
		// ��ֹ���(��Ŀ�����)
		step = (step == 1000000000 ? 0:step);
	}
	@Override
	public boolean isOutOfBounds() {
		// Ӣ�ۣ���Զ��Խ��
		return false;
	}
}
/** ���� */
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
	/** �ж��Ƿ��ӵ�����
	 * ���ӵ������һ����(��Ϊ�ӵ�̫С�����Ժ������)
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
/** �۷� */
class Bee extends FlyingObject {
	public static final int DOUBLE_FIRE = 0;
	public static final int LIFE = 1;
	int award;// ���影������
	public Bee() {
		image = Shoot.bee;
		width = image.getWidth();
		height = image.getHeight();
		// Math.random() [0,1) * 400
		x = (int) (Math.random() * (400-width));
		y = -height;
		award = (int) (Math.random() * 2);// �漴һ����������
	}
	public void step() {
		y+=2;// �ӿ��ƶ��ٶ�
	}
	@Override
	public boolean isOutOfBounds() {
		return y >= Shoot.HEIGHT;
	}
	/** �ж��Ƿ��ӵ�����*/
	public boolean shootBy(Bullet bullet) {
		int bX = bullet.x;
		int bY = bullet.y;
		return bX > this.x 
				&& bX < this.x + this.width
				&& bY > this.y
				&& bY < this.y + this.height;
	}
}
/** �ӵ� */
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





