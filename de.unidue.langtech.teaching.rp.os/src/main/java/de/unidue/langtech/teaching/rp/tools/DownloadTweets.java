package de.unidue.langtech.teaching.rp.tools;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

/**
 * Download tweets by their status ID.
 * Used for TwitterLID corpus.
 * @author Onur
 *
 */
public class DownloadTweets {

	public static void main(String[] args) throws IOException{
		
		    String twitterlidBaseDir = new DkproContext().getWorkspace("TwitterLID").getAbsolutePath();
			
			File corpus = new File(twitterlidBaseDir + "/ground-truth.trn");
			File fullCorpus = new File(twitterlidBaseDir + "/ground-truth_full.trn");
		
			 if (!(fullCorpus.exists())) {
				 
			 for (String line : FileUtils.readLines(corpus)) {
			    	
			    	try {
			    		
						String[] lines = line.split(";");
						String tweetid = lines[0];
						String language = lines[1].replaceAll("german", "de").replaceAll("english", "en").replaceAll("french", "fr")
								.replaceAll("dutch", "nl").replaceAll("spanish", "es");
						String url = "https://www.twitter.com/anyuser/status/" + Long.parseLong(tweetid);
						
						//source: http://stackoverflow.com/a/24998098
						Document doc = Jsoup.connect(url).get(); 
						Element tweetText = doc.select("p.js-tweet-text.tweet-text").last();
						Element privateTweet = doc.select("span.message-text").first(); //check if account is private
						String tweet = tweetText.text();
						
						if (privateTweet.text().isEmpty()) {
							System.out.println(tweet);
							FileUtils.writeStringToFile(fullCorpus, 
									tweetid + "\t" + tweet + "\t" + language + "\n", true);
						}
						
					} 	catch (NullPointerException e) {
						System.err.println("Error! Account suspended");
					}  	catch (HttpStatusException e) {
						System.err.println("Error! Tweet not found (404)");
					}  		
			    }
			 }
		
	}

}
