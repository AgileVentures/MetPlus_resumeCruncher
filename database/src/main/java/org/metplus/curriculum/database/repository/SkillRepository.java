package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.Skill;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by joao on 5/28/16.
 */
public interface SkillRepository extends CrudRepository<Skill, String> {
    Skill findByName(String name);
}
