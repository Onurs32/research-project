package de.unidue.langtech.teaching.rp.detector;

import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Wrapper for Optimaize
 * @author Onur
 *
 */
public class Optimaize 
	extends JCasAnnotator_ImplBase 
{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = true)
    private static String[] languages;
    
    private LanguageDetector languageDetector;
    
    
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
    	super.initialize(context);
    	
		try {
			languageDetector = makeNewDetector();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
    }
	

	@Override
	public void process(JCas aJCas) 
			throws AnalysisEngineProcessException 
	{
	    
	    String docText = aJCas.getDocumentText();
		if (docText != null) {
		    List<DetectedLanguage> result = languageDetector.getProbabilities(docText);
		    DetectedLanguage best = result.get(0);
		    aJCas.setDocumentLanguage(best.getLocale().getLanguage());
		}
		
	}
	
	/*
	 * Based on makeNewDetector() method from LanguageDetectorImplTest
	 * https://github.com/optimaize/language-detector/blob/master/src/test/java/com/optimaize/langdetect/LanguageDetectorImplTest.java
	 * @return
	 * @throws IOException
	 */
	private LanguageDetector makeNewDetector() 
			throws IOException 
	{
		
        LanguageDetectorBuilder builder = LanguageDetectorBuilder.create(NgramExtractors.standard());
        LanguageProfileReader langProfileReader = new LanguageProfileReader();
        for (String language : languages) {
            LanguageProfile languageProfile = langProfileReader.read(Optimaize.class.getResourceAsStream("/languages/" + language));
            builder.withProfile(languageProfile);
        }

        return builder.build();
    }

}