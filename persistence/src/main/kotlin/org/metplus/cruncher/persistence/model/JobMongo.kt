package org.metplus.cruncher.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document
@TypeAlias("job")
class JobMongo(
        @Field("jobId")
        @Id val id: String,
        val title: String,
        val description: String
)
