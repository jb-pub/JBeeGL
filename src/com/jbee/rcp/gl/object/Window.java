package com.jbee.rcp.gl.object;

import java.io.File;
import java.util.ArrayList;

import data.Dumy;

/**
 * 모델들의 집합체
 * @author 서정봉
 *
 */
public class Window {
	
	/**
	 * 광원들 (일단 하나 -_-)
	 */
	private ArrayList<SpotLight> m_light;
	
	/**
	 * Model리스트를 저장하는 객체
	 */
	private ArrayList<Model> orgModels;
	
	/**
	 * shading에 필요한 z버퍼
	 */
	public double [][] zBuffer;
	
	/**
	 * 윈도우 생성자
	 *  - 일단 두개의 객체를 
	 *
	 */
	public Window(){
		// 광원 추가
		m_light = new ArrayList<SpotLight>();
		SpotLight light = new SpotLight();
		light.x = 1000;
		light.y = 1000;
		light.z = 1000;
		light.setId("Light #1");
		m_light.add(light);
		light = new SpotLight();
		light.x = 1000;
		light.y = 1000;
		light.z = -1000;
		light.setId("Light #2");
		light.setIntensity(0.5);
		m_light.add(light);
		light = new SpotLight();
		light.x = -1000;
		light.y = -1000;
		light.z = 1000;
		light.setId("Light #3");
		light.setIntensity(0.3);
		m_light.add(light);
		
		// 모델 추가
		orgModels = new ArrayList<Model>();
		try {
			
			// teapot
			Model model = Model.parse("teapot",new Dumy().getClass().getResourceAsStream("teapot.dat"));
			model.setId("teapot.dat");
			model.setVisible(true);
			model.setZoom(1);
			model.setTrans(0,0,0);
			model.setColor(0, 0, 1);
			orgModels.add(model);
			
			///////
			///////
			///////
//			DataSet dataSet = new DataSet();
//			dataSet.loadfile("data/wine/wine-train.txt", DataSet.DATASET);
//			dataSet.insertToModel(orgModels, 0, 1, 2);
			///////
			///////
			///////
			
			// sphere
//			file = new File("data/sphere.dat");
//			model = Model.parse(file);
//			model.setId("sphere.dat");
//			model.setVisible(true);
//			model.setTrans(-150, 120, -50);
//			model.setZoom(0.2);
//			orgModels.add(model);
			
			// 내 학번 넣기
//			drawMySN(0,50,-200,0.5);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 내 학번 쓰기 =_=;;;;;;
	 * @param xStart
	 * @param yStart
	 * @param zStart
	 * @param zoom
	 * @throws Exception
	 */
	public void drawMySN(int xStart, int yStart, int zStart, double zoom) throws Exception{
		if(orgModels==null) orgModels = new ArrayList<Model>();
		File file = new File("data/two.dat");
		Model model = Model.parse(file);
		model.setId("2");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart-200*zoom, yStart+50*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);

		file = new File("data/zero.dat");
		model = Model.parse(file);
		model.setId("0");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart-150*zoom, yStart+50*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);

		file = new File("data/zero.dat");
		model = Model.parse(file);
		model.setId("0");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart-95*zoom, yStart+50*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);
		
		file = new File("data/three.dat");
		model = Model.parse(file);
		model.setId("3");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart-35*zoom, yStart+60*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);
		
		file = new File("data/zero.dat");
		model = Model.parse(file);
		model.setId("0");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart+20*zoom, yStart+50*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);
		
		file = new File("data/nine.dat");
		model = Model.parse(file);
		model.setId("9");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart+77*zoom, yStart+66*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);

		file = new File("data/nine.dat");
		model = Model.parse(file);
		model.setId("9");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart+136*zoom, yStart+66*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);

		file = new File("data/nine.dat");
		model = Model.parse(file);
		model.setId("9");
		model.setVisible(true);
		model.setRotate(180, 0, 0);
		model.setTrans(xStart+195*zoom, yStart+66*zoom, zStart);
		model.setZoom(0.5*zoom);
		orgModels.add(model);
	}
	
	/**
	 * 윈도우 안에 포함된  모델이 몇개인지
	 * @return
	 */
	public int getNofModel(){
		if(orgModels==null) return 0;
		return orgModels.size();
	}
	
	/**
	 * 모델 객체 얻기
	 * @param i
	 * @return
	 */
	public Model getModel(int i){
		return orgModels.get(i);
	}

	/**
	 * 광원 리스트 얻기
	 * @return
	 */
	public ArrayList<SpotLight> getLights(){
		return m_light;
	}
	
	/**
	 * 3D 모델 제거하기
	 * @param model
	 */
	public void removeModel(Model model){
		orgModels.remove(model);
	}
	
	/**
	 * 3D 모델 삽입하기
	 * @param model
	 */
	public void addModel(Model model){
		orgModels.add(model);
	}
	
	/**
	 * z버퍼 생성하기 (사이즈 변경)
	 * @param width
	 * @param height
	 */
	public void setZBufferSize(int width, int height){
		zBuffer = new double[height][width];
	}
	
	public void initZBuffer(){
		if(zBuffer!=null){
			int height = zBuffer.length;
			int width = zBuffer[0].length;
			for(int i=0;i<height;i++){
				for(int j=0;j<width;j++){
					zBuffer[i][j] = -9999999;
				}
			}		
		}
	}
}
