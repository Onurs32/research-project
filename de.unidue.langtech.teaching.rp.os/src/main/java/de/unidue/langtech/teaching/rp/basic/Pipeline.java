package de.unidue.langtech.teaching.rp.basic;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;
import de.tudarmstadt.ukp.dkpro.core.textcat.LanguageIdentifier;


public class Pipeline {

  public static void main(String[] args) throws Exception {
    runPipeline(
        createReaderDescription(Reader.class,
            Reader.PARAM_INPUT_FILE, "src/main/resources/test.txt"),
        createEngineDescription(LanguageIdentifier.class),
        createEngineDescription(WriteClass.class));
  }
}
