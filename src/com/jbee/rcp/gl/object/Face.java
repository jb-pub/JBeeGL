package com.jbee.rcp.gl.object;

import java.util.ArrayList;

/**
 * 3D Object의 한 평면(삼각형)을 나타냄.
 * @author 서정봉
 *
 */
public class Face {

	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  변수 

	/**
	 * 평면을 나타내는 점 - 3개 
	 */
	private int [] m_vertexNum;
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  생성자
	
	/**
	 * 3개의 점의 번호를 입력받아 생성함.
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	public Face(int v1, int v2, int v3){
		m_vertexNum = new int[3];
		m_vertexNum[0] = v1;
		m_vertexNum[1] = v2;
		m_vertexNum[2] = v3;
	}
	
	/**
	 * 이 면의 i vertex의 번호가 무엇인지 확인
	 * @param i
	 * @return
	 */
	public int getVNum(int i){
		if(i<0||i>2) return Integer.MIN_VALUE;
		else return m_vertexNum[i];
	}
	
	/**
	 * 이 면의 Normal백터를 구함.
	 * @param m_vertices
	 * @return
	 */
	public Vertex getNormalVector(ArrayList<Vertex> m_vertices){
		return null;
	}
}
