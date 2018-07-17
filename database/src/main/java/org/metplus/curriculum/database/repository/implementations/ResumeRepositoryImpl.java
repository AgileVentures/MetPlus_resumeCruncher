package org.metplus.curriculum.database.repository.implementations;

import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.ResumeCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Comparator;
import java.util.List;

/**
 * Created by joao on 3/20/16.
 */
public class ResumeRepositoryImpl implements ResumeCustomRepository {
    @Autowired
    private MongoOperations operations;

    @Override
    public List<Resume> resumesOnCriteria(Comparator<Resume> orderResumes) {
        List<Resume> result = operations.findAll(Resume.class);
        result.sort(orderResumes);
        return result;
    }
}
