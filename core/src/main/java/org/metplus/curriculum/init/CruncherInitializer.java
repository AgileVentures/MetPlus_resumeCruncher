package org.metplus.curriculum.init;

import org.apache.poi.ss.formula.functions.Match;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.cruncher.ResumeMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
public abstract class CruncherInitializer {
    @Autowired
    private CrunchersList crunchersList;

    @Autowired
    private MatcherList matchersList;
    /**
     * Function used to initialize the cruncher holder bean
     */
    @PostConstruct
    public void postContructor() {
        init();
        crunchersList.addCruncher(getCruncher());
        matchersList.addMatchers(getMatcher());
    }

    /**
     * Function called in the post constructor to initialize
     * all the needed information of the cruncher
     */
    public abstract void init();

    /**
     * Function used to retrieve the cruncher
     * @return Cruncher to be used
     */
    public abstract Cruncher getCruncher();


    /**
     * Function used to retrieve the matcher
     * @return Matcher to be used
     */
    public abstract ResumeMatcher getMatcher();
}
