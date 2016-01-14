package de.unidue.langtech.teaching.rp.old;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Web1TMinimizer {

	public static void main(String[] args) throws IOException {
		
		
		LineIterator it = FileUtils.lineIterator(new File("D:/_Korpora/de_vocab"), "UTF-8"); //http://www.baeldung.com/java-read-lines-large-file
   	 
//    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("D:/_Korpora/de_vocab_new", true)));
    	
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File("D:/_Korpora/de_vocab_new"), true), "UTF-8"); //http://www.xyzws.com/javafaq/how-to-append-data-to-the-end-of-existing-file-in-java/165
        BufferedWriter fbw = new BufferedWriter(writer);
        
    	
		try {
		    while (it.hasNext()) {
		    	
		        String line = it.nextLine();
		        
		        String [] parts = line.split("\t");
		        String word = parts[0];
		        long frequency = Long.parseLong(parts[1]);
		        
		     
		        
		        if (frequency > 100) {
		        	
//		    		out.println(word + "\t" + frequency);
		        	
		    		fbw.write(word + "\t" + frequency);
		    		fbw.newLine();
	
		        }
		    }
		    
		} finally {
			
		    LineIterator.closeQuietly(it);
		    
//		    out.close();
		    
		    fbw.close();
		    
		}

	}

}
