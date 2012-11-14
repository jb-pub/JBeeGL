package com.jbee.common.util;

import java.util.ArrayList;

public class StringUtil {
	
	/**
	 * ����, Tab���ڵ��� �ϳ��� �������� ���� �� ����
	 * @param org
	 * @return
	 */
	public static String trim(String org){
		StringBuffer buf = new StringBuffer(org.length());
		char c;
		boolean space = true;
		for(int i=0;i<org.length();i++){
			c = org.charAt(i);
			if(c=='\t'||c==' '){
				if(space){
					buf.append(' ');
					space=false;
				}
				else continue;
			}
			else{
				buf.append(c);
				space=true;
			}
		}
		return buf.toString();
	}
	
	/**
	 * �����̳� �ǹ��ڷ� ����� �ܾ���� �и��Ͽ� �Ѱ���.
	 * @param org
	 * @return
	 */
	public static String [] splitBySpace(String org){
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer buf = new StringBuffer(org.length());
		char c;
		boolean space = true;
		for(int i=0;i<org.length();i++){
			c = org.charAt(i);
			if(c=='\t'||c==' '){
				if(space && buf.length()!=0){
					list.add(buf.toString());
					buf.setLength(0);
				}
				else continue;
				space=false;
			}
			else{
				buf.append(c);
				space=true;
			}
		}
		if(buf.length()!=0){
			list.add(buf.toString());
		}
		String [] strs = new String[list.size()];
		list.toArray(strs);
		return strs;
		
	}
}
