package org.metplus.curriculum.cruncher;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
public interface Cruncher {
    /**
     * Function that will do the work of generating
     * the meta data associated with a input
     * @param data Input to be processed
     * @return Processed data
     */
    CruncherMetaData crunch(String data);

    /**
     * Retrieve the name of the cruncher
     * @return Name of the cruncher
     */
    String getCruncherName();

}
