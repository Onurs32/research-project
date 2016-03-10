package de.unidue.langtech.teaching.rp.uimatools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Recreate a given Web1T resource, which has a frequency limit.
 * @author Onur
 *
 */
public class Web1TMinimizer 
{

	public static void main(String[] args) 
			throws IOException 
	{
		
		//source: http://www.baeldung.com/java-read-lines-large-file
		LineIterator it = FileUtils.lineIterator(new File("D:/_Korpora/de_vocab"), "UTF-8"); 
    	
		//source: http://www.xyzws.com/javafaq/how-to-append-data-to-the-end-of-existing-file-in-java/165
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File("D:/_Korpora/de_vocab_new"), true), "UTF-8");
        BufferedWriter fbw = new BufferedWriter(writer);
        
		try {
			
		    while (it.hasNext()) {
		        String line = it.nextLine();
		        String [] parts = line.split("\t");
		        long frequency = Long.parseLong(parts[1]);
		        
		        if (frequency > 100) {
		    		fbw.write(line);
		    		fbw.newLine();
		        }
		    }
		    
		} finally {
		    LineIterator.closeQuietly(it);
		    fbw.close();
		}
		
	}

}
