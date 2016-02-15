package de.unidue.langtech.teaching.rp.detector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import me.champeau.ld.UberLanguageDetector;

public class JLangDetect 
	extends JCasAnnotator_ImplBase 
{
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;
    
    private UberLanguageDetector detector;
    
    private Set<String> languageRestrictionsSet;
    
    @Override
    public void initialize(UimaContext context) 
    		throws ResourceInitializationException 
    {
    	
    	super.initialize(context);
    	
		detector = UberLanguageDetector.getInstance();
		
		languageRestrictionsSet = new HashSet<String>(Arrays.asList(languages));
    }
        

	@Override
	public void process(JCas aJCas) 
			throws AnalysisEngineProcessException 
	{
		
	    String docText = aJCas.getDocumentText();
		if (docText != null) {
			String language = detector.detectLang(docText, languageRestrictionsSet);
			aJCas.setDocumentLanguage(language);
		}
		
	}

}
