package de.tuberlin.dima.textmining.assignment2;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.stanford.nlp.stats.TwoDimensionalCounter;

import java.util.Collection;
import java.util.List;

public class NGramModel implements LanguageModel {
	
	protected TwoDimensionalCounter<String,String> bigramCounter = new TwoDimensionalCounter<String,String>();
	protected double totalSum = 0.0;

	NGramModel(){
		bigramCounter.defaultReturnValue(0.0);
	}

	@Override
	public void train(Collection<List<String>> corpus) {

		for (List<String> sentence : corpus) {
			List<String> stoppedSentence = Lists.newArrayList(sentence);
			stoppedSentence.add(0, "<start>");
			stoppedSentence.add("<stop>");
			for (int i=1; i < stoppedSentence.size(); i++) {
				bigramCounter.incrementCount(stoppedSentence.get(i-1), stoppedSentence.get(i));
			}
		}
		totalSum = bigramCounter.totalCount();
		System.out.println("[NGramModel.train()]: Model trained. Contains " + bigramCounter.entrySet().size() +
				" bigrams. Total count:" + totalSum);
	}

	@Override
	public double getWordProbability(List<String> sentence, int index) {

		String word = sentence.get(index);
		String history = sentence.get(index-1);
	    double probability = ( bigramCounter.getCount(history, word) / (totalSum + 1.0) )
	    		/ ( bigramCounter.totalCount(history) / (totalSum + 1.0) );
	    if (probability == 0.0) {
	    	probability = 1.0 / (totalSum + 1.0);
	    }
	    return probability;
	}

	@Override
	public double sentenceLogProbability(List<String> sentence) {

		List<String> mySentence = Lists.newArrayList(sentence);
		mySentence.add(0, "<start>");
		mySentence.add("<stop>");
		double probability = 0.0;

		for (int i=1; i < mySentence.size(); i++) {
			probability += Math.log(getWordProbability(mySentence, i));
			Preconditions.checkArgument(!Double.isInfinite(probability), "MSG: word:" + mySentence.get(i) + " - " +
					getWordProbability(mySentence, i) + " count:" + bigramCounter.getCount(mySentence.get(i-1), mySentence.get(i)));
		}
		return probability;
	}

	
	protected String generateWord(String history) {

		double sample = Math.random();
		double sum = 0.0;
		for (String word : bigramCounter.getCounter(history).keySet()) {
			sum += ((bigramCounter.getCount(history, word) / totalSum) / (bigramCounter.totalCount(history) / totalSum));
			if (sum > sample) {
				return word;
			}
		}
		return "<unk>";
	}
	
	@Override
	public Iterable<String> generateSentence() {
		
		List<String> sentence = Lists.newArrayList();

	    String word = generateWord("<start>");
	    while (!word.equals("<stop>")) {
	      sentence.add(word);
	      word = generateWord(word);
	    }
		return sentence;
	}
}
