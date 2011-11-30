package de.tuberlin.dima.textmining.assignment6;

import com.google.common.collect.Lists;
import rita.wordnet.RiWordnet;
import java.util.List;

public class WordReplacement {

	private RiWordnet wordnet = new RiWordnet();

	public Iterable<ShallowSentence> findWords(Iterable<ShallowSentence> sentences, int replaceInterval) {

		List<ShallowSentence> words = Lists.newArrayList();
		
		// implement a method, which replaces words in a sentence based 
		// on a synonym mapping to Wordnet

		// NOTE: words replaced by a synonym are marked with <synonym>...</synonym>	
		for ( ShallowSentence sentence : sentences ) {

			int i = replaceInterval - 2;
			String[] synonyms = null;
			ShallowToken token = new ShallowToken("");
			
			while ( ( synonyms == null || synonyms.length == 0 ) && i < sentence.size()-1 ) {
				i += 1;
				token = sentence.get(i); //.elementAt(i);
				synonyms = getSynonyms( token.getLemma(), token.getTag() );
			}
			if ( synonyms != null && synonyms.length != 0 ) {
				int r = (int) ( Math.random() * synonyms.length );
				// verb hack for 3rd person singulifying
				if ( token.getTag().startsWith("VB") && token.getText().endsWith("s") 
						&& !token.getText().endsWith("ss") ) {
					synonyms[r] = synonyms[r] + "s";
				}
				// substitute the picked synonym into this sentence
				token.setText( "<synonym>" + synonyms[r] + "</synonym>" );
				sentence.set( i, token ); // apparently not needed
			}
			// NOTE: the next line makes the program freaking slow, hence substituted it
//			words.add( sentence );
			for ( ShallowToken tkn : sentence ) {
				System.out.print( tkn.getText() + " ");
			}
			System.out.println();
		}
		return words;		
	}
	
	/**
	 * Generates all synonyms for a given word and POS tag
	 *
	 * @return array of synonyms  
	 */
	public String[] getSynonyms( String lemma, String tag ) {
		
		if ( tag.startsWith( "JJ" ) ) {
			tag = "a";
		} else if ( tag.startsWith( "RB" ) || tag.matches( "WRB" ) ) {
			tag = "r";
		} else if ( tag.startsWith( "VB" ) ) {
			tag = "v";
		} else {
			tag = "n";
		}
		return wordnet.getAllSynonyms( lemma, tag, 15 );
	}
}
