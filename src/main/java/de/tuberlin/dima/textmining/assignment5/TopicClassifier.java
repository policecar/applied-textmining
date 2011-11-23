package de.tuberlin.dima.textmining.assignment5;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.index.Term;
import org.apache.lucene.util.Version;

import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.TwoDimensionalCounter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;


/**
 * Assignment 5 of the Course Applied Text Mining Ws 2011/12 TU Berlin DIMA
 *
 * In this assignment you will have to implement a topic classifier
 */
public class TopicClassifier {
	
	private final static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
	
	private ClassicCounter<String> classes = new ClassicCounter<String>();
	private ClassicCounter<String> priors = new ClassicCounter<String>();
	private TwoDimensionalCounter<String, String> features = new TwoDimensionalCounter<String, String>();
	private double EPSILON = 1e-6;
    
    /**
     * This function estimates the parameters of the classifier based on the training set
     *
     * @param trainingDocs
     */
    public void train(List<labeledDocument> trainingDocs) throws IOException {

        // traverse training collection
        for(labeledDocument currentDoc : trainingDocs) {
        	
        	// The class label of the current document (e.g."rec.autos")
        	String documentLabel = currentDoc.getLabel();

        	/* Note: if you want to tune your smoothing parameters on held-out data, you'll have to
        	 * split the training set yourself
        	 *
        	 * e.g.:
                      
				double sample = Math.random();
				if(sample <= 0.80){
					//  add doc to training data
				}
				else(sample > 0.80 ){
					// add doc to held out data
				}
        	 */

        	// utilize lucene token stream to transform text into tokens (removes stop words etc.)
        	TokenStream stream = analyzer.tokenStream(null, new StringReader(currentDoc.getText()));
        	CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);

        	// for each term in document
        	while (stream.incrementToken()) {
        		if (attr.length() > 0) {
        			String term = new String(attr.buffer(), 0, attr.length());
                  
        			// Implement the appropriate parameter estimation for the classifier here
        			features.incrementCount( term, documentLabel ); // token counting
        		}
        	}
        	classes.incrementCount( documentLabel ); // count number of documents per class
        }
        
    	// assuming the resp. prior as (number of samples in class) / (total number of samples)
        for ( String classname : classes.keySet() ) {
        	priors.setCount( classname, Math.log( classes.getCount( classname )) - 
        								Math.log( classes.totalCount()) );
        }        
    }

    public String classifyDocument(String document) throws IOException {

    	ClassicCounter<String> posteriors = new ClassicCounter<String>();
    	
    	// extract tokens from document string
    	TokenStream stream = analyzer.tokenStream(null, new StringReader( document ));
    	CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
    	
    	while (stream.incrementToken()) { // for each term in document
    		if (attr.length() > 0) {
    			
    			String term = new String(attr.buffer(), 0, attr.length());
    			
    			for ( String classname : classes.keySet() ) {
    				if ( features.containsKey( term, classname ) ) {
    					double likelihood;
    					// add log Laplace-smoothed probabilities
//    					likelihood = Math.log1p( features.getCount( term, classname )) - 
//    										Math.log( features.totalCount( term ) + features.totalCount());
    					// use linear interpolation
    					likelihood = Math.log( EPSILON * features.getCount( term, classname ) + 
    							(( 1.0 - EPSILON ) * features.totalCount( term )) ); 
    					posteriors.incrementCount( classname, likelihood ); // : )
    				} else {
    					posteriors.incrementCount( classname, Math.log( EPSILON ) );
    				}
    			}
    		}
    	}
    	double argmax = 0.0;
    	String winner = null;
    	for (String classname : posteriors.keySet()) {
    		double prob = Math.exp ( posteriors.getCount( classname ) + priors.getCount( classname ) );
    		if ( prob >= argmax) {
    			argmax = prob;
    			winner = classname;
    		}
    	}
    	return winner;
    }
}