package de.unidue.langtech.teaching.rp.detector;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

public class TikaLanguageIdentifier extends JCasAnnotator_ImplBase{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		String docText = aJCas.getDocumentText();

//		LanguageIdentifier.clearProfiles();
//		
//		for (String language : languages) {
//
//		LanguageProfile profile = new LanguageProfile();
//		LanguageIdentifier.addProfile(language, profile);
//		
//		}
		
		if (docText != null) {
			
			LanguageIdentifier identifier = new LanguageIdentifier(docText);
			aJCas.setDocumentLanguage(identifier.getLanguage());
			
		}
		
	}

}
