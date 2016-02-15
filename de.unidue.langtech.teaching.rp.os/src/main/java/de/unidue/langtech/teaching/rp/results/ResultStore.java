package de.unidue.langtech.teaching.rp.results;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import dnl.utils.text.table.TextTable;


public class ResultStore 
{
	
	public static void saveResults(List<File> toolFiles, String corpusName) 
			throws IOException 
	{
		
	List <String> temp = FileUtils.readLines(toolFiles.get(0));
	int rowLength = temp.size();
		

   	String[] columnNames = new String[toolFiles.size()+3];
   	String [][] information = new String[rowLength][toolFiles.size()+3]; 

   	for (int i = 0; i < toolFiles.size(); i++) {
		
   		   List <String> toolInformation = FileUtils.readLines(toolFiles.get(i));
           
           List<String> tweets = new ArrayList<String>();
           List<String> goldLang = new ArrayList<String>();
           List<String> detectLang = new ArrayList<String>();
           
           for (String toolInfo : toolInformation) {
        	   
        	   String[] parts = toolInfo.split("\t");
        	   tweets.add(parts[0]);
        	   goldLang.add(parts[1]);
        	   detectLang.add(parts[2]);
        	   
           }
           
       	
       	String toolName = toolFiles.get(i).getName().replace(corpusName, "").replace(".txt", "").replace("_results", "");
           
           columnNames[0] = "Text";
           columnNames[1] = "False?";
           columnNames[2] = "GoldLang";
           columnNames[i+3] = toolName;  
           
           //fill arrays with information         
           for(int row=0;row < tweets.size(); row++){
        	   
               for (int col=0; col < toolFiles.size()+1; col++){
            	   
               information[row][0] = tweets.get(row);
               information[row][2] = goldLang.get(row);
               information[row][i+3] = detectLang.get(row);
               
               }
           }
   		} 
   	
	   	for (int i=0;i<information.length;i++) {
	   		
   	   	    boolean equal = true;
   	   	    
   	   	    for (int j=3;j<information[i].length;j++) {
   	   	    	
   	   	        if (!information[i][2].equals(information[i][j]) && information[i][j] != null) {
   	   	            equal = false;
   	   	            break;
   	   	        }
   	   	        
   	   	    }
   	   	    
   	   	    if (!equal) {
   	   	    	information[i][1] = "x";
   	   	    }
   	   	}
	   	
   	
   			corpusName = corpusName.replace("-", "");
   	
   			File file = new File("D:/_Projekt_Korpora/" + corpusName + "_results.txt");
   			PrintStream ps = new PrintStream(file);
   	
   			TextTable tt = new TextTable(columnNames, information); 
   			tt.setAddRowNumbering(true);
   			
   				
   			tt.printTable(ps, 0);
   				

	}
	
	public static void saveScores(List<File> scoreFiles, List<File> timeFiles, String corpusName) 
			throws IOException 
	{
		
	List <String> temp = FileUtils.readLines(scoreFiles.get(0));
	int rowLength = temp.size();
		

   	String[] columnNames = new String[scoreFiles.size()+1];
   	String [][] information = new String[rowLength+1][scoreFiles.size()+1]; 

   	for (int i = 0; i < scoreFiles.size(); i++) {
		
   		   List <String> toolInformation = FileUtils.readLines(scoreFiles.get(i));
           
           List<String> languages = new ArrayList<String>();
           List<String> scores = new ArrayList<String>();
           
           //dump line for notation
           
           languages.add("");
           scores.add("ACCURACY__PRECISION__RECALL");
           
           for (String toolInfo : toolInformation) {
        	   String[] parts = toolInfo.split("\t");
        	   languages.add(parts[0]);
        	   scores.add(parts[1]);
           }
           
       	String toolScoreName = scoreFiles.get(i).getName().replace(corpusName, "").replace(".txt", "").replace("_scores", "");;
           
           columnNames[0] = "Language";
           columnNames[i+1] = toolScoreName;  
           
           //fill arrays with information         
           for(int row=0;row < languages.size(); row++){
               information[row][0] = languages.get(row);
               information[row][i+1] = scores.get(row);
           }
   		}    
   		
   	
   			File file = new File("D:/_Projekt_Korpora/" + corpusName.replace("-", "") + "_scores.txt");
   			PrintStream ps = new PrintStream(file);
   	
   			TextTable tt = new TextTable(columnNames, information); 
   			tt.printTable(ps, 0);
   			
	        List<String> times = new ArrayList<String>();
   			
   		   	for (int i = 0; i < timeFiles.size(); i++) {
   		   		
   	            List<String> timeInformation = FileUtils.readLines(timeFiles.get(i));
   	        	String toolTimeName = timeFiles.get(i).getName().replace(corpusName, "").replace(".txt", "").replace("_times", "");
   	        	
   	           for (String timeInfo : timeInformation) {
   	        	   
   	        	   if (timeInfo.startsWith("sum")) {
   	        		   String[] parts = timeInfo.split("=");
   	        		   String timeString = parts[1];
   	        		   double time = Double.parseDouble(timeString);
   	        		   
   	        		   //source: http://alvinalexander.com/java/java-decimalformat-example-numberformat
   	        		   DecimalFormat tf = new DecimalFormat("#.##");
   	        		   times.add(tf.format(time));
   	        	   }
   	        	   
   	           }
   	           
   	        FileUtils.writeStringToFile(file, "\nTime for " + toolTimeName + ": " + times.get(i) + " s", true);
   	              		
   		   	}   	  	

	}

}
