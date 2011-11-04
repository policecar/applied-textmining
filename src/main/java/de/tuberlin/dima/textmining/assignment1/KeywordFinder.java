package de.tuberlin.dima.textmining.assignment1;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

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
	}
}
