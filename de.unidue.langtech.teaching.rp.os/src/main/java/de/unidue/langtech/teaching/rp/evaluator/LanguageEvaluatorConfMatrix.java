package de.unidue.langtech.teaching.rp.evaluator;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class LanguageEvaluatorConfMatrix
    extends JCasAnnotator_ImplBase
{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;
    
	public static final String PARAM_SCORE_FILE = "scoreFile";
    @ConfigurationParameter(name = PARAM_SCORE_FILE, mandatory = true)
    private File scoreFile;



	private static  HashMap<String, List<Double>> languageMaps;
	
	private static HashMap<String,Integer> confMatrix;
    
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
        confMatrix = new HashMap<String,Integer>();
        
        languageMaps = new HashMap<String, List<Double>>();
        
        Double[] values = new Double[] {0.0, 0.0, 0.0, 0.0};
        
        for (int i = 0; i < languages.length; i++) {
        	
        	
        	languageMaps.put(languages[i], Arrays.asList(values));
        }
 
    }
    
    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        
        OriginalLanguage actual = JCasUtil.selectSingle(jcas, OriginalLanguage.class);
        String actualLanguage = actual.getLanguage();
        String detectedLanguage = jcas.getDocumentLanguage();
        
        System.out.println(jcas.getDocumentText());
        System.out.println(actualLanguage + " detected as " + detectedLanguage);
        
        String key = actualLanguage + "," + detectedLanguage;
        
        if (confMatrix.containsKey(key)) {
        
        	confMatrix.put(actualLanguage + "," + detectedLanguage, confMatrix.get(key) + 1);
        
        } else {
        	
        	confMatrix.put(actualLanguage + "," + detectedLanguage, 1);
        }
        
        for (String language : languages) {

        	List <Double> values = languageMaps.get(language);
        	
        	double tp = values.get(0);
        	double tn = values.get(1);
        	double fp = values.get(2);
        	double fn = values.get(3);
        	
        	//source: https://github.com/dkpro/dkpro-csniper/blob/master/csniper-ml/src/main/java/de/tudarmstadt/ukp/csniper/ml/TKSVMlightResultConsumer.java
        	
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

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();
        
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
		defaultFormat.setMinimumFractionDigits(2);
		
		System.out.println("\n--------------------------------------------------------------------------" + "\n\nScores for each language\n\n");
		
		for (int i = 0; i < languages.length; i++) {
			
        	List <Double> values = languageMaps.get(languages[i]);
        	
        	double tp = values.get(0);
        	double tn = values.get(1);
        	double fp = values.get(2);
        	double fn = values.get(3);
        	
        	//source: https://github.com/dkpro/dkpro-csniper/blob/master/csniper-ml/src/main/java/de/tudarmstadt/ukp/csniper/ml/TKSVMlightResultConsumer.java
        	
    		double accuracy = (tp + tn) / (tp + tn + fp + fn);
    		double precision = tp / (tp + fp);
    		double recall = tp / (tp + fn);
    		
    		String accuracyFormatted = defaultFormat.format(accuracy);
    		String precisionFormatted = defaultFormat.format(precision);
    		String recallFormatted = defaultFormat.format(recall);
       
        	System.out.println("Scores for language: " + languages[i] + "\nAccuracy: " + 
        						accuracyFormatted + "\nPrecision: " + precisionFormatted + "\nRecall: " + recallFormatted + "\n");
        	
                try {
        			FileUtils.writeStringToFile(scoreFile, languages[i] + "\t" + accuracyFormatted + "___" + precisionFormatted + "___" + recallFormatted + "\n", true);
        		} catch (IOException e) {
        			throw new AnalysisEngineProcessException(e);
        		}        

		}
		
    	printConfMatrix(confMatrix);
		
    }
    
    /*
     * Based on: http://stackoverflow.com/a/26857286/3677505
     * @param confMatrix
     */
    public void printConfMatrix(Map <String, Integer> confMatrix) 
    {
    	
    	Set<String> classNames = new HashSet<String>();
    	
    	for(String key : confMatrix.keySet()) {
    		
    	    String[] classes = key.split(",");
    	    
    	    if(classes != null && classes.length > 0) {
    	        classNames.addAll(Arrays.asList(classes));
    	    }
    	    
    	}
    	
    	List<String> sortedClassNames = new ArrayList<String>();
    	sortedClassNames.addAll(classNames);
    	Collections.sort(sortedClassNames);
    	
    	System.out.print("Gold/Detected");
    	
    	for(String predictedClassName : sortedClassNames) {
    	    System.out.print("\t\t" + predictedClassName + "\t");
    	}
    	
    	System.out.println();
    	
    	for(String actualClassName : sortedClassNames) {

    	    System.out.print(actualClassName);
    	    
    	    for(String predictedClassName : sortedClassNames) {
    	    	
    	        Integer value = confMatrix.get(actualClassName + "," + predictedClassName);
    	        System.out.print("\t\t\t");
    	        
    	        if(value != null) {
    	            System.out.print(value);
    	        }
    	        
    	    }
    	    
    	    System.out.println();
    	    
    	}
    	
    }
    
}