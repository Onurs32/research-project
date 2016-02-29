package de.unidue.langtech.teaching.rp.os.detector;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unidue.langtech.teaching.rp.detector.LanguageIdentifier;

/**
 * Tests all languages that are present in the three used corpora.
 * Tests are based on LanguageIdentifierTest in the DKPro repository.
 * Config file for all languages which are used here is already set.
 * @author Onur
 *
 */
public class LanguageIdentifierTest {
	
	String[] languages = new String[] {"de", "en", "fr", "it", "nl", "es"};
	String ligaModel = "src/main/resources/textcat_models/textcat_liga.conf";
	
	@Test
	public void testDutch()
	throws Exception
	{
		AnalysisEngine ae = createEngine(LanguageIdentifier.class,
        		LanguageIdentifier.PARAM_CONFIG_FILE, ligaModel);
		JCas aJCas = ae.newJCas();
		aJCas.setDocumentText("Dit is een nederlandse documentaire.");
		ae.process(aJCas);
		assertEquals("nl", aJCas.getDocumentLanguage());
	}
	
	@Test
	public void testEnglish()
	throws Exception
	{
		AnalysisEngine ae = createEngine(LanguageIdentifier.class,
        		LanguageIdentifier.PARAM_CONFIG_FILE, ligaModel);
		JCas aJCas = ae.newJCas();
		aJCas.setDocumentText("This is an english file.");
		ae.process(aJCas);
		assertEquals("en", aJCas.getDocumentLanguage());
	}
	
	@Test
	public void testFrench()
	throws Exception
	{
		AnalysisEngine ae = createEngine(LanguageIdentifier.class,
        		LanguageIdentifier.PARAM_CONFIG_FILE, ligaModel);
		JCas aJCas = ae.newJCas();
		aJCas.setDocumentText("C'est un document français.");
		ae.process(aJCas);
		assertEquals("fr", aJCas.getDocumentLanguage());
	}

	@Test
	public void testGerman()
	throws Exception
	{
		AnalysisEngine ae = createEngine(LanguageIdentifier.class,
        		LanguageIdentifier.PARAM_CONFIG_FILE, ligaModel);
		JCas aJCas = ae.newJCas();
		aJCas.setDocumentText("Das ist ein deutsches Dokument.");
		ae.process(aJCas);
		assertEquals("de", aJCas.getDocumentLanguage());
	}
	
	@Test
	public void testItalian()
	throws Exception
	{
		AnalysisEngine ae = createEngine(LanguageIdentifier.class,
        		LanguageIdentifier.PARAM_CONFIG_FILE, ligaModel);
		JCas aJCas = ae.newJCas();
		aJCas.setDocumentText("Questo è un documentario italiano.");
		ae.process(aJCas);
		assertEquals("it", aJCas.getDocumentLanguage());
	}
	
	@Test
	public void testSpanish()
	throws Exception
	{
		AnalysisEngine ae = createEngine(LanguageIdentifier.class,
        		LanguageIdentifier.PARAM_CONFIG_FILE, ligaModel);
		JCas aJCas = ae.newJCas();
		aJCas.setDocumentText("Este es un documental español.");
		ae.process(aJCas);
		assertEquals("es", aJCas.getDocumentLanguage());
	}

}
