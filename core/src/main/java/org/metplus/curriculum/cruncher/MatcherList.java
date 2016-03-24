package org.metplus.curriculum.cruncher;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao on 3/22/16.
 * Component that will store all the matchers
 */
@Component("matchersList")
public class MatcherList {

    List<Matcher> matchers;

    /**
     * All the matchers
     * @return List of all the matchers
     */
    public List<Matcher> getMatchers() {
        if(matchers == null)
            matchers = new ArrayList<>();
        return matchers;
    }

    /**
     * Add a new matcher
     * @param matcher Matcher
     */
    public void addMatchers(Matcher matcher) {
        getMatchers().add(matcher);
    }
}
