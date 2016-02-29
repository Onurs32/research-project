package de.unidue.langtech.teaching.rp.os.detector;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.junit.Assert.assertEquals;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unidue.langtech.teaching.rp.detector.LanguageDetectorWeb1T;

public class LanguageDetectorWeb1TTest {
	
	   @Test
	    public void web1tLanguageDetectorTest()
	        throws Exception
	    {
		   
		   String web1TBaseDir = new DkproContext().getWorkspace("Web1T").getAbsolutePath();
		   
		   ExternalResourceDescription de = createExternalResourceDescription(
	                Web1TFrequencyCountResource.class,
	                Web1TFrequencyCountResource.PARAM_INDEX_PATH,
	                web1TBaseDir + "/Euro/de",
	                Web1TFrequencyCountResource.PARAM_LANGUAGE, "de",
	                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");

	        ExternalResourceDescription en = createExternalResourceDescription(
	                Web1TFrequencyCountResource.class,
	                Web1TFrequencyCountResource.PARAM_INDEX_PATH,
	                web1TBaseDir + "/Euro/en",
	                Web1TFrequencyCountResource.PARAM_LANGUAGE, "en",
	                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");
	        
	        ExternalResourceDescription fr = createExternalResourceDescription(
	                Web1TFrequencyCountResource.class,
	                Web1TFrequencyCountResource.PARAM_INDEX_PATH,
	                web1TBaseDir + "/Euro/fr",
	                Web1TFrequencyCountResource.PARAM_LANGUAGE, "fr",
	                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");
	        
	        ExternalResourceDescription it = createExternalResourceDescription(
	                Web1TFrequencyCountResource.class,
	                Web1TFrequencyCountResource.PARAM_INDEX_PATH,
	                web1TBaseDir + "/Euro/it",
	                Web1TFrequencyCountResource.PARAM_LANGUAGE, "it",
	                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");
	        
	        ExternalResourceDescription nl = createExternalResourceDescription(
	                Web1TFrequencyCountResource.class,
	                Web1TFrequencyCountResource.PARAM_INDEX_PATH,
	                web1TBaseDir + "/Euro/nl",
	                Web1TFrequencyCountResource.PARAM_LANGUAGE, "nl",
	                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");
	        
	        ExternalResourceDescription es = createExternalResourceDescription(
	                Web1TFrequencyCountResource.class,
	                Web1TFrequencyCountResource.PARAM_INDEX_PATH,
	                web1TBaseDir + "/Euro/es",
	                Web1TFrequencyCountResource.PARAM_LANGUAGE, "es",
	                Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");

	        List<ExternalResourceDescription> resources = new ArrayList<ExternalResourceDescription>();
	        resources.add(de);
	        resources.add(en);
	        resources.add(fr);
	        resources.add(it);
	        resources.add(nl);
	        resources.add(es);

	        AnalysisEngineDescription engine = createEngineDescription(
	                createEngineDescription(BreakIteratorSegmenter.class), 
	                createEngineDescription(LanguageDetectorWeb1T.class,
	                        LanguageDetectorWeb1T.PARAM_MAX_NGRAM_SIZE, 1,
	                        LanguageDetectorWeb1T.PARAM_FREQUENCY_PROVIDER_RESOURCES, resources));

	        JCas jcas = JCasFactory.createJCas();
	        jcas.setDocumentText("@Alchey Did you see King of Leon on Ellen D's show recently?");
	        
	        runPipeline(jcas, engine);

	        assertEquals("en", jcas.getDocumentLanguage());
		   
	    }

}
