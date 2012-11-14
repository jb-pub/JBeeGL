package com.jbee.rcp.gl.engine;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.jbee.rcp.gl.object.Face;
import com.jbee.rcp.gl.object.Matrix;
import com.jbee.rcp.gl.object.Model;
import com.jbee.rcp.gl.object.SpotLight;
import com.jbee.rcp.gl.object.Triangle;
import com.jbee.rcp.gl.object.Triangle2;
import com.jbee.rcp.gl.object.Vertex;
import com.jbee.rcp.gl.object.Window;

/**
 * Model들을 이용하여 최총 화면에 뿌려지기 위한 2D데이터를 생성하는 곳 
 * @author 서정봉
 *
 */
public class Render {
	
	public static final Color color_X = new Color(null, 80,10,10);
	public static final Color color_Y = new Color(null, 10,80,10);
	public static final Color color_Z = new Color(null, 10,10,80);
	public static final Color color_W = new Color(null, 255,255,255);
	public static final Color color_B = new Color(null, 0,0,0);
	
	public static final int DRAW_AXIS = 0x0001;
	public static final int DRAW_TO_TOON = 0x0100;

	public static final int PARALLEL = 0x0010;
	public static final int PERSPECTIVE = 0x0020;
	
	public static final int MODE_WIREFRAME = 0x1000;
	public static final int MODE_FLAT = 0x2000;
	public static final int MODE_GOURAUD = 0x4000;
	public static final int MODE_PHONG = 0x8000;
	public static final int MODE_TOON = MODE_PHONG | DRAW_TO_TOON;
	
	public static final int DEFAULT_MODE = DRAW_AXIS | PERSPECTIVE | MODE_WIREFRAME;
	
	/**
	 * 시선(관점)의 위치
	 */
	private Vertex m_eye;
	
	/**
	 * 시선이 바라보는 곳 (일단 원점 -_-)
	 */
//	private Vertex m_lookAt = new Vertex(0,0,0);
	
	/**
	 * View up Vector
	 */
	private Vertex viewUpV;
	
	/**
	 * 축을 나타내는 Vertex
	 */
	private Vertex [] axis;
	
//	private Color [] vertexColors;
//	private Vertex [] vertexVectors;
	
	public Render(){
		m_eye =  new Vertex(306,249,306);
		viewUpV = new Vertex(0,1,0); 
		axis = new Vertex[6];
		int lineLength = 999999;
		axis[0] = new Vertex(lineLength,0,0);
		axis[1] = new Vertex(-lineLength,0,0);
		axis[2] = new Vertex(0,lineLength,0);
		axis[3] = new Vertex(0,-lineLength,0);
		axis[4] = new Vertex(0,0,lineLength);
		axis[5] = new Vertex(0,0,-lineLength);
	}
	
	/**
	 * Window에 속한 Model들을 Rendering하여 imgPanel에 그려줌
	 * @param imgPanel 그려줄 Image패널
	 * @param source 그려줄 Window 소스
	 * @param type Image를 어떻게 그려줄 것인지
	 */
	public void rendering(GC memGC, Window source, int width, int height, int mode) throws Exception{
		// 바탕 초기화
		if((mode&DRAW_TO_TOON)==0){
			memGC.setForeground(color_W);
			memGC.setBackground(color_B);
		}
		else{
			memGC.setForeground(color_B);
			memGC.setBackground(color_W);			
		}
		memGC.fillRectangle(new Rectangle(0,0,width,height));
		memGC.drawText("Eye x = "+(int)m_eye.x, 10,10);
		memGC.drawText("Eye y = "+(int)m_eye.y, 10,30);
		memGC.drawText("Eye z = "+(int)m_eye.z, 10,50);
		
		int center_x = width/2;
		int center_y = height/2;
		
		// UVN 메트릭 구하기
		Vertex n = new Vertex(m_eye.x,m_eye.y,m_eye.z);
		Vertex u = viewUpV.CrossProduct(n);
		n.Normalization();
		u.Normalization();
		Vertex v = n.CrossProduct(u);
		v.Normalization();
		
		// 시야의 방향 Matrix구하기
		Matrix eyeR = new Matrix();
		eyeR.set0(u.x, u.y, u.z);
		eyeR.set1(v.x, v.y, v.z);
		eyeR.set2(n.x, n.y, n.z);

		// x,y,z축 그리기
		if((mode & DRAW_AXIS)!=0) drawAxis(memGC, eyeR, center_x, center_y, (mode&DRAW_TO_TOON)!=0);

		// 시야 거리 Matrix구하여 시야 방향과 곱함
		Matrix distance = new Matrix(500/m_eye.Length());
		eyeR = eyeR.x(distance);
		
		// perspective 프로젝션일 때
		if( (mode&PERSPECTIVE) !=0 ){
			Matrix perspective = new Matrix();
			double zprp = 0.0;
			double zvp = 1000.0;
			double dp = zvp - zprp;
			
			perspective.setValue(3, 2, -1/dp);
			perspective.setValue(3, 3, zvp/dp);
			
			eyeR = perspective.x(eyeR);
		}
		
		// light소스 위치 수정
		ArrayList<SpotLight> orgLight, lights = new ArrayList<SpotLight>();
		orgLight = source.getLights();
		if(orgLight!=null){ 
			for(int i=0;i<orgLight.size();i++){
				lights.add((SpotLight)eyeR.apply(orgLight.get(i)));
			}
		}

		// wireframe이 아닌경우 zBuffer초기화
		if( (mode & MODE_WIREFRAME) ==0 ){
			source.initZBuffer();
		}

		//	wireframe 모드일 때
		if( (mode & MODE_WIREFRAME) !=0 ){
			// 사용할 변수
			Model model = null;
			ArrayList<Vertex> vertecies = null;
			ArrayList<Face> faces = null;

			// window에 포함된 모든 모델에 대하여 렌더링
			for(int i=0;i<source.getNofModel();i++){
				// 모델 하나 얻어옴
				model = source.getModel(i);
				
				// 보이지 않는 상태면 그리지 않음
				if(!model.isVisible()) continue;
				
				// 그릴 vertex와 face를 얻어옴
				vertecies = model.getVertices(eyeR);
				faces = model.getFaces();
			
				drawWireframe(memGC, vertecies, faces, center_x, center_y, model.getColor());
			}
		}
		else{
			// flat shading일 때
			if( (mode & MODE_FLAT) !=0 ){
				drawFlat(memGC, eyeR, lights, source, center_x, center_y);
			}
			// gouraud shading일 때
			else if( (mode & MODE_GOURAUD) !=0 ){
				drawGouraud(memGC, eyeR, lights, source, center_x, center_y);
			}
			// phong shading일 때
			else if( (mode & MODE_PHONG) !=0 ){
				drawPhong(memGC, eyeR, lights, source, center_x, center_y, (mode&DRAW_TO_TOON)!=0 );
			}
		}
		
		if((mode&DRAW_TO_TOON)!=0){
			int lineSize = 3;
			memGC.setForeground(color_B);
			for(int y=0;y<center_y*2;y++){
				for(int x=0;x<center_x*2;x++){
					if(source.zBuffer[y][x]==-9999999){
						boolean draw = false;
						for(int i=Math.max(0, y-lineSize);i<=Math.min(center_y*2-1, y+lineSize);i++){
							for(int j=Math.max(0, x-lineSize);j<=Math.min(center_x*2-1, x+lineSize);j++){
								if(source.zBuffer[i][j]!=-9999999){
									draw = true;
									break;
								}
							}
							if(draw) break;
						}
						if(draw) memGC.drawPoint(x, y);
					}
				}
			}
		}
		else{
			memGC.setForeground(memGC.getDevice().getSystemColor(SWT.COLOR_YELLOW));
		}
		
		// 광원 나타내줌
		drawLight(memGC, lights, center_x, center_y);
		
	}
	
	/**
	 * x,y,z축 그리기
	 * @param memGC
	 * @param eyeR
	 * @param center_x
	 * @param center_y
	 */
	private void drawAxis(GC memGC, Matrix eyeR, int center_x, int center_y, boolean isToon){
		Vertex [] rAxis = new Vertex[axis.length]; 
		for(int i=0;i<axis.length;i++){
			rAxis[i] = eyeR.apply(axis[i]);
		}
		// Line 그리기
		memGC.setForeground(color_X);
		memGC.drawLine(center_x+(int) rAxis[0].x, center_y-(int) rAxis[0].y, center_x+(int) rAxis[1].x, center_y-(int) rAxis[1].y);
		memGC.setForeground(color_Y);
		memGC.drawLine(center_x+(int) rAxis[2].x, center_y-(int) rAxis[2].y, center_x+(int) rAxis[3].x, center_y-(int) rAxis[3].y);
		memGC.setForeground(color_Z);
		memGC.drawLine(center_x+(int) rAxis[4].x, center_y-(int) rAxis[4].y, center_x+(int) rAxis[5].x, center_y-(int) rAxis[5].y);
		// x, y, z양의 방향 표시
		if(isToon)
			memGC.setForeground(color_B);
		else
			memGC.setForeground(color_W);
		Point pt = getPointOfLineMeetBox(rAxis[0].x, -rAxis[0].y, center_x, center_y, 15);
		memGC.drawText("+X", center_x+pt.x-5, center_y+pt.y-5, true);
		pt = getPointOfLineMeetBox(rAxis[4].x, -rAxis[4].y, center_x, center_y, 15);
		memGC.drawText("+Z", center_x+pt.x-5, center_y+pt.y-5, true);
		
		if(rAxis[2].y>10) memGC.drawText("+Y", center_x-5, 10, true);
	}
	
	/**
	 * -width~width,-height~height 안의 padding만큼의 bound와 원점~{x,y} 라인이 만나는 점을 찾아서 반환
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param width
	 * @param height
	 * @return
	 */
	private Point getPointOfLineMeetBox(double x, double y, int width, int height, int padding){
		int mY = 0;
		int bound_x = width-padding;
		int bound_y = height-padding;
		
		double r = y/x;
		
		// 1사분면
		if(x==0 && y==0){
			return new Point(99999,0);
		}
		else if(x>=0 && y>=0){
			mY = (int) ( r * (double)bound_x );
			if(mY <= bound_y)
				return new Point(bound_x,mY);
			else
				return new Point((int) ( (double)bound_y / r), bound_y);
		}
		// 2사분면
		else if(x>0 && y<0){
			mY = (int) ( r * (double)bound_x );
			if(mY >= -bound_y)
				return new Point(bound_x,mY);
			else
				return new Point((int) ( -(double)bound_y / r), -bound_y);			
		}
		// 3사분면
		else if(x<0 && y<0){
			mY = (int) ( r * (double)-bound_x );
			if(mY >= -bound_y)
				return new Point(-bound_x,mY);
			else
				return new Point((int) ( -(double)bound_y / r), -bound_y);			
		}
		// 4사분면
		else{
			mY = (int) ( r * (double)-bound_x );
			if(mY <= bound_y)
				return new Point(-bound_x,mY);
			else
				return new Point((int) ( (double)bound_y / r), bound_y);			
		}
		
	}
	
	/**
	 * 시점 지정하기
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setEyePoint(double x, double y, double z){
		m_eye.x = x;
		m_eye.y = y;
		m_eye.z = z;
	}
		
	/**
	 * Wireframe (쉐이딩없음)
	 * @param vertices
	 * @param faces
	 */
	private void drawWireframe(GC memGC, ArrayList<Vertex> vertecies, ArrayList<Face> faces, int center_x, int center_y, Color color){
		Face drawF = null;
		Vertex [] ve = new Vertex[3];
		int [] points = new int[8];
		memGC.setForeground(color); // memGC.getDevice().getSystemColor(SWT.COLOR_YELLOW));		
		for (int j=0; j<faces.size() ; j++){
			drawF = faces.get(j);
			ve[0] = vertecies.get(drawF.getVNum(0));
			ve[1] = vertecies.get(drawF.getVNum(1));
			ve[2] = vertecies.get(drawF.getVNum(2));
			// x,y 좌표를 중심점 기준으로 이동
			points[0] = center_x+(int) ve[0].x;
			points[1] = center_y-(int) ve[0].y;
			points[2] = center_x+(int) ve[1].x;
			points[3] = center_y-(int) ve[1].y;
			points[4] = center_x+(int) ve[2].x;
			points[5] = center_y-(int) ve[2].y;
			points[6] = points[0];
			points[7] = points[1];
			// 라인을 그림
			memGC.drawPolyline(points);
		}	
	}

	
	/**
	 * Flat 쉐이딩
	 */
	private void drawFlat(GC memGC, Matrix eyeR, ArrayList<SpotLight> lights, Window window, int center_x, int center_y){
		Face drawF = null;
		Vertex [] ve = new Vertex[3];
		Vertex n1, n2, normal, center;
		Vertex eye = eyeR.apply(m_eye);
		int [] points = new int[8];
		Color drawColor = null;
	
		Model model = null;
		for(int i=0;i<window.getNofModel();i++){
			model = window.getModel(i);
			if(!model.isVisible()) continue;	// 객체를 그리지 않는 모드이면 그리지 않음
			
			ArrayList<Vertex> drawVertecies = model.getVertices(eyeR);
			ArrayList<Face> faces = model.getFaces();

			for (int j=0; j<faces.size() ; j++){
				drawF = faces.get(j);
				ve[0] = drawVertecies.get(drawF.getVNum(0));
				ve[1] = drawVertecies.get(drawF.getVNum(1));
				ve[2] = drawVertecies.get(drawF.getVNum(2));
				
				// 면의 normal백터를 구함
				n1 = ve[1].sub(ve[0]);
				n2 = ve[2].sub(ve[0]);
				normal = n1.CrossProduct(n2);
				normal.Normalization();
				
				// normal백터의 z값이 0보다 작을 때는 시야에서 보이지 않는 면이므로 드로잉에서 제외 
				if(normal.z<0) continue;
				
				// 면의 중심점을 구함.
				center = new Vertex(ve[0].x+ve[1].x+ve[2].x, ve[0].y+ve[1].y+ve[2].y, ve[0].z+ve[1].z+ve[2].z).divide(3.0);
							
				// x,y 좌표를 중심점 기준으로 이동
				points[0] = center_x+(int) ve[0].x;
				points[1] = center_y-(int) ve[0].y;
				points[2] = center_x+(int) ve[1].x;
				points[3] = center_y-(int) ve[1].y;
				points[4] = center_x+(int) ve[2].x;
				points[5] = center_y-(int) ve[2].y;
				points[6] = points[0];
				points[7] = points[1];
				
				// 면의 color를 구하여 배경으로 셋팅
				drawColor = getColor(lights, center, normal, eye, model);
				memGC.setBackground(drawColor);

				// 면을 그림
				memGC.fillPolygon(points);
			}			

		}
		
	}
	
	/**
	 * Gouraud 쉐이딩
	 */
	private void drawGouraud(GC memGC, Matrix eyeR, ArrayList<SpotLight> lights, Window window, int center_x, int center_y){
		Face drawF = null;
		Vertex [] ve = new Vertex[3];
		Vertex n1, n2, normal;
		Vertex eye = eyeR.apply(m_eye);
		double [] veIntensity = new double[3];
		
		memGC.setForeground(color_W);
	
		Model model = null;
		Triangle tri = new Triangle();
		for(int i=0;i<window.getNofModel();i++){
			model = window.getModel(i);
			if(!model.isVisible()) continue;	// 객체를 그리지 않는 모드이면 그리지 않음
			
			ArrayList<Vertex> drawVertecies = model.getVertices(eyeR);
			ArrayList<Face> faces = model.getFaces();

			// 모든 vertex의 vector를 저장
			int vSize = drawVertecies.size();
			Vertex [] vertexVector = new Vertex[vSize];
			double [] vertexIntensity = new double[vSize];
			
			// 모든 면을 돌면서 모든 점의 Vector를 찾음.
			for (int j=0; j<faces.size() ; j++){
				drawF = faces.get(j);
				ve[0] = drawVertecies.get(drawF.getVNum(0));
				ve[1] = drawVertecies.get(drawF.getVNum(1));
				ve[2] = drawVertecies.get(drawF.getVNum(2));
				
				// 면의 normal백터를 구함
				n1 = ve[1].sub(ve[0]);
				n2 = ve[2].sub(ve[0]);
				normal = n1.CrossProduct(n2);
				normal.Normalization();
				
				int index = 0;
				for(int n=0;n<3;n++){
					index = drawF.getVNum(n);
					if(vertexVector[index]==null){
						vertexVector[index] = new Vertex().addMyself(normal);
					}
					else{
						vertexVector[index].addMyself(normal);
					}
				}
			}
			
			// 모든 vertex의 Vector노멀라이즈 & 각 점마다의 색을 구하여 찾음.
			for (int j=0;j<vSize;j++){
				vertexVector[j].Normalization();
				vertexIntensity[j] = getIntensity(lights, drawVertecies.get(j), vertexVector[j], eye);
			}

			for (int j=0; j<faces.size() ; j++){
				drawF = faces.get(j);
				ve[0] = drawVertecies.get(drawF.getVNum(0));
				ve[1] = drawVertecies.get(drawF.getVNum(1));
				ve[2] = drawVertecies.get(drawF.getVNum(2));
								
				// 면의 normal백터를 구함
				n1 = ve[1].sub(ve[0]);
				n2 = ve[2].sub(ve[0]);
				normal = n1.CrossProduct(n2);
				normal.Normalization();
				
				// normal백터의 z값이 0보다 작을 때는 시야에서 보이지 않는 면이므로 드로잉에서 제외 
				if(normal.z<=0) continue;
				
				veIntensity[0] = vertexIntensity[drawF.getVNum(0)];
				veIntensity[1] = vertexIntensity[drawF.getVNum(1)];
				veIntensity[2] = vertexIntensity[drawF.getVNum(2)];

				// 삼각형을 그리도록 함
				tri.setVertex(ve);
				tri.setVIntensity(veIntensity);
				tri.Draw(memGC, center_x, center_y, window.zBuffer);
			}
			
//			memGC.setForeground(color_W);
//			int color = 0;
//			// 모든 vertex의 Vector노멀라이즈 & 각 점마다의 색을 구하여 찾음.
//			for (int j=0;j<vSize;j++){
//				ve[0] = drawVertecies.get(j);
//				if(vertexVector[j].z>0){
//					color = (int) (255.0 * vertexIntensity[j]);
//					memGC.setBackground(new Color(null,color,color,color));
//					memGC.drawOval(center_x+(int)ve[0].x-1, center_y-(int)ve[0].y-1, 3,3);
//					memGC.fillOval(center_x+(int)ve[0].x+5, center_y-(int)ve[0].y+5, 10,10);
//				}
//			}

		}
	}
	
	
	/**
	 * Phong 쉐이딩
	 */
	private void drawPhong(GC memGC, Matrix eyeR, ArrayList<SpotLight> lights, Window window, int center_x, int center_y, boolean isToon){
		Face drawF = null;
		Vertex [] ve = new Vertex[3];
		Vertex n1, n2, normal;
		Vertex eye = eyeR.apply(m_eye);
		Vertex [] vVector = new Vertex[3];
		Triangle2 tri = new Triangle2();
	
		Model model = null;
		for(int i=0;i<window.getNofModel();i++){
			model = window.getModel(i);
			if(!model.isVisible()) continue;	// 객체를 그리지 않는 모드이면 그리지 않음
			
			ArrayList<Vertex> drawVertecies = model.getVertices(eyeR);
			ArrayList<Face> faces = model.getFaces();

			// 모든 vertex의 vector를 저장
			int vSize = drawVertecies.size();
			Vertex [] vertexVector = new Vertex[vSize];
			
			// 모든 면을 돌면서 모든 점의 Vector를 찾음.
			for (int j=0; j<faces.size() ; j++){
				drawF = faces.get(j);
				ve[0] = drawVertecies.get(drawF.getVNum(0));
				ve[1] = drawVertecies.get(drawF.getVNum(1));
				ve[2] = drawVertecies.get(drawF.getVNum(2));
				
				// 면의 normal백터를 구함
				n1 = ve[1].sub(ve[0]);
				n2 = ve[2].sub(ve[0]);
				normal = n1.CrossProduct(n2);
				normal.Normalization();
				
				int index = 0;
				for(int n=0;n<3;n++){
					index = drawF.getVNum(n);
					if(vertexVector[index]==null){
						vertexVector[index] = new Vertex().addMyself(normal);
					}
					else{
						vertexVector[index].addMyself(normal);
					}
				}
			}
			
			// 모든 vertex의 Vector노멀라이즈
			for (int j=0;j<vSize;j++){
				vertexVector[j].Normalization();
				//////////////////////////////////////////////////////
//				ve[0] = drawVertecies.get(j);
//				vertexVector[j].multipleMyself(15.0);
//				points[0] = center_x+(int) ve[0].x;
//				points[1] = center_y-(int) ve[0].y;
//				points[2] = points[0]+(int) vertexVector[j].x;
//				points[3] = points[1]-(int) vertexVector[j].y;
//				memGC.drawLine(points[0],points[1],points[2],points[3]);								
//				vertexVector[j].Normalization();
				//////////////////////////////////////////////////////
			}

			for (int j=0; j<faces.size() ; j++){
				drawF = faces.get(j);
				ve[0] = drawVertecies.get(drawF.getVNum(0));
				ve[1] = drawVertecies.get(drawF.getVNum(1));
				ve[2] = drawVertecies.get(drawF.getVNum(2));
								
				// 면의 normal백터를 구함
				n1 = ve[1].sub(ve[0]);
				n2 = ve[2].sub(ve[0]);
				normal = n1.CrossProduct(n2);
				normal.Normalization();
				
				// normal백터의 z값이 0보다 작을 때는 시야에서 보이지 않는 면이므로 드로잉에서 제외 
				if(normal.z<=0) continue;
				
				vVector[0] = vertexVector[drawF.getVNum(0)];
				vVector[1] = vertexVector[drawF.getVNum(1)];
				vVector[2] = vertexVector[drawF.getVNum(2)];

				// 삼각형을 그리도록 함
				tri.setVertex(ve);
				tri.setVVector(vVector);
				tri.Draw(memGC, center_x, center_y, window.zBuffer, lights, eye, isToon);
			}				

		}
	}

	/**
	 * 특정 점의 색 얻기
	 * @param lights
	 * @param point
	 * @param normal
	 * @param eye
	 * @return
	 */
	private Color getColor(ArrayList<SpotLight> lights, Vertex point, Vertex normal, Vertex eye, Model m){
		if(lights!=null){
			double intensity = 0;
			int size = lights.size();
			SpotLight light = null;
			for(int i=0;i<size;i++){
				light = lights.get(i);
				if(light.isVisible())	intensity += light.getPointsIntensity(eye, point, normal);
			}
			if(intensity>1) intensity = 1;
			else if(intensity<0) intensity = 0;
			int r = (int) ( 255.0 * intensity * m.getintR());
			int g = (int) ( 255.0 * intensity * m.getintG());
			int b = (int) ( 255.0 * intensity * m.getintB());
			return new Color(null, r,g,b);
		}
		else 
			return color_B;
	}
	
	/**
	 * 특정 점의 Intensity얻기
	 * @param lights
	 * @param point
	 * @param normal
	 * @param eye
	 * @return
	 */
	private double getIntensity(ArrayList<SpotLight> lights, Vertex point, Vertex normal, Vertex eye){
		if(lights!=null){
			double intensity = 0;
			int size = lights.size();
			SpotLight light = null;
			for(int i=0;i<size;i++){
				light = lights.get(i);
				if(light.isVisible())	intensity += light.getPointsIntensity(eye, point, normal);
			}
			if(intensity>1) return 1;
			else if(intensity<0) return 0;
			else return intensity;
		}
		else 
			return 0;
	}
	
	/**
	 * 광원을 그려줌
	 * @param memGC
	 * @param lights
	 * @param center_x
	 * @param center_y
	 */
	public void drawLight(GC memGC, ArrayList<SpotLight> lights, int center_x, int center_y){
		if(lights!=null){
			int size = lights.size();
			SpotLight light = null;
			for(int i=0;i<size;i++){
				light = lights.get(i);
				if(light.isVisible()){
					memGC.setBackground(light.getColor());
					memGC.fillOval(center_x+(int)light.x-5, center_y-(int)light.y-5, 10, 10);
					memGC.drawOval(center_x+(int)light.x-5, center_y-(int)light.y-5, 9, 9);
				}
			}
		}		
	}

}
