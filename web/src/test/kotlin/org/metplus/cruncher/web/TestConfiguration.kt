package org.metplus.cruncher.web

import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.job.MatchWithResume
import org.metplus.cruncher.job.UpdateJob
import org.metplus.cruncher.rating.CrunchJobProcessSpy
import org.metplus.cruncher.rating.CrunchResumeProcess
import org.metplus.cruncher.rating.CrunchResumeProcessSpy
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.rating.ProcessCruncher
import org.metplus.cruncher.resume.DownloadResume
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeFileRepositoryFake
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.ResumeRepositoryFake
import org.metplus.cruncher.resume.UploadResume
import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.SaveSettings
import org.metplus.cruncher.settings.SettingsRepository
import org.metplus.cruncher.settings.SettingsRepositoryFake
import org.metplus.cruncher.web.security.services.LocalTokenService
import org.metplus.cruncher.web.security.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootConfiguration
@EnableWebMvc
open class TestConfiguration {
    @Bean
    open fun getSettingsRepository(): SettingsRepository = SettingsRepositoryFake()

    @Bean
    open fun getJobRepository(): JobsRepository = JobRepositoryFake()

    @Bean
    open fun getResumeRepository(): ResumeRepository = ResumeRepositoryFake()

    @Bean
    open fun getResumeFileRepository(): ResumeFileRepository = ResumeFileRepositoryFake()

    @Bean
    open fun getSettings(): GetSettings = GetSettings(getSettingsRepository())

    @Bean
    open fun saveSettings(): SaveSettings = SaveSettings(getSettingsRepository())

    @Bean
    open fun createJob(
            @Autowired jobsRepository: JobsRepository,
            @Autowired crunchJobProcessSpy: ProcessCruncher<Job>
    ): CreateJob = CreateJob(jobsRepository, crunchJobProcessSpy)

    @Bean
    open fun updateJob(@Autowired jobsRepository: JobsRepository,
                       @Autowired crunchJobProcessSpy: ProcessCruncher<Job>
    ): UpdateJob = UpdateJob(jobsRepository, crunchJobProcess())

    @Bean
    open fun matchWithResume(@Autowired resumeRepository: ResumeRepository,
                             @Autowired jobsRepository: JobsRepository,
                             @Autowired matcher: Matcher<Resume, Job>
    ): MatchWithResume = MatchWithResume(resumeRepository, jobsRepository, matcher)

    @Bean
    open fun tokenService(): TokenService = LocalTokenService()

    @Bean
    open fun uploadResume(
            @Autowired resumeRepository: ResumeRepository,
            @Autowired resumeFileRepository: ResumeFileRepository,
            @Autowired crunchResumeProcess: ProcessCruncher<Resume>
    ): UploadResume = UploadResume(resumeRepository, resumeFileRepository, crunchResumeProcess)

    @Bean
    open fun downloadResume(
            @Autowired resumeRepository: ResumeRepository,
            @Autowired resumeFileRepository: ResumeFileRepository
    ): DownloadResume = DownloadResume(resumeRepository, resumeFileRepository)

    @Bean
    open fun crunchResumeProcess(): ProcessCruncher<Resume> = CrunchResumeProcessSpy()

    @Bean
    open fun crunchJobProcess(): ProcessCruncher<Job> = CrunchJobProcessSpy()

    @Bean
    open fun matcher(): Matcher<Resume, Job> = MatcherStub()
}