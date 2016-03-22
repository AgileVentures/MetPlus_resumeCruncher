package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.Resume;

import java.util.Comparator;
import java.util.List;

/**
 * Created by joao on 3/20/16.
 */
public interface ResumeCustomRepository {
    List<Resume> resumesOnCriteria(Comparator<Resume> orderResumes);
}
