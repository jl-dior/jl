package com.jl;
import java.awt.image.BufferedImage;

/** 飞行物 （被所有飞行物继承）*/
public abstract class FlyingObject {
	int x; // x坐标
	int y; // y坐标
	BufferedImage image;// 自身图片（长相）
	int width;// 宽
	int height;// 高
	/** 移动方法(不知道如何实现，抽象化) */
	public abstract void step();
	/** 判断是否越界 */
	public abstract boolean isOutOfBounds();
	
	
	
}
