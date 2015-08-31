package org.metplus.curriculum.cruncher;

import java.util.Hashtable;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
public interface Cruncher {
    Hashtable<String, Integer> calculate(String expression);
}
