package org.metplus.curriculum.cruncher;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao on 3/16/16.
 */
@Component("crunchersList")
public class CrunchersList {

    List<Cruncher> allCrunchers;

    /**
     * Add a new cruncher to the list of crunchers
     * @param cruncher Cruncher to be added
     */
    public void addCruncher(Cruncher cruncher) {
        getCrunchers().add(cruncher);
    }

    /**
     * Retrieve all crunchers
     * @return List with all the crunchers registered
     */
    public List<Cruncher> getCrunchers() {
        if(allCrunchers == null)
            allCrunchers = new ArrayList<>();
        return allCrunchers;
    }
}
