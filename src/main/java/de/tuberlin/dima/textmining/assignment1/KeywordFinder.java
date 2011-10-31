package de.tuberlin.dima.textmining.assignment1;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

<<<<<<< HEAD
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

=======
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
>>>>>>> 23e1216a9fc4bb26897c09d20142630840fee37d

public class KeywordFinder {
    
	private String text;
    
	public List<String> keywords( String text, int howMany ) {
        
		this.text = text;
		List<String> keywords = Lists.newArrayList();
        
		keywords = this.frequentist( keywords, howMany );        
        return keywords;
  	}
    
	/**
	 * Returns the howMany most frequent tokens that aren't stop words
	 * according to either a list of common stop words or a word length
	 * based heuristics. includes compound word checking.
	 * 
	 * @param keywords
	 * @param howMany
	 * @return
	 */
  	public List<String> frequentist( List<String> keywords, int howMany ) {
	    
  		// tokenize and clean text
  		List<String> tokenList = new LinkedList<String>();
  		StringTokenizer st = new StringTokenizer( this.text );

<<<<<<< HEAD
  		String stopWords;
  	    try {
	    	stopWords = Resources.toString( Resources.getResource("assignment1/en_stop_words.txt"), Charsets.UTF_8 );
	    } catch (IOException ioex) {
	    	stopWords = null;
	    }
	    
  	    while ( st.hasMoreTokens() ) {
	    	String token = st.nextToken();
	    	token = token.replaceAll( "[\\s\\p{P}]+$", "" );				// remove punctuation
	    	token = token.replaceAll( "^[\\s\\p{P}]+", "" );
	    	token = token.replaceAll( "['s]$", "" );						// remove some en specifics
	    	token = token.toLowerCase();	    							// toLowerCase()
	    	if ( stopWords != null && stopWords.indexOf( token ) == -1 ) {	// filter stop words
	    		tokenList.add( token );
  	    	} else if ( stopWords == null && token.length() > 3 ) {
	    		tokenList.add( token );
  	    	}
  	    }

  	    // count token frequency, and sort by it (descending)
  		HashMap<String,Integer> tokenMap = new HashMap<String,Integer>();
  	    Iterator<String> it = tokenList.iterator();
  	    while(it.hasNext()) {
  	    	String token = it.next();
  	    	if ( tokenMap.containsKey( token ) ) {
  	    		tokenMap.put( token, tokenMap.get( token ) + 1 );
  	    	} else {
  	    		tokenMap.put( token, 1 );
  	    	}
  	    }
  	    ValueComparator vc =  new ValueComparator( tokenMap );
	    TreeMap<String,Integer> treemap = new TreeMap<String,Integer>( vc );
	    treemap.putAll( tokenMap );
  	    
	    // extract the first howMany tokens
	    for ( int i=0; i<howMany; i++) {
	    	keywords.add( treemap.pollFirstEntry().getKey() );
	    }
	    
 	    // compute n-grams and their frequency
  	    HashMap<String,Integer> ngramMap = this.twogramify( tokenList, false );	// of all tokens
  	    HashMap<String,Integer> keygramMap = this.twogramify( keywords, true );	// of the keywords
  	    // if a keygram is a frequent ngram, use compound instead of single words
  	    for (Map.Entry<String,Integer> entry : keygramMap.entrySet()) {
  	    	String ngram = entry.getKey();
  	    	if ( ngramMap.containsKey( ngram ) && ngramMap.get( ngram )  > 50 ) {
  	    		String[] words = ngram.split( " " );
  	    		int idx1 = keywords.indexOf( words[0] );
  	    		keywords.remove( idx1 );
  	    		int idx2 = keywords.indexOf( words[1] );
  	    		keywords.remove( idx2 );
  	    		keywords.add( ngram );
  	    		keywords.add( treemap.pollFirstEntry().getKey() );
  	    	}
  	    }
  	    
	    return keywords;
  	}
  	
  	public HashMap<String,Integer> twogramify( List<String> tokens, boolean bothWays ) {
  		
  		HashMap<String,Integer> ngrams = new HashMap<String,Integer>();
  		for ( int i=0; i<tokens.size()-2+1; i++ ) {
  			String ngram = tokens.get( i ) + " " + tokens.get( i+1 );
  			if ( ngrams.containsKey( ngram ) ) {
  				ngrams.put( ngram, ngrams.get( ngram ) + 1 );
  			} else {
  				ngrams.put( ngram , 1 );	
  			}
  			if ( bothWays ) {
	  			String ngram_inv = tokens.get( i+1 ) + " " + tokens.get( i );
	  			if ( ngrams.containsKey( ngram_inv ) ) {
	  				ngrams.put( ngram_inv, ngrams.get( ngram_inv ) + 1 );
	  			} else {
	  				ngrams.put( ngram_inv , 1 );	
	  			}
  			}
  		}
  		return ngrams;
  	}
}


class ValueComparator implements Comparator<String> {

	Map<String, Integer> base;
	
	public ValueComparator( Map<String, Integer> base ) {
		this.base = base;
	}

	public int compare( String a, String b ) {
	
		if( (Integer)base.get(a) < (Integer)base.get(b) )
			return 1;
		else if( (Integer)base.get(a) == (Integer)base.get(b) )
			return 0;
		else
			return -1;
=======
	/*
	 * TODO: make better
	 */
	private String cheapPullStopWords(String inputText) {

		inputText = inputText.replaceAll("The", "");
		inputText = inputText.replaceAll("As", "");
		inputText = inputText.replaceAll("Another", "");
		inputText = inputText.replaceAll("After", "");

		return inputText;

	}

	@SuppressWarnings("rawtypes")
	public LinkedHashMap sortHashMapByValues(HashMap passedMap,
			boolean ascending) {

		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		if (!ascending)
			Collections.reverse(mapValues);

		LinkedHashMap someMap = new LinkedHashMap();
		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				if (passedMap.get(key).toString().equals(val.toString())) {
					passedMap.remove(key);
					mapKeys.remove(key);
					someMap.put(key, val);
					break;
				}
			}
		}
		return someMap;
	}

	public List<String> keywords(String text, int howMany) {
		List<String> keywords = Lists.newArrayList();

		Pattern pattern = Pattern.compile("\\b([A-Z]{2,})\\b");

		Matcher matcher = pattern.matcher(text);

		HashMap<String, Integer> keywordCount = new HashMap<String, Integer>();

		/*
		 * get bold keys (main actors short)
		 */
		while (matcher.find()) {

			if (keywordCount.containsKey(matcher.group())) {
				keywordCount.put(matcher.group(),
						keywordCount.get(matcher.group()) + 1);
			} else {
				keywordCount.put(matcher.group(), 1);
			}

		}

		text = this.cheapPullStopWords(text);

		/*
		 * get common bigrams
		 */
		pattern = Pattern.compile("\\b[A-Z][a-z]+ [A-Z][a-z]+\\b");
		matcher = pattern.matcher(text);
		HashMap<String, Integer> bigrams = new HashMap<String, Integer>();

		while (matcher.find()) {

			if (bigrams.containsKey(matcher.group())) {
				bigrams.put(matcher.group(), bigrams.get(matcher.group()) + 1);
			} else {
				bigrams.put(matcher.group(), 1);
			}

		}

		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Integer> sortedBold = sortHashMapByValues(
				keywordCount, false);
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Integer> sortedBi = sortHashMapByValues(bigrams,
				false);

		/*
		 * now find common bigrams that contain main actors (for full name)
		 */

		for (String key : sortedBold.keySet()) {

			// break when howMany are found
			if (keywords.size() == howMany)
				break;

			// System.out.println(key + " " + sortedBold.get(key));

			for (String bigram : sortedBi.keySet()) {

				// System.out.println(bigram + " : " + sortedBi.get(bigram));

				if (bigram.toLowerCase().contains(key.toLowerCase())) {
					// System.out.println("FOUND! + " + bigram + " : "
					// + sortedBi.get(bigram));

					if (!keywords.contains(bigram))
						keywords.add(bigram);

					break;
				}

			}

		}

		// keywords.addAll(foundKeywords);

		return keywords;
>>>>>>> 23e1216a9fc4bb26897c09d20142630840fee37d
	}
}
