package de.unidue.langtech.teaching.rp.uimatools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

public class LanguageExtractor 
	extends JCasAnnotator_ImplBase
{
	
    /**
     * Languages that are desired.
     */
    public static final String PARAM_DESIRED_LANGUAGES = "languages";
    @ConfigurationParameter(name = PARAM_DESIRED_LANGUAGES, mandatory = false)
    private static String[] languages;
    

    public static final String PARAM_OUTPUT_FILE = "file";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = false)
    private static File file;

	@Override
	public void process(JCas aJCas) 
			throws AnalysisEngineProcessException 
	{
		
		for (String language : languages) {

			if (aJCas.getDocumentLanguage().equals(language)) {
				
				try {
					FileUtils.writeStringToFile(file, aJCas.getDocumentText() + "\t" + aJCas.getDocumentLanguage() + "\n", true);
				} catch (IOException e) {
					throw new AnalysisEngineProcessException(e);
				}
			}
		}
	}

}
