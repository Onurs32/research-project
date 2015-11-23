package de.unidue.langtech.teaching.rp.pipeline;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource;
import de.unidue.langtech.teaching.rp.detector.LanguageDetectorWeb1T;
import de.unidue.langtech.teaching.rp.evaluator.LanguageEvaluator;
import de.unidue.langtech.teaching.rp.reader.TwitterLIDReader;
import de.unidue.langtech.teaching.rp.tools.LanguageExtractor;

public class LanguageDetectorPipeline {

	public static void main(String[] args) throws IOException, UIMAException {

		String web1TBaseDir = new DkproContext().getWorkspace("Web1T").getAbsolutePath();
		File languageFile = new File("src/main/resources/" + "desiredLanguages.txt");
		
        AnalysisEngineDescription ldWeb1T = createEngineDescription(
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
            );
        
    	System.out.println("Started language detection");
    	double start = System.currentTimeMillis();
        
        SimplePipeline.runPipeline(
                CollectionReaderFactory.createReader(
                        TwitterLIDReader.class,
                        TwitterLIDReader.PARAM_INPUT_FILE, "D:/_Projekt_Korpora/Corpus 1 - Twitter/ground-truth_full.tst"
                ),
                AnalysisEngineFactory.createEngineDescription(ArktweetTokenizer.class),
                ldWeb1T,
                AnalysisEngineFactory.createEngineDescription(LanguageEvaluator.class)//,
//                AnalysisEngineFactory.createEngineDescription(LanguageExtractor.class,
//                		LanguageExtractor.PARAM_DESIRED_LANGUAGES, new String[] {"de"},
//                		LanguageExtractor.PARAM_OUTPUT_FILE, languageFile)
                );
        
        double end = System.currentTimeMillis();
        double time = end - start;
        System.out.println("Language detection took " + time/1000 + " seconds");

	}

}
