package com.jbee.rcp.gl.object;

import org.eclipse.swt.graphics.Color;

/**
 * �� ����
 * @author ������
 *
 */
public class SpotLight extends Vertex{
	
	/**
	 * ������ Intensity
	 * 0~1������ ���� ������ ��.
	 */
	private double intensity;
	
	/**
	 * ���� id
	 */
	private String id;
	
	/**
	 * ������
	 *
	 */
	public SpotLight(){
		super();
		intensity = 1.0;
		visible = true;
	}

	/**
	 * ������
	 *
	 */
	public SpotLight(double x, double y, double z, double h, double intensity, boolean isVisible){
		super(x,y,z,h);
		this.intensity = intensity;
		visible = isVisible;
	}
	
	public String getId(){
		if(id==null) return "Light";
		else return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}
	
	public Color getColor(){
		int color = (int) ( 255.0 * intensity);
		return new Color(null, color,color,color);
	}

	/**
	 * light �̵��� �ʿ��� Matrix�� ������.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTrans(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Matrix getTrans(){
		Matrix matrix = new Matrix();
		matrix.m[3] = x;
		matrix.m[7] = y;
		matrix.m[11] = z;
		return matrix;
	}
	
	/**
	 * �� ���� �� ������ ���ؼ��� Intensity�� ���Ѵ�.
	 * @param eye ����
	 * @param vertex ���� ���ϰ��� �ϴ� ��
	 * @param nVector �ش� ���� ��ֹ��� 
	 * @return
	 */
	public double getPointsIntensity(Vertex eye, Vertex point, Vertex nVector){
		Vertex ViewVector, LightVector, HalfwayVector;
		double Ka = 0.01;
		double Kd = 0.7f;
		double Ks = 1.0f-Kd-Ka;
		double diffuse, specular;
		
		ViewVector = eye.sub(point);
		ViewVector.Normalization();

		LightVector = this.sub(point);
		LightVector.Normalization();
		
		HalfwayVector = LightVector.add(ViewVector).Normalization();

		// Diffuse Reflection�� ����
		diffuse =nVector.DotProduct(LightVector);

		// Specular Reflection�� ����
		specular = Math.pow(nVector.DotProduct(HalfwayVector), 20);

		if(diffuse<0 || specular<0)
			specular=0;
		else if(specular>1)
			specular=1;		

		if(diffuse<0)
			diffuse=0;
		else if(diffuse>1)
			diffuse=1;

		return intensity * (Kd*diffuse + Ks*specular + Ka); // intensity = 0~1 ������ ��
	}	
	
	public void setZoom(double z){
		if(z>1) z=1;
		intensity = z;
	}
	
	public double getZoom(){
		return intensity;
	}

}
