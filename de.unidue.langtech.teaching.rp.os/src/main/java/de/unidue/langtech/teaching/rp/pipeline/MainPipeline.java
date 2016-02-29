package de.unidue.langtech.teaching.rp.pipeline;


import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import de.unidue.langtech.teaching.rp.detector.Optimaize;
import de.unidue.langtech.teaching.rp.detector.TikaLanguageIdentifier;
import de.unidue.langtech.teaching.rp.evaluator.LanguageEvaluator;
import de.unidue.langtech.teaching.rp.reader.CorpusReader;
import de.unidue.langtech.teaching.rp.tools.ResultStore;
import de.unidue.langtech.teaching.rp.uimatools.Stopwatch;
import de.unidue.langtech.teaching.rp.uimatools.Writer;

/**
 * Pipeline can only run for one corpus. No arrays/list of reader descriptions possible!
 * Reader descriptions for three corpora are already configured.
 * Please configure the string array in line 76 according to the languages supported by the corpus.
 * Also please set the config file for TextCat LanguageIdentifier in lines 132 - 133.
 * TikaLanguageIdentifier can only load one config property file! So please edit the property file
 * in src/main/resources/org/apache/tika/language accordingly!
 * @author Onur
 *
 */
public class MainPipeline 
{

	public static void main(String[] args) 
			throws Exception 
	{
		
		String web1TBaseDir = new DkproContext().getWorkspace("Web1T").getAbsolutePath();
		String twitterlidBaseDir = new DkproContext().getWorkspace("TwitterLID").getAbsolutePath();
		String ligaBaseDir = new DkproContext().getWorkspace("LIGA").getAbsolutePath();
		String newsCommentaryBaseDir = new DkproContext().getWorkspace("NewsCommentary").getAbsolutePath();
		
    	//TwitterLID Corpus
    	CollectionReaderDescription twitterCorpus = CollectionReaderFactory.createReaderDescription(
    			CorpusReader.class,
    			CorpusReader.PARAM_INPUT_FILE, twitterlidBaseDir + "/ground-truth_full.trn"
        );
    	
    	//LIGA Corpus
    	@SuppressWarnings("unused")
		CollectionReaderDescription ligaCorpus = CollectionReaderFactory.createReaderDescription(
    			CorpusReader.class,
    			CorpusReader.PARAM_INPUT_FILE, ligaBaseDir + "/corpus_LIGA.txt"
        );
    	
    	//NewsCommentary Corpus
    	@SuppressWarnings("unused")
		CollectionReaderDescription newsCorpus = CollectionReaderFactory.createReaderDescription(
    			CorpusReader.class,
    			CorpusReader.PARAM_INPUT_FILE, newsCommentaryBaseDir + "/nc-v8_full.txt"
        );
    	
        CorpusConfiguration corpus = new CorpusConfiguration(twitterCorpus, "TwitterLID_Training");
                
        
        String[] languages = new String[] {"de", "en", "fr", "nl", "es"};
        
        List<File> resultFiles = new ArrayList<File>();
        List<File> scoreFiles = new ArrayList<File>();
        List<File> timeFiles = new ArrayList<File>();
        
        List<AnalysisEngineDescription> description = new ArrayList<AnalysisEngineDescription>();
        
        description.add(AnalysisEngineFactory.createEngineDescription(JLangDetect.class,
        		JLangDetect.PARAM_LANGUAGES, languages));
        
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
        
        String twitterLIDModel = "src/main/resources/textcat_models/textcat_tweetlid.conf";
        //String ligaModel = "src/main/resources/textcat_models/textcat_liga.conf";
        
        description.add(createEngineDescription(LanguageIdentifier.class,
                		LanguageIdentifier.PARAM_CONFIG_FILE, twitterLIDModel));
        
        description.add(createEngineDescription(Optimaize.class,
                		Optimaize.PARAM_LANGUAGES, languages));
        
        description.add(createEngineDescription(TikaLanguageIdentifier.class));
        

       
        	for (AnalysisEngineDescription desc : description) {
        		
        		String descName = desc.getImplementationName();
        		
        		String detectorName = descName.substring(descName.lastIndexOf(".") + 1);
        		
        		File resultFile = new File("src/main/resources/temp_results/" + corpus.getCorpusName() + "-" + detectorName + ".txt");
        		File scoreFile = new File("src/main/resources/temp_results/" + corpus.getCorpusName() + "-" + detectorName + "_scores.txt");
        		File timerFile = new File("src/main/resources/temp_results/" + corpus.getCorpusName() + "-" + detectorName + "_times.txt");
        		
                SimplePipeline.runPipeline(
                		corpus.readerDescription,
						AnalysisEngineFactory.createEngineDescription(ArktweetTokenizer.class),
						AnalysisEngineFactory.createEngineDescription(Stopwatch.class,
								Stopwatch.PARAM_TIMER_NAME, "t1"),
						desc,
						AnalysisEngineFactory.createEngineDescription(Stopwatch.class,
								Stopwatch.PARAM_TIMER_NAME, "t1",
								Stopwatch.PARAM_OUTPUT_FILE, timerFile),
						AnalysisEngineFactory.createEngineDescription(LanguageEvaluator.class,
								LanguageEvaluator.PARAM_LANGUAGES, languages,
								LanguageEvaluator.PARAM_SCORE_FILE, scoreFile),
						AnalysisEngineFactory.createEngineDescription(Writer.class,
								Writer.PARAM_RESULT_FILE, resultFile));
                
                resultFiles.add(resultFile);
                scoreFiles.add(scoreFile);
                timeFiles.add(timerFile);
        		
        	}
        	
        	ResultStore.saveResults(resultFiles, corpus.getCorpusName() + "-");
        	ResultStore.saveScores(scoreFiles, timeFiles, corpus.getCorpusName() + "-");
        	
   		   	FileUtils.cleanDirectory(new File("src/main/resources/temp_results"));
        	
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
        
        public String getCorpusName()
        {
        	return corpusName;
        }
    }

}
