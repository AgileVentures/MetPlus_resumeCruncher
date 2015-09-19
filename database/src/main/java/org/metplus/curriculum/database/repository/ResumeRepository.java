package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.CruncherSettings;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface used on CRUD operations over Resumes
 */
public interface ResumeRepository extends CrudRepository<CruncherSettings, String> {
}
