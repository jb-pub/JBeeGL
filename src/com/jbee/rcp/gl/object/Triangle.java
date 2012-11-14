package com.jbee.rcp.gl.object;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

/**
 * Gouraud 삼각형 면을 그리는데 사용됨.
 * @author 서정봉
 *
 */
public class Triangle {
	
	/**
	 * 점을 저장하는 곳
	 */
	private Vertex [] vertex;
	
	/**
	 * 각 점의 Color를 저장 -> Gouraud에서 사용됨
	 */
	private double [] vIntensity;
	
	private class Compare{
		public double xPos;
		public double zPos;
		public double intensity;
		public String toString(){
			return "X="+(int)xPos+" ["+(int)(intensity*100)+"] ";
		}
	}
	
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	///
	///   Setter
	
	public void setVertex(Vertex[] vertex) {
		this.vertex = vertex;
	}
	public void setVIntensity(double[] intensity) {
		vIntensity = intensity;
	}
	
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	///
	///   Draw 함수

	public void Draw(GC memGC, int center_x, int center_y, double [][] zBuffer){
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		
		for(int i=0;i<3;i++){
			if(vertex[i].y<minY) minY = vertex[i].y;
			if(vertex[i].y>maxY) maxY = vertex[i].y;
		}
		
		double iS = 0, iE = 0;
		double zS = 0, zE = 0;
		double xS = 0, xE = 0;
		double distance, ratio, intensity, z;
		int color;
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
						iS = result[i].intensity;
					}
					if(xE<result[i].xPos){
						xE = result[i].xPos;
						zE = result[i].zPos;
						iE = result[i].intensity;				
					}
				}
			}
			
			distance = xE-xS;
						
			for(int x=(int)xS;x<=(int)xE;x++){
				try {
					ratio = ( x-xS ) / distance;
					intensity = (iE * ratio + iS * (1-ratio));
					z = zE * ratio + zS * (1-ratio);
					if(intensity>1) intensity = 1;
					if(intensity<0) intensity = 0;
					color = (int) (255.0*intensity);
					memGC.setForeground(new Color(null, color, color, color));
					if(zBuffer[center_y-y][center_x+x]<z){
						zBuffer[center_y-y][center_x+x] = z;
						memGC.drawPoint(center_x+x, center_y-y);
					}					
				} catch (Exception e) {}
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
		c.intensity = vIntensity[eIndex]*ratio + vIntensity[sIndex]*(1-ratio);
		c.zPos = vertex[eIndex].z*ratio + vertex[sIndex].z*(1-ratio);
		return c;
	}
	
}
