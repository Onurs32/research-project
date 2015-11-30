/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unidue.langtech.teaching.rp.old;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringIterable;

/**
 * Language detector based on n-gram frequency counts, e.g. as provided by Web1T
 * 
 * @author zesch
 *
 */
public class LanguageDetectorWeb1TOld
    extends JCasAnnotator_ImplBase
{

    /**
     * An array of external resources of frequency providers (one for each language that should be detected). 
     */
    public static final String PARAM_FREQUENCY_PROVIDER_RESOURCES = "frequencyProviders";
    @ExternalResource(key = PARAM_FREQUENCY_PROVIDER_RESOURCES, mandatory = true)
    private FrequencyCountProvider[] frequencyProviders;

    /**
     * The minimum n-gram size that should be considered. Default is 1. 
     */
    public static final String PARAM_MIN_NGRAM_SIZE = "minNGramSize";
    @ConfigurationParameter(name = PARAM_MIN_NGRAM_SIZE, mandatory = true, defaultValue = "1")
    private int minNGramSize;
    
    /**
     * The maximum n-gram size that should be considered. Default is 3. 
     */
    public static final String PARAM_MAX_NGRAM_SIZE = "maxNGramSize";
    @ConfigurationParameter(name = PARAM_MAX_NGRAM_SIZE, mandatory = true, defaultValue = "3")
    private int maxNGramSize;

    private Map<String,FrequencyCountProvider> providerMap;
    
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
        providerMap = new HashMap<String,FrequencyCountProvider>();
        
        for (FrequencyCountProvider provider : frequencyProviders) {
            try {
                providerMap.put(provider.getLanguage(), provider);
            }
            catch (Exception e) {
                throw new ResourceInitializationException(e);
            }
        }
    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
      
        List<String> words = JCasUtil.toText(JCasUtil.select(jcas, Token.class));
        
        if (words.size() < 1) {
            return;
        }
        
        List<String> ngrams = new ArrayList<String>();
//        if (words.size() > 1) {
//            ngrams.add(getNgram(BOS, words.get(0), words.get(1)));
//        }
        
//        for (String ngram : new NGramStringIterable(words, 1, 1)) {
//            ngrams.add(ngram);
//        }
        
        for (String ngram : new NGramStringIterable(words, 1, 1)) {
        	ngram = ngram.replaceAll("#", "").replaceAll("RT", "");
        	if (ngram.length()>1 && !(ngram.startsWith("@")) && !(ngram.startsWith("http"))){
            ngrams.add(ngram);
        	}
        }
                
        try {
        	
        	Map<String,Double> langProbs;
        	String maxLanguage = "x-unspecified";
        	
//        	langProbs = getSingleLanguageProbabilities(ngrams, false);
//            Double maxLanguageValue = 0.0;
//            if (langProbs.values().size() > 0) {
//            maxLanguageValue = Collections.max(langProbs.values());
//            
//            for (String lang : langProbs.keySet()) {
//                double prob = langProbs.get(lang);
//                if (langProbs.get(lang).equals(maxLanguageValue)) {
//                    maxLanguage = lang;
//                }
//                System.out.println(lang + " - " + prob);
//            }
//            
//            }
            
//            if (hasDuplicates(langProbs)) {
            	
            	langProbs = getLanguageProbabilities(ngrams);
            	int zeroCounter = 0; //counter used to check if all probs are zero
            	
                double maxLogProb = Double.NEGATIVE_INFINITY;
                for (String lang : langProbs.keySet()) {
                    double prob = langProbs.get(lang);
                    if (prob > maxLogProb) {
                        maxLogProb = prob;
                        maxLanguage = lang;
                    }
                    System.out.println(lang + " - " + prob);
                    if (prob == 0.0) {
                    	zeroCounter++;
                    }
                }
                
//            }	
                
            if (zeroCounter == langProbs.size()) {
            	maxLanguage = "x-unspecified";
            }
            
//            getCertainty(langProbs);
            
            jcas.setDocumentLanguage(maxLanguage);
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
    

    
    private Map<String,Double> getLanguageProbabilities(List<String> ngrams)
            throws Exception
    {
        Map<String,Double> langProbs = new HashMap<String,Double>();
       
        for (String lang : providerMap.keySet()) { 
                                    
            FrequencyCountProvider provider = providerMap.get(lang);
            
            long nrOfUnigrams = provider.getNrOfNgrams(1);
            long nrOfBigrams  = provider.getNrOfNgrams(2);
            long nrOfTrigrams = provider.getNrOfNgrams(3);
            
            double textLogProbability = 0.0;
            
            for (String ngram : ngrams) {
                
                long frequency = provider.getFrequency(ngram);

                int ngramSize = FrequencyUtils.getPhraseLength(ngram);
                                
                long normalization = 1;
                int weighting = 1;
                if (ngramSize == 1) {
                    normalization = nrOfUnigrams;
                }
                else if (ngramSize == 2) {
                    weighting = 2;
                    normalization = nrOfBigrams;
                }
                else if (ngramSize == 3) {
                    weighting = 4;
                    normalization = nrOfTrigrams;
                }
    
                if (frequency > 0) {
                    double logProb = Math.log( weighting * ((double) frequency) / normalization );
                    
                    textLogProbability += logProb;
                }
                else {
                    textLogProbability += Math.log( 1.0 / normalization);
                }
                
            }
          
            langProbs.put(lang, textLogProbability);
        }
        
        return langProbs;
    }
    
    @SuppressWarnings("unused")
	private void getCertainty(Map<String,Double> map) {
    	
    	double maxProb = Collections.max(map.values());
    	
		Map<String,Double> newMap = new HashMap<String, Double>();
		double sum = 0.0;
		
		
		for (Entry<String, Double> entry : map.entrySet()) {
		    newMap.put(entry.getKey(), Math.exp(entry.getValue() - maxProb));
		    sum += Math.exp(entry.getValue() - maxProb);
		}
		
		NumberFormat defaultFormat = NumberFormat.getPercentInstance();
		defaultFormat.setMinimumFractionDigits(2);

		for (Entry<String, Double> entry : newMap.entrySet()) {
		    System.out.println("Language: " + entry.getKey() + " Certainty: " + defaultFormat.format(entry.getValue()/sum));
		}
    	
    }
    
    @SuppressWarnings("unused")
	private Map<String,Double> getSingleLanguageProbabilities(List<String> ngrams, boolean multipleMaximums)
            throws Exception
    {
    	
    	 Map<String,Double> textLogProbability = new HashMap<String,Double>();
       
        for (String ngram : ngrams) {
        	
            TreeMap<Double,String> langProbs = new TreeMap<Double,String>();
        	
                                    
        	 double logProb = 0.0;

            for (String lang : providerMap.keySet()) {
            	
            	
                FrequencyCountProvider provider = providerMap.get(lang);
                
                long nrOfUnigrams = provider.getNrOfNgrams(1);
                long nrOfBigrams  = provider.getNrOfNgrams(2);
                long nrOfTrigrams = provider.getNrOfNgrams(3);
                
       
                
                long frequency = provider.getFrequency(ngram);

                int ngramSize = FrequencyUtils.getPhraseLength(ngram);
                                
                long normalization = 1;
                int weighting = 1;
                if (ngramSize == 1) {
                    normalization = nrOfUnigrams;
                }
                else if (ngramSize == 2) {
                    weighting = 2;
                    normalization = nrOfBigrams;
                }
                else if (ngramSize == 3) {
                    weighting = 4;
                    normalization = nrOfTrigrams;
                }
    
                if (frequency > 0) {
                    logProb = Math.log( weighting * ((double) frequency) / normalization );
                    
                }
                else {
                	logProb = Math.log( 1.0 / normalization);
                }
                
                langProbs.put(logProb,lang);
                System.out.println("Ngram: " + ngram + "\tLang: " + lang + "\tProb: " + logProb + "\tFrequency: " + frequency);

            }
            
            	setTextProbability(langProbs, textLogProbability); 
        }
        
        return textLogProbability;
    }
    
    @SuppressWarnings("unused")
	private String getNgram(String ...strings) {
        return StringUtils.join(strings, " ");
    }
    
    private void setTextProbability(TreeMap<Double,String> langProbs, Map<String,Double> textLogProbability) {
    	
        System.out.println("LangProb: " + langProbs);
        System.out.println("Highest Prob: " + langProbs.lastEntry());
        Double previousValue = textLogProbability.get(langProbs.get(langProbs.lastKey()));
        if (previousValue == null ){
        	previousValue = 0.0;
        }
        textLogProbability.put(langProbs.get(langProbs.lastKey()), 1 + previousValue);
        System.out.println("TextLogProb: " + textLogProbability);
    	
    }
    
    
    @SuppressWarnings("unused")
	private boolean hasDuplicates(Map<String, Double> map){
    	
    	boolean status = false;
    	
    	List<String> maxEntrys = new ArrayList<String>();
    	
    	if (map.values().size() > 0) {
        double maxValueInMap =(Collections.max(map.values()));  
        for (Entry<String, Double> entry : map.entrySet()) {  
            if (entry.getValue() == maxValueInMap) {
            	maxEntrys.add(entry.getKey());     
            }
        }
    	}
    	
        if (maxEntrys.size() > 1) {
        	status = true;
        } 
        
        if (status == true) {
            System.err.println("Single language probabilities method could not clearly resolve language. \nStarting default method.");
        }
        
    	    return status;

    	}
}