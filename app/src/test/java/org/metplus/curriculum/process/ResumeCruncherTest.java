package org.metplus.curriculum.process;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao on 3/17/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ResumeCruncherTest.Run.class})
public class ResumeCruncherTest {
    @RunWith(MockitoJUnitRunner.class)
    public static class Run {
        @Mock
        private CrunchersList allCrunchers;
        @Mock
        private SpringMongoConfig springMongoConfig;
        @Mock
        private ResumeRepository resumeRepository;
        @InjectMocks
        private ResumeCruncher cruncher;

        @Mock
        private Cruncher cruncherImpl;
        @Test
        public void noResumes() throws InterruptedException {
            cruncher.postConstructor();
            cruncher.stop();
            cruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(0)).getCrunchers();
            Mockito.verify(resumeRepository, Mockito.times(0)).save(Mockito.any(Resume.class));
        }
        @Test
        public void oneResume() throws ResumeNotFound, ResumeReadException, InterruptedException {
            Resume resume = Mockito.mock(Resume.class);
            Mockito.when(resume.getResume(springMongoConfig)).thenReturn(new ByteArrayOutputStream());
            List<Cruncher> listCrunchers = new ArrayList<>();
            listCrunchers.add(cruncherImpl);
            Mockito.when(allCrunchers.getCrunchers()).thenReturn(listCrunchers);
            cruncher.postConstructor();
            cruncher.addWork(resume);
            cruncher.stop();
            cruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(1)).getCrunchers();
            Mockito.verify(resumeRepository, Mockito.times(1)).save(resume);
            Mockito.verify(resume).getResume(springMongoConfig);
        }
        @Test
        public void twoResume() throws ResumeNotFound, ResumeReadException, InterruptedException {
            Resume resume = Mockito.mock(Resume.class);
            Mockito.when(resume.getResume(springMongoConfig)).thenReturn(new ByteArrayOutputStream());
            Resume resume1 = Mockito.mock(Resume.class);
            Mockito.when(resume1.getResume(springMongoConfig)).thenReturn(new ByteArrayOutputStream());
            List<Cruncher> listCrunchers = new ArrayList<>();
            listCrunchers.add(cruncherImpl);
            Mockito.when(allCrunchers.getCrunchers()).thenReturn(listCrunchers);
            cruncher.postConstructor();
            cruncher.addWork(resume);
            cruncher.addWork(resume1);
            cruncher.stop();
            cruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(2)).getCrunchers();
            Mockito.verify(resumeRepository).save(resume);
            Mockito.verify(resumeRepository).save(resume1);
            Mockito.verify(resume).getResume(springMongoConfig);
            Mockito.verify(resume1).getResume(springMongoConfig);
        }
        @Test
        public void unableToFindResumeFile() throws ResumeNotFound, ResumeReadException, InterruptedException {
            Resume resume = Mockito.mock(Resume.class);
            Mockito.when(resume.getResume(springMongoConfig)).thenThrow(new ResumeNotFound(""));
            List<Cruncher> listCrunchers = new ArrayList<>();
            listCrunchers.add(cruncherImpl);
            cruncher.postConstructor();
            cruncher.addWork(resume);
            cruncher.stop();
            cruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(0)).getCrunchers();
            Mockito.verify(resumeRepository, Mockito.times(0)).save(resume);
            Mockito.verify(resume).getResume(springMongoConfig);
        }
        @Test
        public void unableToReadResumeFile() throws ResumeNotFound, ResumeReadException, InterruptedException {
            Resume resume = Mockito.mock(Resume.class);
            Mockito.when(resume.getResume(springMongoConfig)).thenThrow(new ResumeReadException(""));
            List<Cruncher> listCrunchers = new ArrayList<>();
            listCrunchers.add(cruncherImpl);
            cruncher.postConstructor();
            cruncher.addWork(resume);
            cruncher.stop();
            cruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(0)).getCrunchers();
            Mockito.verify(resumeRepository, Mockito.times(0)).save(resume);
            Mockito.verify(resume).getResume(springMongoConfig);
        }
    }
}
