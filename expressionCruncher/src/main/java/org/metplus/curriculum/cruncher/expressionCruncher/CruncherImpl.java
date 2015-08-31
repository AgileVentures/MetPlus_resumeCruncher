package org.metplus.curriculum.cruncher.expressionCruncher;

import org.metplus.curriculum.cruncher.Cruncher;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author Joao Pereira
 * Class that given a text count the words in it
 * Created by Joao Pereira on 23/07/2015.
 */
public class CruncherImpl implements Cruncher {
    public static final String CRUNCHER_NAME = "ExpressionCruncher";
    /**
     * Output saved
     */
    private Hashtable<String, Integer> result = new Hashtable<String, Integer>();
    /**
     * List with all the expressions that can be merged
     */
    private Hashtable<String, List<String>> mergeList = new Hashtable<String, List<String>>();
    /**
     * List with all the expressions to be ignored
     */
    private List<String> ignoreList = new ArrayList<String>();

    /**
     * Count using case sensitive
     */
    private boolean caseSensitive = false;

    /**
     * Separator used in keywords with more then 1 word that will be merged
     */
    private final static String SEP = "@@@@@";

    /**
     * Class constructor
     */
    public CruncherImpl() {
    }

    /**
     * Class constructor
     * @param mergeList List of expressions that can be merged
     */
    public CruncherImpl(Hashtable<String, List<String>> mergeList) {
        setMergeList(mergeList);
    }

    /**
     * Class constructor
     * @param ignoreList List of expressions that can be ignored
     */
    public CruncherImpl(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    /**
     * Class constructor
     * @param ignoreList List of expressions that can be ignored
     * @param mergeList List of expressions that can be merged
     */
    public CruncherImpl(List<String> ignoreList, Hashtable<String, List<String>> mergeList) {
        this.ignoreList = ignoreList;
        setMergeList(mergeList);
    }

    /**
     * Check if the next calculation will be case sensitive or not
     * @return True if will be case sensitive, False otherwise
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Set if the next calculation will be case sensitive or not
     * @param caseSensitive True if next calculation is case sensitive, False otherwise
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Change the list of merge expressions
     * @param mergeList New list of merge expressions
     */
    public void setMergeList(Hashtable<String, List<String>> mergeList) {
        this.mergeList = new Hashtable<String, List<String>>();
        for (Map.Entry<String, List<String>> newVal: mergeList.entrySet()) {
            this.mergeList.put(newVal.getKey().replaceAll(" ", SEP), newVal.getValue());
        }
    }

    /**
     * Retrieve the list of merge expressions used
     * @return List of merge expressions
     */
    public Hashtable<String, List<String>> getMergeList() {
        return this.mergeList;
    }

    /**
     * Retrieve the list of ignored expressions used
     * @return List of ignored expressions
     */
    public List<String> getIgnoreList() {
        return this.ignoreList;
    }

    /**
     * Retrieve the last calculated result
     * @return Calculation result
     */
    public Hashtable<String, Integer> getResult() {
        return result;
    }

    /**
     * Method will generate a hash table with the expression
     * @param expression Expression to be checked
     * @return Hash table with the accumulated results
     */
    public Hashtable<String, Integer> calculate(String expression) {
        String auxExpression = expression;

        // Remove the expressions that should be ignored
        for (String ignore: ignoreList) {
            auxExpression = auxExpression.replaceAll(ignore, " ");
        }

        // Convert everything to lower case if the search should be case insensitive
        if (!isCaseSensitive()) {
            auxExpression = auxExpression.toLowerCase();
        }

        // Merge all expressions or words
        for (String key: mergeList.keySet()) {
            for (String convert: mergeList.get(key)) {
                auxExpression = auxExpression.replaceAll(convert, " " + key + " ");
            }
        }

        // Do the reduce to words
        for (String phrase: auxExpression.split("\\.")) {
            for (String word: phrase.split("\\s+")) {
                String auxWord = word.replaceAll(SEP, " ");
                try {
                    Integer a = result.get(auxWord);
                    result.put(auxWord, a+1);
                } catch(NullPointerException e) {
                    result.put(auxWord, 1);
                }
            }
        }

        return result;
    }
}