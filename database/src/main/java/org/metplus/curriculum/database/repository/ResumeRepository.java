package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.Resume;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface used on CRUD operations over Resumes
 */
public interface ResumeRepository extends CrudRepository<Resume, String>, ResumeCustomRepository {
    Resume findByUserId(String userId);
}
