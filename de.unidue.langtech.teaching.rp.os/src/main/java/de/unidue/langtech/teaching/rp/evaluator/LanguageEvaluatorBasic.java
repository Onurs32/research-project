package de.unidue.langtech.teaching.rp.evaluator;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unidue.langtech.teaching.rp.type.OriginalLanguage;


/**
 * The evaluator tells us if a tweet instance was correctly classified.
 * It also gives us some details about the classification, for example how many were correctly classified.
 * 
 * @author suenme
 *
 */
public class LanguageEvaluatorBasic
    extends JCasAnnotator_ImplBase
{
	
	public static final String PARAM_SCORE_FILE = "scoreFile";
    @ConfigurationParameter(name = PARAM_SCORE_FILE, mandatory = true)
    private File scoreFile;

    private int correct;
    private int nrOfDocuments;
    
    
    /* 
     * This is called BEFORE any documents are processed.
     */
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        correct = 0;
        nrOfDocuments = 0;
    }
    
    
    /* 
     * This is called ONCE for each document
     */
    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        nrOfDocuments++; 
        
        OriginalLanguage actual = JCasUtil.selectSingle(jcas, OriginalLanguage.class);
        String actualLanguage = actual.getLanguage();
        String detectedLanguage = jcas.getDocumentLanguage();
        
        System.out.println(jcas.getDocumentText());
        System.out.println(actualLanguage + " detected as " + detectedLanguage);
        
        if (actualLanguage.equals(detectedLanguage)) {
        	correct++;
        }

    }


    /* 
     * This is called AFTER all documents have been processed.
     */
    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();
        
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
		defaultFormat.setMinimumFractionDigits(2);
        
        System.out.println("Language of " + correct + " out of " + nrOfDocuments + " documents were correctly detected."
        		+ "\nScored an accuracy of " + defaultFormat.format((double)correct/(double)nrOfDocuments));
        
        
        try {
			FileUtils.writeStringToFile(scoreFile, defaultFormat.format((double)correct/(double)nrOfDocuments) + "\n", true);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}  
    }
}