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
    public void addCruncher(Cruncher cruncher) {
        getCrunchers().add(cruncher);
    }
    public List<Cruncher> getCrunchers() {
        if(allCrunchers == null)
            allCrunchers = new ArrayList<>();
        return allCrunchers;
    }
}
