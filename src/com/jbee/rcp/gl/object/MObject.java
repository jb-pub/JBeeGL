package com.jbee.rcp.gl.object;

public class MObject {
	/**
	 * 보일지 말지 결정
	 */
	protected boolean visible = true;
	
	public String getId(){
		return "";
	}
	
	public void setId(String id){
		
	}
	
	public Vertex getRotateVertex(){
		return new Vertex(0,0,0);
	}

	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Matrix getTrans(){
		return new Matrix();		
	}
	
	public void setTrans(double x, double y, double z){
		
	}

	public Matrix getRotate(){
		return new Matrix();		
	}
	
	public void setRotate(double x, double y, double z){
		
	}

	public void setZoom(double z){
		
	}
	
	public double getZoom(){
		return 1.0;
	}

}
