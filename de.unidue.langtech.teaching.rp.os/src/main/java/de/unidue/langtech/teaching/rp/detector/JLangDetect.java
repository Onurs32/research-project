package de.unidue.langtech.teaching.rp.detector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import me.champeau.ld.UberLanguageDetector;

public class JLangDetect extends JCasAnnotator_ImplBase {
	
    /**
     * Languages that should be considered. 
     */
    public static final String PARAM_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_LANGUAGES, mandatory = false)
    private static String[] languages;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		UberLanguageDetector detector = UberLanguageDetector.getInstance();
		
		Set<String> languageRestrictionsSet = new HashSet<String>(Arrays.asList(languages));


		
	    String docText = aJCas.getDocumentText();
		if (docText != null) {
			String language = detector.detectLang(docText, languageRestrictionsSet);
			aJCas.setDocumentLanguage(language);
		}
		
	}

}
