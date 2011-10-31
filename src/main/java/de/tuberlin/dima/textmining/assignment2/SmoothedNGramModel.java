package de.tuberlin.dima.textmining.assignment2;

import java.util.List;

import com.google.common.base.Preconditions;

public class SmoothedNGramModel extends NGramModel {

	public double getWordProbability(List<String> sentence, int index) {

		String word = sentence.get(index);
		String history = sentence.get(index-1);
	    double wordProbability = ((bigramCounter.getCount(history, word) + 1.0) / (totalSum + 1.0))
	    		/ ((bigramCounter.totalCount(history) + bigramCounter.getCounter(history).entrySet().size()) / (totalSum + 1.0));
	    if (wordProbability == 0.0) {
	    	wordProbability = 1.0 / (totalSum + 1.0);
	    }
		Preconditions.checkArgument(wordProbability > 0);		
	    return wordProbability;
	}
  	
	protected String generateWord(String history) {

		double sample = Math.random();
		double sum = 0.0;
		for (String word : bigramCounter.getCounter(history).keySet()) {
			sum += ((bigramCounter.getCount(history, word) + 1.0) / totalSum) 
					/ ((bigramCounter.totalCount(history) + bigramCounter.getCounter(history).size()) / totalSum);
			if (sum > sample) {
				return word;
			}
		}
		return "<unk>";
	}		
}
