package de.unidue.langtech.teaching.rp.evaluator;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unidue.langtech.teaching.rp.tools.TableBuilder;
import de.unidue.langtech.teaching.rp.type.OriginalLanguage;


/**
 * The evaluator tells us if a tweet instance was correctly classified.
 * It also gives us some details about the classification, for example how many were correctly classified.
 * 
 * @author suenme
 *
 */
public class LanguageEvaluatorFinal
    extends JCasAnnotator_ImplBase
{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;
    
	public static final String PARAM_OUTPUT_FILE = "OutputFile";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
    private File outFile;

	private static  HashMap<String, List<Double>> languageMaps;
	
	private static List<String> tableLines;
	
    
    
    /* 
     * This is called BEFORE any documents are processed.
     */
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
        languageMaps = new HashMap<String, List<Double>>();
        
        Double[] values = new Double[] {0.0, 0.0, 0.0, 0.0};
        
        for (int i = 0; i < languages.length; i++) {
        	
        	
        	languageMaps.put(languages[i], Arrays.asList(values));
        }
        
        tableLines = new ArrayList<String>();
       

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
        
        tableLines.add(jcas.getDocumentText() + "\t" + actualLanguage + "\t" + detectedLanguage);
        
        
        for (String language : languages) {
        	


        	List <Double> values = languageMaps.get(language);
        	
        	double tp = values.get(0);
        	double tn = values.get(1);
        	double fp = values.get(2);
        	double fn = values.get(3);
        	
        	
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
    		
    		
    		languageMaps.put(language, Arrays.asList(tp, tn, fp, fn));
    		
        	
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
		
//		System.out.println("\n--------------------------------------------------------------------------" + "\n\nScores for each language\n\n");
        
		TableBuilder tb = new TableBuilder();
        
        tb.addRow("Language", "Accuracy", "Precision", "Recall");
		
		for (int i = 0; i < languages.length; i++) {
			
        	List <Double> values = languageMaps.get(languages[i]);
        	
        	double tp = values.get(0);
        	double tn = values.get(1);
        	double fp = values.get(2);
        	double fn = values.get(3);
        	
    		double accuracy = (tp + tn) / (tp + tn + fp + fn);
    		double precision = tp / (tp + fp);
    		double recall = tp / (tp + fn);
    		
    		tb.addRow(languages[i], defaultFormat.format(accuracy), defaultFormat.format(precision), defaultFormat.format(recall));
    		
//        	System.out.println("Scores for language: " + languages[i] + "\nAccuracy: " + accuracy + "\nPrecision: " + precision + "\nRecall: " + recall + "\n");
        	
		}
		
		
		tb.addRow(" ", " ", " ", " ");
        tb.addRow("Text", "GoldLang", "DetectLang", " ");
     
		for (String tableLine : tableLines) {
			
			String[] parts = tableLine.split("\t");
			
	        tb.addRow(parts[0], parts[1], parts[2], " ");
	        
		}
		
        try {
			FileUtils.writeStringToFile(outFile, tb.toString(), true);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
//        System.out.println("Language of " + correct + " out of " + nrOfDocuments + " documents were correctly detected."
//        		+ "\nScored an accuracy of " + defaultFormat.format((double)correct/(double)nrOfDocuments));
    }
}