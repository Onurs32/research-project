package de.unidue.langtech.teaching.rp.detector;

import java.util.HashMap;
import java.util.Map;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class TikaLanguageIdentifier extends JCasAnnotator_ImplBase{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;
    
    private Map<String, LanguageProfile> languageMaps;
    
    
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
		LanguageIdentifier.clearProfiles();
		
		languageMaps = new HashMap <String, LanguageProfile> ();
		
		for (String language : languages) {

		LanguageProfile profile = new LanguageProfile();
		languageMaps.put(language, profile);
		
		}
		
        
    }

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		String docText = aJCas.getDocumentText();

		if (docText != null) {
			
			LanguageIdentifier identifier = new LanguageIdentifier(docText);
			
			LanguageIdentifier.initProfiles(languageMaps);
			
			aJCas.setDocumentLanguage(identifier.getLanguage());
			
		}
		
	}

}
