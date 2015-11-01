package de.unidue.langtech.teaching.rp.os;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

public class WriteClass extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.println(
				"Document Text: " + aJCas.getDocumentText() + 
				"\n" +
				"Document Language: " + aJCas.getDocumentLanguage());
		
	}

}
