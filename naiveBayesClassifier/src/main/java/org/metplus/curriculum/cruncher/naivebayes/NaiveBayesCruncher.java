package org.metplus.curriculum.cruncher.naivebayes;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.init.CruncherInitializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by joao on 5/28/16.
 */
@Component
@ConfigurationProperties(locations = "classpath:expressionCruncher.yml", prefix = "config")
public class NaiveBayesCruncher extends CruncherInitializer {

    private CruncherImpl cruncherImpl;
    @Override
    public void init() {

    }

    @Override
    public Cruncher getCruncher() {
        return cruncherImpl;
    }

    @Override
    public Matcher getMatcher() {
        return null;
    }
}
