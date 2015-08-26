package org.metplus.curriculum.database;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Joao Pereira on 25/08/2015.
 */
public interface SettingsRepository extends MongoRepository<Settings, String> {
    CruncherSettings.Setting findByName(String name);
}
