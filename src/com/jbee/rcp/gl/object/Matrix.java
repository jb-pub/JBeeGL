package com.jbee.rcp.gl.object;

/**
 * 3D Model연산에 필요한 행열
 * @author 서정봉
 *
 */
public class Matrix {
	
	/**
	 * 3D Vertex 계산에 필요한 4x4 Matri 숫자 저장소
	 */
	public double [] m;
	
	/**
	 * 생성자 - 단위 행렬로 생성
	 */
	public Matrix(){
		m = new double[16];
		m[0] = 1;
		m[5] = 1;
		m[10] = 1;
		m[15] = 1;
	}
	
	/**
	 * 생성자 - zoom 행렬로 생성
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
	 * 각 행의 값 Setting
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
	 * 한 포인트의 값 Setting
	 * @param i 행(0~3)
	 * @param j 열(0~3)
	 * @param v
	 */
	public void setValue(int i, int j, double v){
		m[4*i+j] = v;
	}

	/**
	 * Matrix끼리 곱해서 새로운 Matrix를 리터해줌.
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
	 * 이 Matrix에 Vertex적용하여 새로운 Vertex를 리턴해줌.
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
	 * 이 Matrix에 SpotLight적용하여 새로운 SpotLight를 리턴해줌.
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
	 * 테스팅을 위해 값 보여주기
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
