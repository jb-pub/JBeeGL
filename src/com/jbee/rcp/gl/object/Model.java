package com.jbee.rcp.gl.object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;

import com.jbee.common.util.StringUtil;

/**
 * 3D Object 모델
 * @author 서정봉
 *
 */
public class Model extends MObject{
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  변수
	
	/**
	 * 모델의 고유 키
	 */
	private String id;
	
	/**
	 * 모델에 대한 설명
	 */
	private String describe;
		
	////////////////////////////////////////////////////////

	/**
	 * 3D Object 점들의 배열
	 */
	private ArrayList<Vertex> m_vertices;
	
	/**
	 * 3D Object 평면들의 배열
	 */
	private ArrayList<Face> m_faces;
	
	////////////////////////////////////////////////////////

	/**
	 * Object의 Rotation정보
	 */
	private Matrix rotate = null;
	
	/**
	 * rotate X,Y,Z의 값
	 */
	private double rX,rY,rZ;
	
	/**
	 * Object의 Transformation 정보
	 */
	private Matrix trans = null;
	
	/**
	 * Object의 Zoom정보
	 */
	private double zoom = 1.0;
	
	/**
	 * Object의 Color
	 */
	private double intR;
	private double intG;
	private double intB;
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  생성자
	
	public Model(){
		intR = 1.0;
		intG = 1.0;
		intB = 1.0;
	}
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  Getter & Setter
	
	public double getintR() {
		return intR;
	}
	public double getintG() {
		return intG;
	}
	public double getintB() {
		return intB;
	}
	
	public Color getColor(){
		return new Color(null, (int)(255*intR),(int)(255*intG),(int)(255*intB));
	}

	public void setColor(double r, double g, double b) {
		intR = r;
		intG = g;
		intB = b;
	}

	public void setVertices(ArrayList<Vertex> m_vertices) {
		this.m_vertices = m_vertices;
	}

	public void setFaces(ArrayList<Face> m_faces) {
		this.m_faces = m_faces;
	}
	
	/**
	 * model의 회전에 필요한 Matrix를 생성함.
	 *  - 전달인자는 0~360사이의 값
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotate(double x, double y, double z){
		rX = x;
		rY = y;
		rZ = z;
		if(x==0&&y==0&&z==0){
			rotate = null;
		}
		else{
			double cos, sin, seta;
			// x축 회전
			Matrix rX = new Matrix();
			seta = Math.PI*x/180;
			cos = Math.cos(seta);
			sin = Math.sin(seta);
			rX.setValue(1,1, cos);
			rX.setValue(1, 2, -sin);
			rX.setValue(2, 1, sin);
			rX.setValue(2, 2, cos);
			
			// y축 회전
			Matrix rY = new Matrix();
			seta = Math.PI*y/180;
			cos = Math.cos(seta);
			sin = Math.sin(seta);
			rY.setValue(0,0, cos);
			rY.setValue(0, 2, sin);
			rY.setValue(2, 0, -sin);
			rY.setValue(2, 2, cos);
			
			// z축 회전
			Matrix rZ = new Matrix();
			seta = Math.PI*z/180;
			cos = Math.cos(seta);
			sin = Math.sin(seta);
			rZ.setValue(0,0, cos);
			rZ.setValue(0, 1, -sin);
			rZ.setValue(1, 0, sin);
			rZ.setValue(1, 1, cos);
			// 3방면의 회전을 종합하여 저장
			rotate = rX.x(rY.x(rZ));			
		}
	}
	
	/**
	 * 회전각 얻기 (vertex객체 안에 x,y,z 형태)
	 * @return
	 */
	public Vertex getRotateVertex(){
		while(rX<0) rX += 360.0;
		while(rY<0) rY += 360.0;
		while(rZ<0) rZ += 360.0;
		while(rX>=360) rX -= 360.0;
		while(rY>=360) rY -= 360.0;
		while(rZ>=360) rZ -= 360.0;
		return new Vertex(rX,rY,rZ);
	}
	
	/**
	 * model의 이동에 필요한 Matrix를 생성함.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTrans(double x, double y, double z){
		if(x==0&&y==0&&z==0){
			trans = null;
		}
		else{
			trans = new Matrix();
			trans.setValue(0, 3, x);
			trans.setValue(1, 3, y);
			trans.setValue(2, 3, z);			
		}
	}
	
	public void setZoom(double z){
		zoom = z;
	}
	
	public Matrix getRotate(){
		return rotate;
	}
	
	public Matrix getTrans(){
		if(trans==null) return new Matrix();
		return trans;		
	}
	
	public double getZoom(){
		return zoom;    
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  점, 면을 얻음.

	/**
	 * 모든 점을 얻음 -> 필요한 연산이 있는 경우 연산함.
	 */
	public ArrayList<Vertex> getVertices(Matrix eye) {
		boolean matrixUsed = false;
		Matrix all = new Matrix();
		
		// 시점이동을 마지막으로
		if(eye!=null){
			all = all.x(eye);
			matrixUsed = true;
		}
		// 이동을 세번째
		if(trans!=null){
			all = all.x(trans);
			matrixUsed = true;
		}
		// 회전을 두번째
		if(rotate!=null){
			all = all.x(rotate);
			matrixUsed = true;
		}
		// 줌을 제일 처음
		if(zoom!=1.0){
			all = all.x(new Matrix(zoom));
			matrixUsed = true;
		}
		
		// Matrix가 사용되었다면 모든 vertex에 적용
		if(matrixUsed){
			Vertex v = null;
			ArrayList<Vertex>  newArray = new ArrayList<Vertex>();
			for(int i=0;i<m_vertices.size();i++){
				v = all.apply(m_vertices.get(i));
				v.x /= v.h;
				v.y /= v.h;
				v.z /= v.h;
				newArray.add(v);
			}		
			return newArray;
		}
		// Matrix가 사용되지 않았으면 그냥 복사하여 리턴
		else{
			return m_vertices;
		}
	}

	/**
	 * 모든 면 구하기
	 * @return
	 */
	public ArrayList<Face> getFaces() {
		return m_faces;
	}
	
	/**
	 * 점의 갯수
	 * @return
	 */
	public int getNofVertex(){
		if(m_vertices!=null) return m_vertices.size();
		else return 0;
	}
	
	/**
	 * 면의 갯수
	 * @return
	 */
	public int getNofFace(){
		if(m_faces!=null) return m_faces.size();
		else return 0;
	}

	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  File로부터 model을 가져옴.
	
	public static Model parse(File file) throws FileNotFoundException, IOException{
		// 파일 읽기에 사용될 객체
		FileInputStream is = new FileInputStream(file);
		return parse(file.getName(), is);
	}

	public static Model parse(String name, InputStream is) throws FileNotFoundException, IOException{
		InputStreamReader sr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		
		// 리턴할 Model객체
		Model model = new Model();
		
		// vertex 구하기
		line = br.readLine();
		int size = Integer.parseInt(line.split("= ")[1]);
		ArrayList<Vertex> vList = new ArrayList<Vertex>(size);
		Vertex vertex = null;
		String [] sep;
		// 물체의 중심 이동시키기 위해 모든 점의 평균을 구하기 위한 변수
		double xSum = 0, ySum = 0, zSum = 0;
		for(int i=0;i<size;i++){
			sep = StringUtil.splitBySpace(br.readLine());
			vertex = new Vertex(Double.parseDouble(sep[0]),Double.parseDouble(sep[1]),Double.parseDouble(sep[2]));
			xSum += vertex.x;
			ySum += vertex.y;
			zSum += vertex.z;
			vList.add(vertex);
		}
		
		// 물체의 중심이 0,0,0 점이 되도록 이동시킴.
		xSum /= (double) size;
		ySum /= (double) size;
		zSum /= (double) size;
		for(int i=0;i<size;i++){
			vertex = vList.get(i);
			vertex.x -= xSum;
			vertex.y -= ySum;
			vertex.z -= zSum;
		}
		
		// Face 구하기
		line = br.readLine();
		size = Integer.parseInt(line.split("= ")[1]);
		ArrayList<Face> fList = new ArrayList<Face>(size);
		Face face = null;
		for(int i=0;i<size;i++){
			sep = StringUtil.splitBySpace(br.readLine());
			face = new Face(Integer.parseInt(sep[0]),Integer.parseInt(sep[1]),Integer.parseInt(sep[2]));
			fList.add(face);
		}
		
		br.close();
		sr.close();
		is.close();
		
		model.setVertices(vList);
		model.setFaces(fList);
		model.setId(name);
		
		return model;
	}

	// for testing	
	public static void main(String [] args){
		File file = new File("data/teapot.dat");
		try {
			Model model = parse(file);
			model.getClass();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
