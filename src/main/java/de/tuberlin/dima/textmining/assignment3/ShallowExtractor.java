package de.tuberlin.dima.textmining.assignment3;

import java.util.ArrayList;
import java.util.Iterator;

public class ShallowExtractor {

	/**
	 * Find quotes.
	 * 
	 * @param sentences
	 *            the sentences
	 * @return the list
	 */
	public Iterable<SimpleRelation> findQuotes(Iterable<ShallowSentence> sentences) {

		// go thru the text and remove all sentences that aren't framed by quotation marks
		ArrayList<SimpleRelation> quotations = new ArrayList<SimpleRelation>();
		String quote = "";
		String quoter = "";		
		Iterator<ShallowSentence> iter = sentences.iterator();
		boolean quoting = false;

		while (iter.hasNext()) {
			ShallowSentence sentence = iter.next();
			Iterator<ShallowToken> it = sentence.iterator();
			
			while (it.hasNext()) {
				ShallowToken token = it.next();
				// if token contains quotation marks at its beginning, the quote begins
				// save all following tokens in quote until another quotation mark shows up
				if (token.getText().startsWith("\"") && token.getText().endsWith("\"")) {
					continue;
				}
				if (token.getText().startsWith("\"")) {
					quoting = true;
				}
				if (token.getText().endsWith("\"")) {
					quoting = false;
					quote += token.getText();
					boolean aVerb = false;
					while (it.hasNext()) {	// take the rest of the sentence to be the quoter (for starters)
						ShallowToken tkn = it.next();
						if (tkn.getText().equals("said") ||  tkn.getText().equals("told")) {
							aVerb = true;
						}
						quoter += tkn.getText() + " ";
					}
					if (aVerb) {
						quotations.add(new SimpleRelation(RelationType.QUOTE_SPEAKER, quote, quoter));
						aVerb = false;
					}
					quote = "";		// reset quote and quoter
					quoter = "";
				}
				if (quoting) {
					quote += token.getText() + " ";
				}
			}
		}
		return quotations;
	}

	/**
	 * Find apposition.
	 * 
	 * @param sentences
	 *            the sentences
	 * @return the list
	 */
	public Iterable<SimpleRelation> findApposition(Iterable<ShallowSentence> sentences) {

		ArrayList<SimpleRelation> appositions = new ArrayList<SimpleRelation>();
		Iterator<ShallowSentence> iter = sentences.iterator();

		while (iter.hasNext()) {
			ShallowSentence sentence = iter.next();
			Iterator<ShallowToken> it = sentence.iterator();
			String name = "";
			String isA = "";
			int cnt = 0;
			
			while (it.hasNext()) {
				ShallowToken token = it.next();
				if (token.getTag().equals("NNP")){
					name += token.getText() + " ";
					cnt += 1;
				} else if (token.getTag().equals("NN") || token.getTag().equals("JJ")) {
					isA += token.getText() + " ";
					cnt += 1;
				} else {
					name = "";
					isA = "";
					cnt = 0;
				}
			}
			if (cnt > 3 && name.length() > 3 && isA.length() > 3) {
				appositions.add(new SimpleRelation(RelationType.APPOSITION, name, isA));
			}
		}
		return appositions;
	}

}
