package de.tuberlin.dima.textmining.assignment4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tuberlin.dima.textmining.assignment3.ShallowSentence;
import de.tuberlin.dima.textmining.assignment3.ShallowToken;
import de.tuberlin.dima.textmining.assignment3.SimpleRelation;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

public class OpenInformationExtractor {
	
	private static final String NAMED_ENTITY = "((?:PERSON|LOCATION|ORGANIZATION|\\ )+)";	// add |NN|NNP to get many more!
	private static final Pattern EVE_PATTERN = Pattern.compile(NAMED_ENTITY + "\\s" + 
			"((?:V[A-Z]{1,2}|\\ )+)" + "\\s" + NAMED_ENTITY); // "/\1"

	public List<SimpleRelation> findGenericRelations(String text) {

		List<SimpleRelation> foundRelations = new ArrayList<SimpleRelation>();
		// implement open information extraction method here or in other method
		return foundRelations;
	}
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	private void tagNamedEntities(String input) {

		// if you need it, use the Named Entity recognizer
		AbstractSequenceClassifier<Annotation> classifier = CRFClassifier
				.getClassifierNoExceptions("assignment4/all.3class.distsim.crf.ser.gz");
	}
	
	
	public List<SimpleRelation> findGenericRelations(Vector<ShallowSentence> sentences) {

		List<SimpleRelation> foundRelations = new ArrayList<SimpleRelation>();

		// get classifier
		@SuppressWarnings("unchecked")
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier
				.getClassifierNoExceptions("assignment4/all.3class.distsim.crf.ser.gz");
		
		Iterator<ShallowSentence> iter = sentences.iterator();
		while (iter.hasNext()) {
		
			ShallowSentence sentence = iter.next();
			
			// extract plain text
			List<CoreLabel> list = new ArrayList<CoreLabel>();
			for (ShallowToken token : sentence) {
				CoreLabel cl = new CoreLabel();
				cl.setWord(token.getText());
				list.add(cl);
			}
			
			// tag Named Entities
			List<CoreLabel> NERdSentence = classifier.classifySentence(list);
			
			// merge Named Entity and POS tags into tagline
			String tagline = "";
			Iterator<ShallowToken> tit = sentence.iterator();
			Iterator<CoreLabel> wit = NERdSentence.iterator();			
			while (tit.hasNext() && wit.hasNext()) {
				ShallowToken token = tit.next();
				CoreLabel word = wit.next();
				if (word.get(AnswerAnnotation.class).length() > 1) {	// != 0 doesn't work
					tagline += word.get(AnswerAnnotation.class) + " ";
				} else {
					tagline += token.getTag() + " ";
				}
			}
			
			// match pattern, store matches in foundRelations
			Matcher matcher = EVE_PATTERN.matcher(tagline);
			if (matcher.find()) {
				// map match back to natural language tokens
				MatchResult mres = matcher.toMatchResult();
				String entity1 = tagToToken(mres.start(1), mres.end(1), tagline, sentence).trim();
				String verb = tagToToken(mres.start(2), mres.end(2), tagline, sentence).trim();
				String entity2 = tagToToken(mres.start(3), mres.end(3), tagline, sentence).trim();
				if (verb.length() > 2 && verb.indexOf("say") == -1 && verb.indexOf("said") == -1 
						&& entity1.length() > 1 && entity2.length() > 1) { // hack to get rid of "(" verbs – duh
//					System.out.println(verb + "(" + entity1 + "," + entity2 + ")");
					foundRelations.add(new SimpleRelation(matcher.group(2), matcher.group(1), matcher.group(3)));						
				}
			}
		}
		return foundRelations;
	}
	
	private String tagToToken(int start, int stop, String tagline, ShallowSentence sentence) {
		
		int idx_init = tagline.substring(0, start).split(" ").length;
		int idx_final = tagline.substring(0, stop).split(" ").length;
				
		String token = "";
		for (ShallowToken tkn : sentence.subList(idx_init, idx_final)) {
			token += tkn.getText() + " ";
		}
		return token;
	}
}
