package com.jbee.rcp.gl.object;

/**
 * 3D Object������ �� ���� ǥ����.
 * @author ������
 *
 */
public class Vertex extends MObject{
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	// ����

	/**
	 * ���� X��ǥ
	 */
	public double x;
	
	/**
	 * ���� Y��ǥ
	 */
	public double y;

	/**
	 * ���� Z��ǥ
	 */
	public double z;
	
	/**
	 * ���� H��ǥ
	 */
	public double h;
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  ������

	public Vertex(){
	}
	
	public Vertex(double x, double y, double z){
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = 1.0;
	}
	
	public Vertex(double x, double y, double z, double h){
		this(x,y,z);
		this.h = h;
	}

	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  ��Ģ ���� (+,-,*)
	
	/**
	 * �� Vertex�� �Է� Vertex�� ���� vertex�� ������.
	 * @param vertex
	 * @return
	 */
	public Vertex add(Vertex vertex)
	{
		return new Vertex(x + vertex.x, y + vertex.y, z + vertex.z);
	}

	/**
	 * �� Vertex�� �Է� Vertex�� �� vertex�� ������.
	 * @param vertex
	 * @return
	 */
	public Vertex sub(Vertex vertex)
	{
		return new Vertex(x - vertex.x, y - vertex.y, z - vertex.z);
	}

	/**
	 * �� Vertex�� x,y,z�� ���� number��ŭ ������ ���� ������.
	 * @param number
	 * @return
	 */
	public Vertex multiple(double number)
	{
		return new Vertex(x * number, y * number, z * number);
	}
	
	/**
	 * �� Vertex�� x,y,z�� ���� number��ŭ ���� ���� ������.
	 * @param number
	 * @return
	 */
	public Vertex divide(double number)
	{
		return new Vertex(x / number, y / number, z / number);
	}
	
	/**
	 * �ڱ� �ڽſ� �׳� ����
	 * @param vertex
	 */
	public Vertex addMyself(Vertex vertex){
		x+= vertex.x;
		y+= vertex.y;
		z+= vertex.z;
		return this;
	}
	
	/**
	 * �� Vertex�� x,y,z�� ���� number��ŭ ������ ���� ������.
	 * @param number
	 * @return
	 */
	public Vertex multipleMyself(double number)
	{
		x *= number;
		y *= number;
		z *= number;
		return this;
	}

	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  ��Ÿ �Լ�	
	
	/**
	 * Normalization 
	 *  - �������ͷ� ����
	 */
	public Vertex Normalization()
	{
		double dis = Length();
		if(dis!=0){
			x /= dis;
			y /= dis;
			z /= dis;
		}
		return this;
	}

	/**
	 * ������ ����.
	 * @param v
	 * @return
	 */
	public Vertex CrossProduct(Vertex v)
	{
		return new Vertex(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x);
	}

	/**
	 * ������ ����.
	 * @param v
	 * @return
	 */
	public double DotProduct(Vertex v)
	{
		return x*v.x + y*v.y + z*v.z;
	}

	/**
	 * �������� �Ÿ��� ����.
	 * @return
	 */
	public double Length()
	{
		return Math.sqrt(x*x + y*y + z*z);
	}

	public String toString(){
		return "x="+x+" , y="+y+" , z="+z;
	}
}
