package org.metplus.curriculum.cruncher.expressionCruncher;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.metplus.curriculum.database.domain.Resume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Joao Pereira
 * Class that given a text count the words in it
 * Created by Joao Pereira on 23/07/2015.
 */
public class CruncherImpl implements Cruncher {
    private static final Logger logger = LoggerFactory.getLogger(CruncherImpl.class);
    public static final String CRUNCHER_NAME = "ExpressionCruncher";
    /**
     * Output saved
     */
    private Map<String, Integer> result = new HashMap<>();
    /**
     * List with all the expressions that can be merged
     */
    private Map<String, List<String>> mergeList = new HashMap<>();
    /**
     * List with all the expressions to be ignored
     */
    private List<String> ignoreList = new ArrayList<>();


    /**
     * If true when searching for items in the ignore list
     * assumes they are words
     */
    private boolean ignoreListSearchWord = false;

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
    public CruncherImpl(Map<String, List<String>> mergeList) {
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
    public CruncherImpl(List<String> ignoreList, Map<String, List<String>> mergeList) {
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
    public void setMergeList(Map<String, List<String>> mergeList) {
        this.mergeList = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> newVal: mergeList.entrySet()) {
            this.mergeList.put(newVal.getKey().replaceAll(" ", SEP), newVal.getValue());
        }
    }

    /**
     * Retrieve the list of merge expressions used
     * @return List of merge expressions
     */
    public Map<String, List<String>> getMergeList() {
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
    public Map<String, Integer> getResult() {
        return result;
    }

    /**
     * Check if the ignore list should be treated as a list
     * of words or a list of characters
     * If is a list of words while crunching adds a space in each side
     * @return True if list of words, false otherwise
     */
    public boolean isIgnoreListSearchWord() {
        return ignoreListSearchWord;
    }

    /**
     * Set if the ignore list should be treated as a list
     * of words or a list of characters
     * If is a list of words while crunching adds a space in each side
     * @param ignoreListSearchWord True if list of words, false otherwise
     */
    public void setIgnoreListSearchWord(boolean ignoreListSearchWord) {
        this.ignoreListSearchWord = ignoreListSearchWord;
    }

    /**
     * Method will generate a hash table with the expression
     * @param expression Expression to be checked
     * @return Hash table with the accumulated results
     */
    public Map<String, Integer> calculate(String expression) {
        logger.trace("calculate(" + expression + ")");
        String auxExpression = expression;

        // Remove the expressions that should be ignored
        for (String ignore: ignoreList) {
            if(isIgnoreListSearchWord())
                ignore = "\\b" + ignore + "\\b";
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

        result = new HashMap<>();
        // Do the reduce to words
        for (String phrase: auxExpression.split("\\.")) {
            for (String word: phrase.split("\\s+")) {
                String auxWord = word.replaceAll(SEP, " ");
                if (auxWord.length() > 0 && auxWord.charAt(0) != '$') {
                    try {
                        Integer a = result.get(auxWord);
                        result.put(auxWord, a + 1);
                    } catch (NullPointerException e) {
                        result.put(auxWord, 1);
                    }
                }
            }
        }
        logger.trace("result:" + result);

        return result;
    }

    @Override
    public CruncherMetaData crunch(String data) {
        Map<String, Integer> result = calculate(data);
        ExpressionCruncherMetaData allMetaData = new ExpressionCruncherMetaData();
        allMetaData.setMostReferedExpression("");
        int references = 0;
        for(Map.Entry<String, Integer> metaData: result.entrySet()) {
            MetaDataField<Integer> field = new MetaDataField<>(metaData.getValue());
            allMetaData.addField(metaData.getKey(), field);
            if(references < metaData.getValue().intValue()) {
                references = metaData.getValue();
                allMetaData.setMostReferedExpression(metaData.getKey());
            }
        }
        return allMetaData;
    }

    @Override
    public String getCruncherName() {
        return CRUNCHER_NAME;
    }
}