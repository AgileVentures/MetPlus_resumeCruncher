package org.metplus.curriculum.cruncher.expressionCruncher;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.database.domain.*;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.metplus.curriculum.init.CruncherInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
@Component
@ConfigurationProperties(locations = "classpath:expressionCruncher.yml",prefix="config")
public class ExpressionCruncher extends CruncherInitializer {
    private static final String IGNORE_LIST = "IgnoreList";
    private static final String MERGE_LIST = "MergeList";
    private static final String CASE_SENSITIVE = "CaseSensitive";

    private boolean caseSensitive;
    private Map<String, List<String>> mergeList;
    private List<String> ignoreList;

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Map<String, List<String>> getMergeList() {
        return mergeList;
    }

    public void setMergeList(Map<String, List<String>> mergeList) {
        this.mergeList = mergeList;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    private CruncherImpl cruncherImpl;
    @Autowired private SettingsRepository repository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Override
    public void init() {
        try {
            CruncherSettings settings = null;
            try {
                System.out.println("TESTING=======================================");
                settings = repository.findAll().iterator().next().getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
            } catch(NoSuchElementException e) {
                repository.save(new Settings());
                settings = repository.findAll().iterator().next().getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
            }
            ignoreList = (List)settings.getSetting(IGNORE_LIST).getData();
            caseSensitive = (boolean)settings.getSetting(CASE_SENSITIVE).getData();
            mergeList = (Map) settings.getSetting(MERGE_LIST).getData();
            System.out.println("===================================================================");
            System.out.println("Case sensitive: " + caseSensitive);
            System.out.println("Ignore list: " + ignoreList);
            System.out.println("Merge List: " + mergeList);
            System.out.println("===================================================================");

            cruncherImpl = new CruncherImpl(ignoreList, mergeList);
            cruncherImpl.setCaseSensitive(caseSensitive);
        } catch (CruncherSettingsNotFound cruncherSettingsNotFound) {
            System.out.println("===================================================================");
            System.out.println("Case sensitive: " + caseSensitive);
            System.out.println("Ignore list: " + ignoreList);
            System.out.println("Merge List: " + mergeList);
            System.out.println("===================================================================");
            cruncherImpl = new CruncherImpl(ignoreList, mergeList);
            cruncherImpl.setCaseSensitive(caseSensitive);
            save();
        }
    }

    /**
     * Function used to retrieve the cruncher
     *
     * @return Cruncher to be used
     */
    @Override
    public Cruncher getCruncher() {
        return cruncherImpl;
    }

    /**
     * Save the settings of the cruncher to the database
     */
    public void save() {
        CruncherSettings cSettings = null;
        try {
            cSettings = repository.findAll().iterator().next().getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
        } catch (CruncherSettingsNotFound cruncherSettingsNotFound) {
            cSettings = new CruncherSettings(CruncherImpl.CRUNCHER_NAME);
        }
        cSettings.addSetting(new Setting<>(CASE_SENSITIVE, cruncherImpl.isCaseSensitive()));
        cSettings.addSetting(new Setting<>(IGNORE_LIST, cruncherImpl.getIgnoreList()));
        cSettings.addSetting(new Setting<>(MERGE_LIST, cruncherImpl.getMergeList()));
        Settings globalSettings = repository.findAll().iterator().next();
        globalSettings.addCruncherSettings(CruncherImpl.CRUNCHER_NAME, cSettings);
        repository.save(globalSettings);
    }

    public void check()  {
        resumeRepository.resumesOnCriteria(new Comparator<Resume>() {
            @Override
            public int compare(Resume o1, Resume o2) {
                o1.getCruncherData(getCruncher().getCruncherName()).getOrderedFields(new Comparator<Map.Entry<String, MetaDataField>>() {
                    @Override
                    public int compare(Map.Entry<String, MetaDataField> o1, Map.Entry<String, MetaDataField> o2) {
                        o1.get
                        return 0;
                    }
                })
                return 0;
            }
        })
    }
}
