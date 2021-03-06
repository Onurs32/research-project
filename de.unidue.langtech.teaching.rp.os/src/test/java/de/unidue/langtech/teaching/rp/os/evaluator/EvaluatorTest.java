package de.unidue.langtech.teaching.rp.os.evaluator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.NumberFormat;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unidue.langtech.teaching.rp.detector.JLangDetect;
import de.unidue.langtech.teaching.rp.evaluator.LanguageEvaluatorConfMatrix;
import de.unidue.langtech.teaching.rp.reader.CorpusReader;

public class EvaluatorTest {
	
	/**
	 * Test if precision & recall are correctly calculated and written to a scorefile.
	 * @throws Exception
	 */
	@Test
	public void testEvaluator() throws Exception
    {
		
		File readerFile = new File("src/test/resources/evaluatorTest.txt");
		
    	CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
    			CorpusReader.class,
    			CorpusReader.PARAM_INPUT_FILE, readerFile
        );
    	
    	String[] languages = new String[] {"de"};
    	
		AnalysisEngineDescription detector = AnalysisEngineFactory.createEngineDescription(JLangDetect.class,
        		JLangDetect.PARAM_LANGUAGES, languages
		);
		
		File testScoreFile = new File("src/test/resources/testScoreFile.txt");
		
		AnalysisEngineDescription evaluator = AnalysisEngineFactory.createEngineDescription(LanguageEvaluatorConfMatrix.class,
				LanguageEvaluatorConfMatrix.PARAM_LANGUAGES, languages,
				LanguageEvaluatorConfMatrix.PARAM_SCORE_FILE, testScoreFile
		);
		
		SimplePipeline.runPipeline(
				reader,
				AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class),
				detector,
				evaluator
		);
		
    	double tp = 1.0;
    	//double tn = 0.0;
    	double fp = 2.0;
    	double fn = 0.0;
    	
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		
		NumberFormat defaultFormat = NumberFormat.getPercentInstance();
		defaultFormat.setMinimumFractionDigits(2);
		
		String resultGerman = "de" + "\t" + defaultFormat.format(precision) + "___" + defaultFormat.format(recall);
		String resultLine = FileUtils.readLines(testScoreFile).get(0);
		
		assertEquals(resultLine, resultGerman);
		
		testScoreFile.delete();
		
    }

}
