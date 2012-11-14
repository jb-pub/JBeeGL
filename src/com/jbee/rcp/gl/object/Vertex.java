package com.jbee.rcp.gl.object;

/**
 * 3D Object에서의 한 점을 표현함.
 * @author 서정봉
 *
 */
public class Vertex extends MObject{
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	// 변수

	/**
	 * 점의 X좌표
	 */
	public double x;
	
	/**
	 * 점의 Y좌표
	 */
	public double y;

	/**
	 * 점의 Z좌표
	 */
	public double z;
	
	/**
	 * 점의 H좌표
	 */
	public double h;
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  생성자

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
	//  사칙 연산 (+,-,*)
	
	/**
	 * 이 Vertex에 입력 Vertex를 합한 vertex를 리턴함.
	 * @param vertex
	 * @return
	 */
	public Vertex add(Vertex vertex)
	{
		return new Vertex(x + vertex.x, y + vertex.y, z + vertex.z);
	}

	/**
	 * 이 Vertex에 입력 Vertex를 뺀 vertex를 리턴함.
	 * @param vertex
	 * @return
	 */
	public Vertex sub(Vertex vertex)
	{
		return new Vertex(x - vertex.x, y - vertex.y, z - vertex.z);
	}

	/**
	 * 이 Vertex의 x,y,z에 각각 number만큼 곱해준 값을 리턴함.
	 * @param number
	 * @return
	 */
	public Vertex multiple(double number)
	{
		return new Vertex(x * number, y * number, z * number);
	}
	
	/**
	 * 이 Vertex의 x,y,z에 각각 number만큼 나눈 값을 리턴함.
	 * @param number
	 * @return
	 */
	public Vertex divide(double number)
	{
		return new Vertex(x / number, y / number, z / number);
	}
	
	/**
	 * 자기 자신에 그냥 더함
	 * @param vertex
	 */
	public Vertex addMyself(Vertex vertex){
		x+= vertex.x;
		y+= vertex.y;
		z+= vertex.z;
		return this;
	}
	
	/**
	 * 이 Vertex의 x,y,z에 각각 number만큼 곱해준 값을 리턴함.
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
	//  기타 함수	
	
	/**
	 * Normalization 
	 *  - 단위벡터로 만듬
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
	 * 외적을 구함.
	 * @param v
	 * @return
	 */
	public Vertex CrossProduct(Vertex v)
	{
		return new Vertex(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x);
	}

	/**
	 * 내적을 구함.
	 * @param v
	 * @return
	 */
	public double DotProduct(Vertex v)
	{
		return x*v.x + y*v.y + z*v.z;
	}

	/**
	 * 원점과의 거리를 구함.
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
