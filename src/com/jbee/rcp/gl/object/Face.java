package com.jbee.rcp.gl.object;

import java.util.ArrayList;

/**
 * 3D Object�� �� ���(�ﰢ��)�� ��Ÿ��.
 * @author ������
 *
 */
public class Face {

	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  ���� 

	/**
	 * ����� ��Ÿ���� �� - 3�� 
	 */
	private int [] m_vertexNum;
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	//
	//  ������
	
	/**
	 * 3���� ���� ��ȣ�� �Է¹޾� ������.
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
	 * �� ���� i vertex�� ��ȣ�� �������� Ȯ��
	 * @param i
	 * @return
	 */
	public int getVNum(int i){
		if(i<0||i>2) return Integer.MIN_VALUE;
		else return m_vertexNum[i];
	}
	
	/**
	 * �� ���� Normal���͸� ����.
	 * @param m_vertices
	 * @return
	 */
	public Vertex getNormalVector(ArrayList<Vertex> m_vertices){
		return null;
	}
}
