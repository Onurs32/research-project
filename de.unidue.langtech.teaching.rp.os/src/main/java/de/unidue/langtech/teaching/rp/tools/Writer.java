package de.unidue.langtech.teaching.rp.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unidue.langtech.teaching.rp.type.OriginalLanguage;

public class Writer 
	extends JCasAnnotator_ImplBase
{
	
	public static final String PARAM_RESULT_FILE = "ResultFile";
    @ConfigurationParameter(name = PARAM_RESULT_FILE, mandatory = true)
    private File resultFile;

	public void process(JCas jcas) 
			throws AnalysisEngineProcessException 
	{
		
        
        OriginalLanguage actual = JCasUtil.selectSingle(jcas, OriginalLanguage.class);
        
        String actualLanguage = actual.getLanguage();
        String detectedLanguage = jcas.getDocumentLanguage();
        String text = jcas.getDocumentText();
        String information = text + "\t" + actualLanguage + "\t" + detectedLanguage;
        	
            try {
    			FileUtils.writeStringToFile(resultFile, information + "\n", true);
    		} catch (IOException e) {
    			throw new AnalysisEngineProcessException(e);
    		}

	}

}
