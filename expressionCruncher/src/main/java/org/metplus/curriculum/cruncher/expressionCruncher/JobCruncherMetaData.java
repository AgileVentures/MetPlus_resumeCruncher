package org.metplus.curriculum.cruncher.expressionCruncher;

import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.MetaDataField;

import java.util.Map;

/**
 * Created by joao on 3/25/16.
 */
public class JobCruncherMetaData extends MetaData {
    public void setTitleData(ExpressionCruncherMetaData metaData) {
        MetaDataField<MetaData> dataField = new MetaDataField<>(metaData);
        addField("bamm", dataField);
    }
    public Map<String, MetaDataField> getTitleData() {
        MetaDataField<MetaData> dataField = getFields().get("bamm");
        if(dataField == null)
            return null;

        return dataField.getData().getFields();
    }
}
