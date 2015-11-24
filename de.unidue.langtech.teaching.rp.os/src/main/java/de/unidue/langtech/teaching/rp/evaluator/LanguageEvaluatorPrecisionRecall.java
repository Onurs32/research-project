package de.unidue.langtech.teaching.rp.evaluator;

import java.text.NumberFormat;

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
public class LanguageEvaluatorPrecisionRecall
    extends JCasAnnotator_ImplBase
{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGE = "language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    private static String language;


	private static double tp;
	private static double tn;
	private static double fp;
	private static double fn;
    
    
    /* 
     * This is called BEFORE any documents are processed.
     */
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        tp = 0;
        tn = 0;
        fp = 0;
        fn = 0;
    }
    
    
    /* 
     * This is called ONCE for each document
     */
    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        
        OriginalLanguage actual = JCasUtil.selectSingle(jcas, OriginalLanguage.class);
        String actualLanguage = actual.getLanguage();
        String detectedLanguage = jcas.getDocumentLanguage();
        
        System.out.println(jcas.getDocumentText());
        System.out.println(actualLanguage + " detected as " + detectedLanguage);
        
        	
    		if (actualLanguage.equals(language)) { //de
    			if (actualLanguage.equals(detectedLanguage)) { //de as de
    				tp++;
    			}
    			else { //de as en
    				fn++;
    			}
    		}
    		else {
    			if (detectedLanguage.equals(language)) { //en as de
    				fp++;
    			} else {
    				tn++;
    			}
    			
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
		
		double accuracy = (tp + tn) / (tp + tn + fp + fn);
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		
		System.out.println("Accuracy: " + accuracy + "\nPrecision: " + precision + "\nRecall: " + recall);
        
//        System.out.println("Language of " + correct + " out of " + nrOfDocuments + " documents were correctly detected."
//        		+ "\nScored an accuracy of " + defaultFormat.format((double)correct/(double)nrOfDocuments));
    }
}