package org.metplus.curriculum.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.metplus.curriculum.database.template.TemplatePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joao Pereira on 19/08/2015.
 */
@Document
public class CruncherSettings extends SettingsList {
    private static final Logger LOG = LoggerFactory.getLogger(CruncherSettings.class);
    private final String NAME_SETTING = "Name";
    public CruncherSettings() {
        super();
        addMandatorySetting(NAME_SETTING);
    }

    public CruncherSettings(String name) {
        super();
        addMandatorySetting(NAME_SETTING);
        Setting setting = new Setting<>(NAME_SETTING, name);
        addSetting(setting);
    }
    @JsonIgnore
    public String getName() {
        return (String)getSetting(NAME_SETTING).getData();
    }
    public void setName(String name) {
        addSetting(new Setting<>(NAME_SETTING, name));
    }
}
