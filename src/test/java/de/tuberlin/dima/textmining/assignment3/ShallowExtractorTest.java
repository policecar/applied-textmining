package de.tuberlin.dima.textmining.assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import com.google.common.collect.Lists;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ShallowExtractorTest {

	ShallowExtractor extractor = new ShallowExtractor();

	/**
	 * Gets the parsed sentences from file resource.
	 * 
	 * @return the sentences from file
	 */
	public Iterable<ShallowSentence> getSentencesFromFile() {

		List<ShallowSentence> sentences = Lists.newArrayList();

		Scanner scan;
		try {
			scan = new Scanner(new File("src/test/resources/assignment3/sentences-json"));

			while (scan.hasNextLine()) {
				try {
					JSONObject job = new JSONObject(scan.nextLine());
					ShallowSentence readSentence = new ShallowSentence(job);
					sentences.add(readSentence);
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return sentences;
	}

	/**
	 * Test extract quotes.
	 */
	@Test
	public void testExtractQuotes() {

		/*
		 * first get sentences from file
		 */
		Iterable<ShallowSentence> sentences = this.getSentencesFromFile();
				
		/*
		 * TODO: make this function solve the problem
		 */
		extractor.findQuotes(sentences);
	}

	/**
	 * Test extract apposition.
	 */
	@Test
	public void testExtractApposition() {
		/*
		 * first get sentences from file
		 */
		Iterable<ShallowSentence> sentences = this.getSentencesFromFile();

		/*
		 * TODO: make this function solve the problem
		 */
		extractor.findApposition(sentences);
	}

}