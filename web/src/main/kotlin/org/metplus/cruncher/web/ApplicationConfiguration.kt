package org.metplus.cruncher.web

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.WriteConcern
import org.metplus.cruncher.canned.job.MatchWithResumeCanned
import org.metplus.cruncher.canned.rating.CompareResumeWithJobCanned
import org.metplus.cruncher.canned.resume.MatchWithJobCanned
import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.job.MatchWithResume
import org.metplus.cruncher.job.ReCrunchAllJobs
import org.metplus.cruncher.job.UpdateJob
import org.metplus.cruncher.persistence.model.JobRepositoryImpl
import org.metplus.cruncher.persistence.model.JobRepositoryMongo
import org.metplus.cruncher.persistence.model.ResumeFileRepositoryImpl
import org.metplus.cruncher.persistence.model.ResumeRepositoryImpl
import org.metplus.cruncher.persistence.model.ResumeRepositoryMongo
import org.metplus.cruncher.persistence.model.SettingsRepositoryImpl
import org.metplus.cruncher.persistence.model.SettingsRepositoryMongo
import org.metplus.cruncher.rating.CompareResumeWithJob
import org.metplus.cruncher.rating.CrunchJobProcess
import org.metplus.cruncher.rating.CrunchResumeProcess
import org.metplus.cruncher.rating.CruncherList
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.rating.ProcessCruncher
import org.metplus.cruncher.rating.TrainCruncher
import org.metplus.cruncher.rating.TrainCruncherObserver
import org.metplus.cruncher.resume.DownloadResume
import org.metplus.cruncher.resume.MatchWithJob
import org.metplus.cruncher.resume.ReCrunchAllResumes
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.UploadResume
import org.metplus.cruncher.settings.CruncherSettings
import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.SaveSettings
import org.metplus.cruncher.web.rating.AsyncJobProcess
import org.metplus.cruncher.web.rating.AsyncResumeProcess
import org.metplus.curriculum.cruncher.naivebayes.CruncherImpl
import org.metplus.curriculum.cruncher.naivebayes.MatcherImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.*

@SpringBootConfiguration
@EnableConfigurationProperties
@EnableMongoRepositories(basePackages = ["org.metplus.cruncher.persistence"])
open class ApplicationConfiguration {
    @Bean
    open fun getSettingsRepository(
            @Autowired settingsRepositoryMongo: SettingsRepositoryMongo
    ) = SettingsRepositoryImpl(settingsRepository = settingsRepositoryMongo)

    @Bean
    open fun getSettings(
            @Autowired settingsRepository: SettingsRepositoryImpl
    ) = GetSettings(settingsRepository = settingsRepository)

    @Bean
    open fun saveSettings(
            @Autowired settingsRepository: SettingsRepositoryImpl
    ) = SaveSettings(settingsRepository = settingsRepository)

    @Bean
    open fun getJobRepository(
            @Autowired jobRepositoryMongo: JobRepositoryMongo
    ): JobsRepository = JobRepositoryImpl(jobRepository = jobRepositoryMongo)

    @Bean
    open fun getResumeRepository(
            @Autowired resumeRepositoryMongo: ResumeRepositoryMongo
    ): ResumeRepository = ResumeRepositoryImpl(resumeRepositoryMongo)

    @Bean
    open fun getResumeFileRepository(
            @Autowired mongoDbFactory: MongoDbFactory,
            @Autowired mappingMongoConverter: MappingMongoConverter
    ): ResumeFileRepository = ResumeFileRepositoryImpl(
            mongoDbFactory = mongoDbFactory,
            mappingMongoConverter = mappingMongoConverter
    )

    @Bean
    open fun createJob(
            @Autowired jobsRepository: JobsRepository,
            @Autowired crunchJobProcess: CrunchJobProcess
    ): CreateJob = CreateJob(jobsRepository, crunchJobProcess)

    @Bean
    open fun updateJob(
            @Autowired jobsRepository: JobsRepository,
            @Autowired crunchJobProcess: CrunchJobProcess
    ): UpdateJob = UpdateJob(jobsRepository, crunchJobProcess)


    @Bean
    open fun reCrunchAllJobs(
            jobsRepository: JobsRepository,
            jobProcess: ProcessCruncher<Job>) = ReCrunchAllJobs(jobsRepository, jobProcess)

    @Bean
    open fun reCrunchAllResumes(
            resumeRepository: ResumeRepository,
            resumeProcess: ProcessCruncher<Resume>) = ReCrunchAllResumes(resumeRepository, resumeProcess)

    @Bean
    open fun matchWithResume(@Autowired resumeRepository: ResumeRepository,
                             @Autowired jobsRepository: JobsRepository,
                             @Autowired matcher: Matcher<Resume, Job>
    ): MatchWithResume = MatchWithResume(resumeRepository, jobsRepository, matcher)

    @Bean
    open fun matchWithResumeCanned(@Autowired jobsRepository: JobsRepository):
            MatchWithResumeCanned = MatchWithResumeCanned(jobsRepository)

    @Bean
    open fun uploadResume(
            @Autowired resumeRepository: ResumeRepository,
            @Autowired resumeFileRepository: ResumeFileRepository,
            @Autowired crunchResumeProcess: CrunchResumeProcess
    ): UploadResume = UploadResume(resumeRepository, resumeFileRepository, crunchResumeProcess)

    @Bean
    open fun downloadResume(
            @Autowired resumeRepository: ResumeRepository,
            @Autowired resumeFileRepository: ResumeFileRepository
    ): DownloadResume = DownloadResume(resumeRepository, resumeFileRepository)

    @Bean
    open fun matchWithJob(@Autowired resumeRepository: ResumeRepository,
                          @Autowired jobsRepository: JobsRepository,
                          @Autowired matcher: Matcher<Resume, Job>
    ): MatchWithJob = MatchWithJob(resumeRepository, jobsRepository, matcher)

    @Bean
    open fun matchWithJobCanned(@Autowired resumeRepository: ResumeRepository
    ): MatchWithJobCanned = MatchWithJobCanned(resumeRepository)

    @Bean
    open fun allCrunchers(
            naiveBayesCruncherImpl: CruncherImpl,
            trainCruncher: TrainCruncher
    ): CruncherList {
        trainCruncher.process(observer = object : TrainCruncherObserver {
            override fun onSuccess() {
            }
        })
        return CruncherList(listOf(naiveBayesCruncherImpl))
    }

    @Bean
    open fun asyncResumeProcess(
            @Autowired allCrunchers: CruncherList,
            @Autowired resumeRepository: ResumeRepository,
            @Autowired resumeFileRepository: ResumeFileRepository
    ) = AsyncResumeProcess(allCrunchers, resumeRepository, resumeFileRepository)

    @Bean
    open fun asyncJobProcess(
            @Autowired allCrunchers: CruncherList,
            @Autowired jobsRepository: JobsRepository
    ) = AsyncJobProcess(allCrunchers, jobsRepository)

    @Bean
    open fun matcher(): Matcher<Resume, Job> = MatcherImpl()

    @Bean
    open fun naiveBayesImpl() = CruncherImpl()

    @Bean
    open fun cruncherTrainer(
            @Autowired settingsRepository: SettingsRepositoryImpl,
            @Autowired cruncherImpl: CruncherImpl,
            @Autowired cruncherConfiguration: CruncherConfiguration
    ) = TrainCruncher(settingsRepository, cruncherImpl, cruncherConfiguration.cruncherSettings)

    @Bean
    open fun compareResumeWithJob(
            jobsRepository: JobsRepository,
            resumeRepository: ResumeRepository,
            matcher: Matcher<Resume, Job>
    ) = CompareResumeWithJob(jobsRepository, resumeRepository, matcher)

    @Bean
    open fun compareResumeWithJobCanned(
            compareResumeWithJob: CompareResumeWithJob
    ) = CompareResumeWithJobCanned(compareResumeWithJob)
}

@ConfigurationProperties(prefix = "naive-bayes")
@Configuration
open class CruncherConfiguration(
        var learnDatabase: Map<String, List<String>> = mutableMapOf(),
        var cleanExpressions: List<String> = mutableListOf()
) {
    val cruncherSettings = CruncherSettings(this.learnDatabase, this.cleanExpressions)
}