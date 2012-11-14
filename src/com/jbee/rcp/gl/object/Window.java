package com.jbee.rcp.gl.object;

import java.io.File;
import java.util.ArrayList;

import data.Dumy;

/**
 * �𵨵��� ����ü
 * @author ������
 *
 */
public class Window {
	
	/**
	 * ������ (�ϴ� �ϳ� -_-)
	 */
	private ArrayList<SpotLight> m_light;
	
	/**
	 * Model����Ʈ�� �����ϴ� ��ü
	 */
	private ArrayList<Model> orgModels;
	
	/**
	 * shading�� �ʿ��� z����
	 */
	public double [][] zBuffer;
	
	/**
	 * ������ ������
	 *  - �ϴ� �ΰ��� ��ü�� 
	 *
	 */
	public Window(){
		// ���� �߰�
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
		
		// �� �߰�
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
			
			// �� �й� �ֱ�
//			drawMySN(0,50,-200,0.5);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * �� �й� ���� =_=;;;;;;
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
	 * ������ �ȿ� ���Ե�  ���� �����
	 * @return
	 */
	public int getNofModel(){
		if(orgModels==null) return 0;
		return orgModels.size();
	}
	
	/**
	 * �� ��ü ���
	 * @param i
	 * @return
	 */
	public Model getModel(int i){
		return orgModels.get(i);
	}

	/**
	 * ���� ����Ʈ ���
	 * @return
	 */
	public ArrayList<SpotLight> getLights(){
		return m_light;
	}
	
	/**
	 * 3D �� �����ϱ�
	 * @param model
	 */
	public void removeModel(Model model){
		orgModels.remove(model);
	}
	
	/**
	 * 3D �� �����ϱ�
	 * @param model
	 */
	public void addModel(Model model){
		orgModels.add(model);
	}
	
	/**
	 * z���� �����ϱ� (������ ����)
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
