package org.metplus.curriculum.cruncher.naivebayes;


import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;

/**
 * Created by joao on 5/28/16.
 */
public class CruncherImpl implements Cruncher {
    public static final String CRUNCHER_NAME = "NaiveBayes";
    public CruncherMetaData crunch(String data) {
        return null;
    }

    public String getCruncherName() {
        return CRUNCHER_NAME;
    }
}
