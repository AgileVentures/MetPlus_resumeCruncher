package org.metplus.curriculum.database.converters;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Setting;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Joao Pereira on 29/08/2015.
 */
public class ListToCruncherSettings implements Converter<BasicDBList, CruncherSettings> {
    /**
     * Convert the source of type S to target type T.
     *
     * @param source the source object to convert, which must be an instance of S (never {@code null})
     * @return the converted object, which must be an instance of T (potentially {@code null})
     * @throws IllegalArgumentException if the source could not be converted to the desired target type
     */
    @Override
    public CruncherSettings convert(BasicDBList source) {
        CruncherSettings result = new CruncherSettings();
        for(Object set: source) {

            result.addSetting((Setting)set);
        }
        return null;
    }
}
