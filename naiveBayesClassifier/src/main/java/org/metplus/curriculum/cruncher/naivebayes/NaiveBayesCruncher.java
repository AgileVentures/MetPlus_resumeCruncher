package org.metplus.curriculum.cruncher.naivebayes;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.metplus.curriculum.init.CruncherInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class used for initialization of the cruncher based on a
 * Naive Bayes Classifier
 */
@Component
@ConfigurationProperties(locations = "classpath:naiveBayes.yml", prefix = "config")
public class NaiveBayesCruncher extends CruncherInitializer {

    private static final String LEARN_DATABASE = "LearnDatabase";
    private static final String CLEAN_EXPRESSIONS = "CleanExpressions";

    private CruncherImpl cruncherImpl;
    private Map<String, List<String>> learnDatabase;
    private List<String> cleanExpressions;

    public Map<String, List<String>> getLearnDatabase() {
        return learnDatabase;
    }

    public void setLearnDatabase(Map<String, List<String>> learnDatabase) {
        this.learnDatabase = learnDatabase;
    }


    public List<String> getCleanExpressions() {
        return cleanExpressions;
    }

    public void setCleanExpressions(List<String> cleanExpressions) {
        this.cleanExpressions = cleanExpressions;
    }


    @Autowired
    private SettingsRepository repository;

    public NaiveBayesCruncher() {

    }

    public NaiveBayesCruncher(SettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void init() {
        try {
            cruncherImpl = new CruncherImpl();
            load();
        } catch (CruncherSettingsNotFound cruncherSettingsNotFound) {
            save();
        }
    }

    public void reload() {
        try {
            load();
        } catch (CruncherSettingsNotFound cruncherSettingsNotFound) {
        }
    }

    private void load() throws CruncherSettingsNotFound{
        cruncherImpl.resetMemory();
        try {
            CruncherSettings settings;
            try {
                settings = repository.findAll().iterator().next().getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
            } catch(NoSuchElementException e) {
                settings = new CruncherSettings(CruncherImpl.CRUNCHER_NAME);
                Settings globalSettings = repository.findAll().iterator().next();
                globalSettings.addCruncherSettings(CruncherImpl.CRUNCHER_NAME, settings);
                repository.save(globalSettings);
            }

            learnDatabase = (Map)settings.getSetting(LEARN_DATABASE).getData();
            cruncherImpl.train(learnDatabase);
        } finally {
            learnDatabase = null;
        }
    }

    public void save() {
        CruncherSettings cSettings;
        try {
            cSettings = repository.findAll().iterator().next().getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
        } catch (CruncherSettingsNotFound cruncherSettingsNotFound) {
            cSettings = new CruncherSettings(CruncherImpl.CRUNCHER_NAME);
        }
        cSettings.addSetting(new Setting<>(LEARN_DATABASE, learnDatabase));
        Settings globalSettings = repository.findAll().iterator().next();
        globalSettings.addCruncherSettings(CruncherImpl.CRUNCHER_NAME, cSettings);
        repository.save(globalSettings);
    }

    @Override
    public Cruncher getCruncher() {
        return cruncherImpl;
    }

    @Override
    public Matcher getMatcher() {
        return null;
    }
}
