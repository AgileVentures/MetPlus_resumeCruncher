package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.Job;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface of the repository that will handle the CRUD operations
 * on Jobs
 */
public interface JobRepository extends CrudRepository<Job, String> {
    Job findByJobId(String jobId);
}
