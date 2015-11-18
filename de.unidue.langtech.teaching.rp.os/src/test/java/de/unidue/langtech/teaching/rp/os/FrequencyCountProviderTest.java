/*******************************************************************************
 * Copyright 2011
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
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource;

/**
 * Derived from TestFrequencyCountResourceTest
 * @author Onur
 *
 */
public class FrequencyCountProviderTest extends JCasAnnotator_ImplBase
{
	
	final static String FREQUENCY_COUNT_RESOURCE= "FrequencyCountResource";
    @ExternalResource(key = FREQUENCY_COUNT_RESOURCE)
    private FrequencyCountProvider provider;
	


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

        try {
            System.out.println(provider.getFrequency("überstanden"));
            System.out.println(provider.getFrequency("Eröffnung"));
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
		
	}
	
    @Test
    public void configureAggregatedExample() throws Exception {
    	
    	String web1TBaseDir = new DkproContext().getWorkspace("Web1T").getAbsolutePath();
    	
        AnalysisEngineDescription desc = createEngineDescription(FrequencyCountProviderTest.class,
        		FrequencyCountProviderTest.FREQUENCY_COUNT_RESOURCE, createExternalResourceDescription(
                        Web1TFrequencyCountResource.class,
                        Web1TFrequencyCountResource.PARAM_INDEX_PATH, web1TBaseDir + "/Euro/de",
                        Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
                        Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1",
                        Web1TFrequencyCountResource.PARAM_LANGUAGE, "de"
                ));

        // Check the external resource was injected
        AnalysisEngine ae = createEngine(desc);
        ae.process(ae.newJCas());
        
        assertEquals(189071, provider.getFrequency("überstanden"));
    }
    
}