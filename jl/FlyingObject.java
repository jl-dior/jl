package com.jl;
import java.awt.image.BufferedImage;

/** ������ �������з�����̳У�*/
public abstract class FlyingObject {
	int x; // x����
	int y; // y����
	BufferedImage image;// ����ͼƬ�����ࣩ
	int width;// ��
	int height;// ��
	/** �ƶ�����(��֪�����ʵ�֣�����) */
	public abstract void step();
	/** �ж��Ƿ�Խ�� */
	public abstract boolean isOutOfBounds();
	
	
	
}
