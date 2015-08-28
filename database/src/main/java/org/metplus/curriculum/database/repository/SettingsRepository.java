package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.database.domain.Settings;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Joao Pereira on 25/08/2015.
 */
public interface SettingsRepository extends CrudRepository<Settings, String> {
    Setting findByName(String name);
}
