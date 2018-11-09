package org.metplus.cruncher.persistence.model

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.GridFSBuckets
import org.metplus.cruncher.resume.ResumeFile
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeNotFound
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.gridfs.GridFsResource
import org.springframework.data.mongodb.gridfs.GridFsTemplate

class ResumeFileRepositoryImpl(
        private val mongoDbFactory: MongoDbFactory,
        private val mappingMongoConverter: MappingMongoConverter) : ResumeFileRepository {
    override fun deleteIfExists(userId: String) {
        val query = generateQueryToFindFile(userId)
        val fileStore = GridFsTemplate(
                mongoDbFactory, mappingMongoConverter, "filestore")
        fileStore.delete(query)
    }

    override fun save(resumeFile: ResumeFile): ResumeFile {
        try {

            val fileStore = GridFsTemplate(
                    mongoDbFactory, mappingMongoConverter, "filestore")

            val query = generateQueryToFindFile(resumeFile.userId)
            fileStore.delete(query)

            val metaData = BasicDBObject()
            metaData["userid"] = resumeFile.userId
            fileStore.store(resumeFile.fileStream, resumeFile.filename, metaData)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return resumeFile
    }

    private fun generateQueryToFindFile(id: String) =
            Query(Criteria.where("metadata.userid").isEqualTo(id))

    override fun getByUserId(userId: String): ResumeFile {
        val query = generateQueryToFindFile(userId)
        val fileStore = GridFsTemplate(
                mongoDbFactory, mappingMongoConverter, "filestore")
        val gridFile = fileStore.findOne(query) ?: throw ResumeNotFound("Resume for user '$userId' not found")

        val bucket = GridFSBuckets.create(mongoDbFactory.db, "filestore")
        val resource = GridFsResource(gridFile, bucket.openDownloadStream(gridFile.objectId))

        return ResumeFile(
                filename = resource.filename,
                userId = userId,
                fileStream = resource.inputStream
        )
    }
}
