package org.metplus.cruncher.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "resume")
data class ResumeMongo(
        @Field("id")
        @Id val id: String,
        @Field
        var filename: String,
        @Field
        var fileType: String,
        @Field("cruncherData")
        var cruncherData: Map<String, MetaData>?
)