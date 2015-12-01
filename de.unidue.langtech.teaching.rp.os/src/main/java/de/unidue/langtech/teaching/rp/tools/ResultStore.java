package de.unidue.langtech.teaching.rp.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import dnl.utils.text.table.TextTable;

/**
 * Implements the ResultStore and its methods.
 * @author Onur
 *
 */
public class ResultStore {
	

	public void saveResults(List<File> toolFiles, String corpusName) throws IOException {
		
		
	List <String> temp = FileUtils.readLines(toolFiles.get(0));
	int rowLength = temp.size();
		

   	String[] columnNames = new String[toolFiles.size()+2];
   	String [][] information = new String[rowLength][toolFiles.size()+2]; 

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
           
       	
       	String taggerName = toolFiles.get(i).getName().replace(corpusName, "").replace(".txt", "");
           
           columnNames[0] = "Text";
           columnNames[1] = "GoldLang";
           columnNames[i+2] = taggerName;  
           
           //fill arrays with information         
           for(int posRow=0;posRow < tweets.size(); posRow++){
        	   
               for (int posCol=0; posCol < toolFiles.size()+1; posCol++){
               information[posRow][0] = tweets.get(posRow);
               information[posRow][1] = goldLang.get(posRow);
               information[posRow][i+2] = detectLang.get(posRow);
               }
           }
   		}    
   	
   			corpusName = corpusName.replace("-", "");
   	
   			File file = new File("D:/_Projekt_Korpora/" + corpusName + ".txt");
   			PrintStream ps = new PrintStream(file);
   	
   			TextTable tt = new TextTable(columnNames, information); 
   			tt.setAddRowNumbering(true);
   			tt.printTable(ps, 0);
   	

	}

}
