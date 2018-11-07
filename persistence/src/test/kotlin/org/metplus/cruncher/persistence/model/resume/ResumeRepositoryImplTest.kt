package org.metplus.cruncher.persistence.model.resume

import org.junit.jupiter.api.extension.ExtendWith
import org.metplus.cruncher.persistence.TestConfig
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.ResumeRepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
internal class ResumeRepositoryImplTest : ResumeRepositoryTest() {
    @Autowired
    lateinit var mongoRepository: ResumeRepository

    override fun getRepository(): ResumeRepository {
        return mongoRepository
    }
}