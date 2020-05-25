package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;

    // Feel free to add extra fields and helper methods.
    // This field contains the TF scores for all the pages
    private IDictionary<URI, Double> normDocumentURI;

    /**
     * @param webpages  A set of all webpages we have parsed. Must be non-null and
     *                  must not contain nulls.
     */
    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //

        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        this.normDocumentURI = new ChainedHashDictionary<>();

        for (Webpage page : webpages) {
            URI eachURI = page.getUri();
            double eachNormDocumentURI = norm(this.documentTfIdfVectors.get(eachURI));
            this.normDocumentURI.put(eachURI, eachNormDocumentURI);
        }

    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Double> scores = new ChainedHashDictionary<>();
        IDictionary<String, Double> words = new ChainedHashDictionary<>();

        double total = pages.size();

        for (Webpage page : pages) {
            ISet<String> check = new ChainedHashSet<>();

            for (String eachWord : page.getWords()) {
                if (!check.contains(eachWord)) {
                    double terms = words.getOrDefault(eachWord, 0.0) + 1.0;
                    words.put(eachWord, terms);
                    check.add(eachWord);
                }
            }
        }

        for (KVPair<String, Double> pair : words) {
            double inverseTerms = total / pair.getValue();
            double idf = Math.log(inverseTerms);

            scores.put(pair.getKey(), idf);
        }


        return scores;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        IDictionary<String, Double> scores = new ChainedHashDictionary<>();

        double total = words.size();

        for (String eachWord : words) {
            double number = scores.getOrDefault(eachWord, 0.0) + 1.0;
            scores.put(eachWord, number);
        }

        for (KVPair<String, Double> score : scores) {
            String uniqueWord = score.getKey();
            double number = score.getValue();

            double tf = number / total;
            scores.put(uniqueWord, tf);
        }

        return scores;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.

        IDictionary<URI, IDictionary<String, Double>> tfIdfVectors = new ChainedHashDictionary<>();

        for (Webpage page : pages) {
            URI eachURI = page.getUri();
            IDictionary<String, Double> tfIdfScores = new ChainedHashDictionary<>();
            IDictionary<String, Double> tfScore = computeTfScores(page.getWords());

            for (KVPair<String, Double> eachTfPair: tfScore) {
                String eachWord = eachTfPair.getKey();
                double tf = tfScore.get(eachWord);
                double idf = this.idfScores.get(eachWord);

                double tfIdfScore = tf * idf;

                tfIdfScores.put(eachWord, tfIdfScore);
            }

            tfIdfVectors.put(eachURI, tfIdfScores);
        }

        return tfIdfVectors;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.

        // Look up document TF-IDF vector using pageUri
        IDictionary<String, Double> documentVector = this.documentTfIdfVectors.get(pageUri);
        IDictionary<String, Double> queryVector = new ChainedHashDictionary<>();
        IDictionary<String, Double> queryTF = computeTfScores(query);

        double numerator = 0.0;

        for (KVPair<String, Double> tfPair : queryTF) {
            // Compute query word score
            String word = tfPair.getKey();

            double tf = tfPair.getValue();
            double idf = this.idfScores.getOrDefault(word, 0.0);
            double queryWordScore = tf * idf;

            queryVector.put(word, queryWordScore);

            // Compute Document Word Score
            double docWordScore = documentVector.getOrDefault(word, 0.0);

            numerator += docWordScore * queryWordScore;
        }

        double denominator = this.normDocumentURI.get(pageUri) * norm(queryVector);

        if (denominator != 0) {
            return numerator / denominator;
        } else {
            return 0.0;
        }

    }

    /**
     * The Helper method to help calculate the normalized sum of the given TF-IDF vectors
     */
    private double norm(IDictionary<String, Double> vector) {
        double output = 0.0;

        for (KVPair<String, Double> vectorPair : vector) {
            double score = vectorPair.getValue();
            output += score * score;
        }

        return Math.sqrt(output);

    }
}
