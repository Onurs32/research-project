package de.unidue.langtech.teaching.rp.pipeline;

import java.io.IOException;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.unidue.langtech.teaching.rp.detector.OptimaizeLangDetect;
import de.unidue.langtech.teaching.rp.evaluator.LanguageEvaluator;
import de.unidue.langtech.teaching.rp.reader.TwitterLIDReader;

public class OptimaizePipeline {

	public static void main(String[] args) throws IOException, UIMAException {

		
        SimplePipeline.runPipeline(
                CollectionReaderFactory.createReader(
                        TwitterLIDReader.class,
                        TwitterLIDReader.PARAM_INPUT_FILE, "D:/_Projekt_Korpora/Corpus 1 - Twitter/ground-truth_full.trn"
                ),
                AnalysisEngineFactory.createEngineDescription(ArktweetTokenizer.class),
                AnalysisEngineFactory.createEngineDescription(OptimaizeLangDetect.class,
                		OptimaizeLangDetect.PARAM_LANGUAGES, new String[]{"en", "fr", "es", "de", "nl"}),
                AnalysisEngineFactory.createEngineDescription(LanguageEvaluator.class)
                );

	}

}