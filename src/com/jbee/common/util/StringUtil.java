package com.jbee.common.util;

import java.util.ArrayList;

public class StringUtil {
	
	/**
	 * 공백, Tab문자들을 하나의 공백으로 줄인 후 리턴
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
	 * 공백이나 탭문자로 띄워진 단어들을 분리하여 넘겨줌.
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
