package com.jbee.rcp.gl.object;

/**
 * 3D Model���꿡 �ʿ��� �࿭
 * @author ������
 *
 */
public class Matrix {
	
	/**
	 * 3D Vertex ��꿡 �ʿ��� 4x4 Matri ���� �����
	 */
	public double [] m;
	
	/**
	 * ������ - ���� ��ķ� ����
	 */
	public Matrix(){
		m = new double[16];
		m[0] = 1;
		m[5] = 1;
		m[10] = 1;
		m[15] = 1;
	}
	
	/**
	 * ������ - zoom ��ķ� ����
	 */
	public Matrix(double zoom){
		m = new double[16];
		m[0] = zoom;
		m[5] = zoom;
		m[10] = zoom;
		m[15] = 1;
	}
	
	
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	
	/**
	 * �� ���� �� Setting
	 */
	public void set0(double x, double y, double z){
		m[0] = x;
		m[1] = y;
		m[2] = z;
	}
	public void set1(double x, double y, double z){
		m[4] = x;
		m[5] = y;
		m[6] = z;
	}
	public void set2(double x, double y, double z){
		m[8] = x;
		m[9] = y;
		m[10] = z;
	}
	
	/**
	 * �� ����Ʈ�� �� Setting
	 * @param i ��(0~3)
	 * @param j ��(0~3)
	 * @param v
	 */
	public void setValue(int i, int j, double v){
		m[4*i+j] = v;
	}

	/**
	 * Matrix���� ���ؼ� ���ο� Matrix�� ��������.
	 * @param b
	 * @return
	 */
	public Matrix x(Matrix b)
	{
		Matrix r = new Matrix();
		for (int i = 0; i < 16; i++)
		{
			int j = i / 4;
			int k = i % 4;
			
			r.m[i] = m[j*4] * b.m[k] +
				 m[j*4+1] * b.m[k+4] +
				 m[j*4+2] * b.m[k+8] +
				 m[j*4+3] * b.m[k+12];
		}
		return r;
	}

	/**
	 * �� Matrix�� Vertex�����Ͽ� ���ο� Vertex�� ��������.
	 * @param b
	 * @return
	 */
	public Vertex apply(Vertex b)
	{
		double x,y,z,h;
		x = m[0]*b.x + m[1]*b.y + m[2]*b.z + m[3]*b.h;
		y = m[4]*b.x + m[5]*b.y + m[6]*b.z + m[7]*b.h;
		z = m[8]*b.x + m[9]*b.y + m[10]*b.z + m[11]*b.h;
		h = m[12]*b.x + m[13]*b.y + m[14]*b.z + m[15]*b.h;
		return new Vertex(x,y,z,h);
	}
	
	/**
	 * �� Matrix�� SpotLight�����Ͽ� ���ο� SpotLight�� ��������.
	 * @param b
	 * @return
	 */
	public SpotLight apply(SpotLight b)
	{
		double x,y,z,h;
		x = m[0]*b.x + m[1]*b.y + m[2]*b.z + m[3]*b.h;
		y = m[4]*b.x + m[5]*b.y + m[6]*b.z + m[7]*b.h;
		z = m[8]*b.x + m[9]*b.y + m[10]*b.z + m[11]*b.h;
		h = m[12]*b.x + m[13]*b.y + m[14]*b.z + m[15]*b.h;
		return new SpotLight(x,y,z,h,b.getIntensity(), b.isVisible());
	}
	
	/**
	 * �׽����� ���� �� �����ֱ�
	 */
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<4;i++){
			buf.append("{\t");
			buf.append(m[4*i]).append("\t\t");			
			buf.append(m[4*i+1]).append("\t\t");			
			buf.append(m[4*i+2]).append("\t\t");			
			buf.append(m[4*i+3]).append("\t}\n");			
		}
		return buf.toString();
	}
}
