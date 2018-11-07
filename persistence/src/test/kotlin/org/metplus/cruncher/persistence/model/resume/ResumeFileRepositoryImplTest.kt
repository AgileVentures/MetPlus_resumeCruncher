package org.metplus.cruncher.persistence.model.resume

import org.junit.jupiter.api.extension.ExtendWith
import org.metplus.cruncher.persistence.TestConfig
import org.metplus.cruncher.persistence.model.ResumeFileRepositoryImpl
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeFileRepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class ResumeFileRepositoryImplTest(
        @Autowired private val simpleMongoDbFactory: SimpleMongoDbFactory,
        @Autowired private val mappingMongoConverter: MappingMongoConverter
) : ResumeFileRepositoryTest() {
    private var resumeFileRepository: ResumeFileRepositoryImpl = ResumeFileRepositoryImpl(simpleMongoDbFactory, mappingMongoConverter)
    override fun getRepository(): ResumeFileRepository {
        return resumeFileRepository
    }
}