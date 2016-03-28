package org.metplus.curriculum.cruncher.expressionCruncher;

import org.metplus.curriculum.database.domain.MetaData;

/**
 * Created by joao on 3/21/16.
 */
public class ExpressionCruncherMetaData extends MetaData {
    public String getMostReferedExpression() {
        return mostReferedExpression;
    }

    public void setMostReferedExpression(String mostReferedExpression) {
        this.mostReferedExpression = mostReferedExpression;
    }

    private String mostReferedExpression;
}
