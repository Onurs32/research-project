package de.unidue.langtech.teaching.rp.tools;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Download tweets by their ID.
 * For this method, only an exception is thrown when tweet is not found (404).
 * When a tweet belongs to a private account, no exception is thrown.
 * So we have to use two lists to bypass this issue.
 * @author Onur
 *
 */
public class DownloadTweets {

	public static void main(String[] args) throws IOException{
			
			File corpus = new File("D:/_Projekt_Korpora/Corpus 1 - Twitter/ground-truth.tst");
			File fullCorpus = new File("D:/_Projekt_Korpora/Corpus 1 - Twitter/ground-truth_full_new.tst");
		
			 if (!(fullCorpus.exists())) {
				 
			 for (String line : FileUtils.readLines(corpus)) {
			    	
			    	try {
			    		
						String[] lines = line.split(";");
						String tweetid = lines[0];
						String language = lines[1].replaceAll("german", "de").replaceAll("english", "en").replaceAll("french", "fr")
								.replaceAll("dutch", "nl").replaceAll("spanish", "es");
						String url = "https://www.twitter.com/anyuser/status/" + Long.parseLong(tweetid);
						
						Document doc = Jsoup.connect(url).get(); //http://stackoverflow.com/a/24998098
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
