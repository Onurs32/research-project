package de.unidue.langtech.teaching.rp.detector;

import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

/**
 * Identifies the language of a document with Optimaize Language-Detector.
 *
 *
 */
public class OptimaizeLangDetect extends JCasAnnotator_ImplBase {
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;
	

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		LanguageDetector languageDetector = null;
		try {
			languageDetector = makeNewDetector();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	    
	    String docText = aJCas.getDocumentText();
		if (docText != null) {
			
		    List<DetectedLanguage> result = languageDetector.getProbabilities(docText);
		    DetectedLanguage best = result.get(0);
		    
		    aJCas.setDocumentLanguage(best.getLocale().getLanguage());
		}
		
	}
	
	private LanguageDetector makeNewDetector() throws IOException {
        LanguageDetectorBuilder builder = LanguageDetectorBuilder.create(NgramExtractors.standard());
//        builder.shortTextAlgorithm(50);
//        builder.prefixFactor(1.5);
//        builder.suffixFactor(2.0);

        LanguageProfileReader langProfileReader = new LanguageProfileReader();
        for (String language : languages) {
            LanguageProfile languageProfile = langProfileReader.read(OptimaizeLangDetect.class.getResourceAsStream("/languages/" + language));
            builder.withProfile(languageProfile);
        }

        return builder.build();
    }


}