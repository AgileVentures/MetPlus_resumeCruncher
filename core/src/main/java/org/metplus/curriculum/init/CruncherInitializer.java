package org.metplus.curriculum.init;

import org.metplus.curriculum.cruncher.Cruncher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
@Component
public abstract class CruncherInitializer {
    /**
     * Function used to initialize the cruncher holder bean
     */
    @PostConstruct
    public abstract void init();

    /**
     * Function used to retrieve the cruncher
     * @return Cruncher to be used
     */
    public abstract Cruncher getCruncher();
}
