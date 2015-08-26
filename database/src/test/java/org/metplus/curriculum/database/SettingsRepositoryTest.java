package org.metplus.curriculum.database;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Joao Pereira on 25/08/2015.
 */
public class SettingsRepositoryTest {
    @Autowired
    private SettingsRepository repository;
    @Before
    public void setUp(){
        repository.deleteAll();
    }
    @Test
    public void testFindByName() throws Exception {
        repository.save(new Settings());
        assertEquals(1, repository.findAll().size());
        Settings setting = repository.findAll().get(0);
        CruncherSettings.Setting<Integer> set = new CruncherSettings.Setting<>("Bamm", 10);
        setting.addSetting(set);
        repository.save(setting);assertEquals(1, repository.findAll().size());
        setting = repository.findAll().get(0);
        assertEquals(10, setting.getSetting("Bamm"));
    }
}