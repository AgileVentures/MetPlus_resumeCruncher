package org.metplus.curriculum.web;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class StarFormater {
    public static double format(double stars) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);

        return Double.parseDouble(df.format(new Double(stars)));
    }
}
