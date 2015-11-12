package de.unidue.langtech.teaching.rp.detector;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

public class TikaLanguageIdentifier extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		String docText = aJCas.getDocumentText();
		if (docText != null) {
			
			LanguageIdentifier identifier = new LanguageIdentifier(docText);
			aJCas.setDocumentLanguage(identifier.getLanguage());
			
		}
		
	}

}
