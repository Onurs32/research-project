package de.unidue.langtech.teaching.rp.os.reader;

import static org.junit.Assert.assertEquals;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unidue.langtech.teaching.rp.reader.CorpusReader;
import de.unidue.langtech.teaching.rp.type.OriginalLanguage;

public class ReaderTest {
	
    @Test
    public void testTwitterLID()throws Exception
    {
    	
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                CorpusReader.class,
                CorpusReader.PARAM_INPUT_FILE, "src/test/resources/readerTest_twitterLID.txt"
        );
        
        int i = 0;
        for (JCas jcas : new JCasIterable(reader)) {
            // we know that the first line should be the first tweet
            if (i==0) {
                assertEquals("Bayern-Remis in Gladbach: Team lethargisch, Trainer tobt: Mario Gomez traf per Kopf, Bastian Schweinsteiger per ... http://bit.ly/9BzI6a", jcas.getDocumentText());
                
                OriginalLanguage origLang = JCasUtil.selectSingle(jcas, OriginalLanguage.class);
                
                assertEquals("de", origLang.getLanguage());
            }
            
            System.out.println(jcas.getDocumentText());
            i++;
        }
        
        // there are 3 lines in the file, so we should get 3 documents here
        assertEquals(3, i);
    	
    }
    
    /*
     * Also tests NewsCommentary corpus due to the same format
     */
    @Test
    public void testLIGA()throws Exception
    {
    	
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                CorpusReader.class,
                CorpusReader.PARAM_INPUT_FILE, "src/test/resources/readerTest_LIGA.txt"
        );
        
        int i = 0;
        for (JCas jcas : new JCasIterable(reader)) {
            // we know that the first line should be the first tweet
            if (i==0) {
                assertEquals("für aktuelle tweets live von der berlinale schaut mal bei vorbei", jcas.getDocumentText());
                
                OriginalLanguage origLang = JCasUtil.selectSingle(jcas, OriginalLanguage.class);
                
                assertEquals("de", origLang.getLanguage());
            }
            
            System.out.println(jcas.getDocumentText());
            i++;
        }
        
        // there are 3 lines in the file, so we should get 3 documents here
        assertEquals(3, i);
    	
    }

}
