package com.jbee.rcp.gl.object;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSet {
	public static final int DATASET = 0;
	public static final int TESTSET = 1;
	public static final int ANSWER = 2;
	
//	private int type;
	private double[][] array;
	private String[] answer;
	private HashMap<String,Integer> group;
	private HashMap<Integer,String> index;
	
	private int maxFeat;
	private int row;
	
	public DataSet()
	{
		array = null;
		answer = null;
		group = null;
		index = null;
		maxFeat = -1;
		row = -1;
	}
	
	//////////////////////////////////////////////////////////////////////
	// Functions

	public void loadfile( String filename, int t )
	{
		File file = new File( filename );
		FileReader fileReader = null;
		
		try{
			fileReader = new FileReader(file);			
		}
		catch ( FileNotFoundException e ){
//			e.printStackTrace(System.err);
			System.out.println("file not found >> "+filename+"\n");
			return;
		}
		
		
		try
		{
			StreamTokenizer token = new StreamTokenizer(fileReader);
			int attrSize = -1;
			
			// row 정보
			token.nextToken();
			row = (int)token.nval;
			
			// maxFeat 정보
			switch(t){
			case DATASET:
				token.nextToken();
				maxFeat = ((int)token.nval)-1;
				attrSize = maxFeat+1;
				break;
			case TESTSET:
				token.nextToken();
				maxFeat = ((int)token.nval);
				attrSize = maxFeat;
				break;
			case ANSWER:
				token.nextToken();
				maxFeat = -1;
				break;
			}
			
			if(maxFeat>0) array = new double[row][maxFeat];
			answer = new String[row];
			
			for ( int k=0; k < row; k++)
			{
				if(maxFeat>0){
					for ( int m=0; m < attrSize; m++)
					{
						if ( m != maxFeat )
						{
							token.nextToken();
							array[k][m] = token.nval;
						}
						else
						{
							token.nextToken();
							answer[k] = token.sval;
						}
					}
				}
				else{
					token.nextToken();
					answer[k] = token.sval;
				}
			}
//			type = t;
			
			if(t==DATASET){
				group = new HashMap<String,Integer>();
				index = new HashMap<Integer,String>();
				int count = 0;
				for(int i=0;i<answer.length;i++){
					if(group.get(answer[i])==null){
						group.put(answer[i], new Integer(count));
						index.put(new Integer(count), answer[i]);
						count++;
					}
				}
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	// Getters

	public double[][] getArray() {
		return array;
	}
	
	public void setAnswer(String [] answer){
		this.answer = answer;
	}
	
	public int getNumOfGroup(){
		if(group==null) return -1;
		else return group.size();
	}

	public String[] getAnswer() {
		return answer;
	}
	
	public int getIndexFromAnswer(String key){
		if(group==null) return -1;
		else return group.get(key);
	}
	
	public String getAnswerFromIndex(int key){
		if(index==null) return null;
		else return index.get(new Integer(key));
	}

	public int getMaxFeat() {
		return maxFeat;
	}

	public int getRowCount() {
		return row;
	}
	
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	
	public void insertToModel(ArrayList<Model> list, int sx, int sy, int sz) throws Exception{
		// 사용할 변수 초기화
		double [] arrayX = new double[row];
		double [] arrayY = new double[row];
		double [] arrayZ = new double[row];
		
		// Normalization
		double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
		double zMin = Double.MAX_VALUE, zMax = Double.MIN_VALUE;
		double temp;
		for(int n=0;n<row;n++){
			temp = array[n][sx];
			if(temp<xMin) xMin = temp;
			if(temp>xMax) xMax = temp;
			temp = array[n][sy];
			if(temp<yMin) yMin = temp;
			if(temp>yMax) yMax = temp;
			temp = array[n][sz];
			if(temp<zMin) zMin = temp;
			if(temp>zMax) zMax = temp;
		}
		
//		int padding = 5;
		int width = 400;
		double xRatio = width/(xMax-xMin);
		double yRatio = width/(yMax-yMin);
		double zRatio = width/(zMax-zMin);
		
		for(int n=0;n<row;n++){
			arrayX[n] = (array[n][sx]-xMin)*xRatio-width/2;
			arrayY[n] = (array[n][sy]-yMin)*yRatio-width/2;
			arrayZ[n] = (array[n][sz]-zMin)*zRatio-width/2;
		}

		// model로 추가해줌.
		for(int i=0;i<row;i++){
			File file = new File("data/cube.dat");
			Model model = Model.parse(file);
			model.setId("teapot.dat");
			model.setVisible(true);
			model.setZoom(0.02);
			model.setTrans(arrayX[i], arrayY[i], arrayZ[i]);
			int group = getIndexFromAnswer(answer[i]);
			
			switch(group){
			case 0:
				model.setColor(0.5, 0.5, 1);				
				break;
			case 1:
				model.setColor(0.5, 1, 0.5);
				break;
			case 2:
				model.setColor(1, 0.5, 0.5);
				break;
			case 3:
				model.setColor(1, 1, 0.5);
				break;
			case 5:
				model.setColor(0.5, 1, 1);
				break;
			case 6:
				model.setColor(1, 0.5, 1);
				break;
			default:
				break;
			}
			
			list.add(model);
		}
	}
}
