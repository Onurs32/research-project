package de.unidue.langtech.teaching.rp.reader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.unidue.langtech.teaching.rp.type.OriginalLanguage;


/**
 * Reader which can read three corpora: TwitterLID, LIGA & NewsCommentary
 * LIGA and NewsCommentary have the same format.
 * TwitterLID additionally contains the status ID for a tweet.
 * @author Onur
 *
 */
public class CorpusReader
    extends JCasCollectionReader_ImplBase
{

    /**
     * Input file
     */
    public static final String PARAM_INPUT_FILE = "InputFile";
    @ConfigurationParameter(name = PARAM_INPUT_FILE, mandatory = true)
    private File inputFile;    
    
    private List<String> lines;
    private int currentLine;
    

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
        try {
           lines = FileUtils.readLines(inputFile);
           currentLine = 0;
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }
    
    

    public boolean hasNext()
        throws IOException, CollectionException
    {
        return currentLine < lines.size();
    }
    
    

    @Override
    public void getNext(JCas jcas)
        throws IOException, CollectionException
    {
        // split line into gold standard language and actual text
        String[] parts = lines.get(currentLine).split("\t");
        
        if (parts.length == 3) {
            jcas.setDocumentText(parts[1]);
            
            OriginalLanguage actual = new OriginalLanguage(jcas);
            actual.setLanguage(parts[2]);
            actual.addToIndexes();
            
        } else {
            jcas.setDocumentText(parts[0]);
            
            OriginalLanguage actual = new OriginalLanguage(jcas);
            actual.setLanguage(parts[1]);
            actual.addToIndexes();
        }
        
        currentLine++;
    }

    
    public Progress[] getProgress()
    {
        return new Progress[] { new ProgressImpl(currentLine, lines.size(), "lines") };
    }
}