package org.metplus.curriculum.init;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
public abstract class CruncherInitializer {
    @Autowired
    private CrunchersList crunchersList;
    /**
     * Function used to initialize the cruncher holder bean
     */
    @PostConstruct
    public void postContructor() {
        init();
        crunchersList.addCruncher(getCruncher());
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
}
