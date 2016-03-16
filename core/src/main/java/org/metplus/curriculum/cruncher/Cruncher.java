package org.metplus.curriculum.cruncher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
public interface Cruncher {
    CruncherMetaData crunch(String data);
    String getCruncherName();
}
