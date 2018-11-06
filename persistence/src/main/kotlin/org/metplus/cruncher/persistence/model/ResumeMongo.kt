package org.metplus.cruncher.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document
@TypeAlias("resume")
data class ResumeMongo(
        @Field("jobId")
        @Id val id: String,
        @Field
        var filename: String,
        @Field
        var fileType: String
)