package de.unidue.langtech.teaching.rp.pipeline;


import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource;
import de.unidue.langtech.teaching.rp.detector.JLangDetect;
import de.unidue.langtech.teaching.rp.detector.LanguageDetectorWeb1T;
import de.unidue.langtech.teaching.rp.detector.LanguageIdentifier;
import de.unidue.langtech.teaching.rp.detector.OptimaizeLangDetect;
import de.unidue.langtech.teaching.rp.evaluator.LanguageEvaluatorFinal;
import de.unidue.langtech.teaching.rp.reader.LIGAReader;
import de.unidue.langtech.teaching.rp.reader.TwitterLIDReader;

public class MainPipeline {

	public static void main(String[] args) throws Exception {
		
		String web1TBaseDir = new DkproContext().getWorkspace("Web1T").getAbsolutePath();
//		String twitterlidBaseDir = new DkproContext().getWorkspace("TwitterLID").getAbsolutePath();
//		String ligaBaseDir = new DkproContext().getWorkspace("LIGA").getAbsolutePath();
		
    	//TwitterLID Corpus
    	CollectionReaderDescription twitterCorpus = CollectionReaderFactory.createReaderDescription(
    			TwitterLIDReader.class,
                TwitterLIDReader.PARAM_INPUT_FILE, "D:/_Projekt_Korpora/Corpus 1 - Twitter/ground-truth_full.trn"
        );
    	
//    	//LIGA Corpus
//    	CollectionReaderDescription ligaCorpus = CollectionReaderFactory.createReaderDescription(
//    			LIGAReader.class,
//    			LIGAReader.PARAM_INPUT_FILE, "D:/_Projekt_Korpora/Corpus 2 - LIGA/corpus_LIGA.txt"
//        );
    	
        CorpusConfiguration[] corpusConfigurations = new CorpusConfiguration[] {
                
                new CorpusConfiguration(twitterCorpus, "TwitterLID")//,
//                new CorpusConfiguration(ligaCorpus, "LIGA")
                
                  };
        
        String [] twitterlidLanguages = new String[] {"de", "en", "fr", "nl", "es"};
//        String [] ligaLanguages = new String[] {"de", "en", "fr", "nl", "es", "it"};
        
        List<AnalysisEngineDescription> description = new ArrayList<AnalysisEngineDescription>();
        
        description.add(AnalysisEngineFactory.createEngineDescription(JLangDetect.class,
        		JLangDetect.PARAM_LANGUAGES, twitterlidLanguages));
        
        description.add(createEngineDescription(
            	LanguageDetectorWeb1T.class,
            	LanguageDetectorWeb1T.PARAM_FREQUENCY_PROVIDER_RESOURCES, 
                Arrays.asList(
                        createExternalResourceDescription(
                                Web1TFrequencyCountResource.class,
                                Web1TFrequencyCountResource.PARAM_INDEX_PATH, web1TBaseDir + "/Euro/de",
                                Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_LANGUAGE, "de"
                        ),
                        createExternalResourceDescription(
                                Web1TFrequencyCountResource.class,
                                Web1TFrequencyCountResource.PARAM_INDEX_PATH, web1TBaseDir + "/Euro/fr",
                                Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_LANGUAGE, "fr"
                        ),
                        createExternalResourceDescription(
                                Web1TFrequencyCountResource.class,
                                Web1TFrequencyCountResource.PARAM_INDEX_PATH, web1TBaseDir + "/Euro/en",
                                Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_LANGUAGE, "en"
                        ),
                        createExternalResourceDescription(
                                Web1TFrequencyCountResource.class,
                                Web1TFrequencyCountResource.PARAM_INDEX_PATH, web1TBaseDir + "/Euro/nl",
                                Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_LANGUAGE, "nl"
                        ),
                        createExternalResourceDescription(
                                Web1TFrequencyCountResource.class,
                                Web1TFrequencyCountResource.PARAM_INDEX_PATH, web1TBaseDir + "/Euro/es",
                                Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1",
                                Web1TFrequencyCountResource.PARAM_LANGUAGE, "es"
                        )
                 )
            ));
        
        description.add(createEngineDescription(LanguageIdentifier.class,
                		LanguageIdentifier.PARAM_CONFIG_FILE, "src/main/resources/textcat_tweetlid.conf"));
        
        description.add(createEngineDescription(OptimaizeLangDetect.class,
                		OptimaizeLangDetect.PARAM_LANGUAGES, twitterlidLanguages));
        

        
        for (CorpusConfiguration corpus : corpusConfigurations) {
        	
        	
        	for (AnalysisEngineDescription desc : description) {
        		
        		String descName = desc.getImplementationName();
        		
        		String detectorName = descName.substring(descName.lastIndexOf(".") + 1);
        		
        		File outFile = new File("D:/_Projekt_Korpora/" + corpus.getCorpusName() + "-" + detectorName + ".txt");
        		
        		
                SimplePipeline.runPipeline(
                		corpus.readerDescription,
						AnalysisEngineFactory.createEngineDescription(ArktweetTokenizer.class),
						desc,
						AnalysisEngineFactory.createEngineDescription(LanguageEvaluatorFinal.class,
								LanguageEvaluatorFinal.PARAM_LANGUAGES, twitterlidLanguages,
								LanguageEvaluatorFinal.PARAM_OUTPUT_FILE, outFile));
        		
        	}
        	
        }
	

	}
	
	
	
    public static class CorpusConfiguration
    {
        CollectionReaderDescription readerDescription;
        String corpusName;


        public CorpusConfiguration(CollectionReaderDescription aReaderDescription, String aReaderName)
        {
            readerDescription = aReaderDescription;
            corpusName = aReaderName;
        }
        
        public String getCorpusName(){
        	return corpusName;
        }
    }

}