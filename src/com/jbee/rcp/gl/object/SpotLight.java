package com.jbee.rcp.gl.object;

import org.eclipse.swt.graphics.Color;

/**
 * 점 광원
 * @author 서정봉
 *
 */
public class SpotLight extends Vertex{
	
	/**
	 * 광원의 Intensity
	 * 0~1사이의 값을 가지게 됨.
	 */
	private double intensity;
	
	/**
	 * 광원 id
	 */
	private String id;
	
	/**
	 * 생성자
	 *
	 */
	public SpotLight(){
		super();
		intensity = 1.0;
		visible = true;
	}

	/**
	 * 생성자
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
	 * light 이동에 필요한 Matrix를 생성함.
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
	 * 한 점의 이 광원에 대해서의 Intensity를 구한다.
	 * @param eye 시점
	 * @param vertex 색을 구하고자 하는 점
	 * @param nVector 해당 점의 노멀백터 
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

		// Diffuse Reflection을 구함
		diffuse =nVector.DotProduct(LightVector);

		// Specular Reflection을 구함
		specular = Math.pow(nVector.DotProduct(HalfwayVector), 20);

		if(diffuse<0 || specular<0)
			specular=0;
		else if(specular>1)
			specular=1;		

		if(diffuse<0)
			diffuse=0;
		else if(diffuse>1)
			diffuse=1;

		return intensity * (Kd*diffuse + Ks*specular + Ka); // intensity = 0~1 사이의 값
	}	
	
	public void setZoom(double z){
		if(z>1) z=1;
		intensity = z;
	}
	
	public double getZoom(){
		return intensity;
	}

}
