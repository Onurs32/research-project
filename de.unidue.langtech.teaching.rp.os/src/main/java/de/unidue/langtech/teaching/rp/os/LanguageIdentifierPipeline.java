/*******************************************************************************
 * Copyright 2010
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unidue.langtech.teaching.rp.os;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.text.NumberFormat;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.textcat.LanguageIdentifier;

public
class LanguageIdentifierPipeline
{

	public static void main(String[] args)
	throws Exception
	{
        AnalysisEngine engine = createEngine(
                    createEngineDescription(
                        LanguageIdentifier.class
                         )
                    );
        
        double nrOfLines = 0;
        double nrOfCorrectOnes = 0;
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
		defaultFormat.setMinimumFractionDigits(2);
        
        for (String line : FileUtils.readLines(new File("src/main/resources/test.txt"))) {
        	nrOfLines++;
            String[] parts = line.split("\t");
            String text = parts[0];
            String language = parts[1];
            
            JCas aJCas = engine.newJCas();
            aJCas.setDocumentText(text);
            engine.process(aJCas);
            
            String[] languageParts = aJCas.getDocumentLanguage().split("/");
            String casLanguage = languageParts[languageParts.length-1];

            System.out.println("Text: " + aJCas.getDocumentText() + " Language: " + language + "\n" + "Detected Language: " + casLanguage);
            if (language.equals(casLanguage)) {
            	nrOfCorrectOnes++;
            }
        }  
        
        System.out.println("Accuracy: " + defaultFormat.format(nrOfCorrectOnes/nrOfLines));
	}

}