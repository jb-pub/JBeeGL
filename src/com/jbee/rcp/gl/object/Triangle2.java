package com.jbee.rcp.gl.object;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import com.jbee.rcp.gl.engine.Render;

/**
 * 삼각형 면을 그리는데 사용됨.
 * @author 서정봉
 *
 */
public class Triangle2 {
	
	/**
	 * 점을 저장하는 곳
	 */
	private Vertex [] vertex;
	
	/**
	 * 각 점의 Vector를 저장 -> Phong에서 사용됨
	 */
	private Vertex [] vVector;
	
	private class Compare{
		public double xPos;
		public double zPos;
		public Vertex nVector;
		public String toString(){
			return "X="+(int)xPos+" ["+nVector+"] ";
		}
	}
	
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	///
	///   Setter
	
	public void setVertex(Vertex[] vertex) {
		this.vertex = vertex;
	}
	public void setVVector(Vertex[] vector) {
		vVector = vector;
	}
	
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	///
	///   Draw 함수

	public void Draw(GC memGC, int center_x, int center_y, double [][] zBuffer, ArrayList<SpotLight> lights, Vertex eye, boolean isToon){
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		
		for(int i=0;i<3;i++){
			if(vertex[i].y<minY) minY = vertex[i].y;
			if(vertex[i].y>maxY) maxY = vertex[i].y;
		}
		
		Vertex nS = null, nE = null, normal;
		double zS = 0, zE = 0;
		double xS = 0, xE = 0;
		double distance, ratio, z;
		Color color;
		Compare [] result = new Compare[3];
		
		if(minY<=-center_y) minY = -center_y+1;
		if(maxY>=center_y) maxY = center_y-1;
		
		for(int y=(int)minY; y<=(int)maxY ; y++){
			result[0] = getCompare(y,0,1);
			result[1] = getCompare(y,1,2);
			result[2] = getCompare(y,0,2);
			
			xS = center_x-1;
			xE = -center_x+1;

			for(int i=0;i<3;i++){
				if(result[i]!=null){
					if(xS>result[i].xPos){
						xS = result[i].xPos;
						zS = result[i].zPos;
						nS = result[i].nVector;
					}
					if(xE<result[i].xPos){
						xE = result[i].xPos;
						zE = result[i].zPos;
						nE = result[i].nVector;;				
					}
				}
			}
			
			distance = xE-xS;
						
			for(double x=xS;x<=xE;x++){
				try{
					ratio = ( x-xS ) / distance;
					z = zE * ratio + zS * (1-ratio);
					normal = nE.multiple(ratio).addMyself(nS.multiple(1-ratio));
					normal.Normalization();
					
					if(zBuffer[center_y-y][center_x+(int)x]<z){
						zBuffer[center_y-y][center_x+(int)x] = z;
						
						if(isToon && normal.z<=0.2 && normal.z>=0){
							memGC.setBackground(memGC.getDevice().getSystemColor(SWT.COLOR_BLACK));
							memGC.fillOval(center_x+(int)x-1, center_y-(int)y-1, 3, 3);
						}
						else{
							color = getColor(lights, new Vertex(x,y,z), normal, eye, isToon);
							memGC.setForeground(color);
							memGC.drawPoint(center_x+(int)x, center_y-y);
						}
					}
				}catch(Exception e){}
			}
			
		}
	}
	
	/**
	 * 두 라인과 y좌표가 만나는지 확인하여 x좌표, 그 점의 intensity, 그 점의 z좌표를 넘겨줌 
	 * @param y
	 * @param sIndex
	 * @param eIndex
	 * @return
	 */
	private Compare getCompare(double y, int sIndex, int eIndex){
		Compare c = new Compare();
		Vertex s = vertex[sIndex];
		Vertex e = vertex[eIndex];
		double ratio = 0;
		if(s.y==e.y) return null;
		if(s.x==e.x){
			c.xPos = s.x;
			if(y>Math.max(s.y, e.y) || y<Math.min(s.y, e.y)) c = null;
		}
		else{
			double r = (e.y - s.y) / (e.x - s.x);
			double m = e.y-(r*e.x);
			c.xPos =  (y-m)/r;
			if(c.xPos<Math.min(s.x, e.x) || c.xPos>Math.max(s.x, e.x)) c = null;
		}
		if(c==null) return null;
		ratio = ( y-s.y ) / ( e.y-s.y );
		c.nVector = vVector[eIndex].multiple(ratio).addMyself(vVector[sIndex].multiple(1-ratio));
		c.zPos = vertex[eIndex].z*ratio + vertex[sIndex].z*(1-ratio);
		return c;
	}
	
	/**
	 * 특정 점의 색 얻기
	 * @param lights
	 * @param point
	 * @param normal
	 * @param eye
	 * @return
	 */
	private Color getColor(ArrayList<SpotLight> lights, Vertex point, Vertex normal, Vertex eye, boolean isToon){
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
			
			if(isToon){
				if(intensity<0.4){
					intensity=0.4;
				}
				else if(intensity<0.7){
					intensity=0.7;
				}
				else{
					intensity = 1;
				}				
				int c = (int) ( 255.0 * intensity);
				return new Color(null, c, 0, 0);
			}
			else{
				int c = (int) ( 255.0 * intensity);
				return new Color(null, c, c, c);				
			}
		}
		else 
			return Render.color_B;
	}
}
