package org.metplus.curriculum.init;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
@Component
public abstract class CruncherInitializer {
    @Autowired
    private CrunchersList allCrunchers;
    /**
     * Function used to initialize the cruncher holder bean
     */
    @PostConstruct
    public void postContructor() {
        System.out.println("BAMMM=================================================");
        init();
        allCrunchers.addCruncher(getCruncher());
    }
    public abstract void init();

    /**
     * Function used to retrieve the cruncher
     * @return Cruncher to be used
     */
    public abstract Cruncher getCruncher();
}
