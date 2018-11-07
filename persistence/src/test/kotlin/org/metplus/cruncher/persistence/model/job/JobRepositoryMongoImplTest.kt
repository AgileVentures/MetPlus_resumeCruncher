package org.metplus.cruncher.persistence.model.job

import org.junit.jupiter.api.extension.ExtendWith
import org.metplus.cruncher.job.JobRepositoryTest
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.persistence.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class JobRepositoryMongoImplTest : JobRepositoryTest() {
    @Autowired
    lateinit var mongoRepository: JobsRepository

    override fun getJobRepository(): JobsRepository {
        return mongoRepository
    }
}