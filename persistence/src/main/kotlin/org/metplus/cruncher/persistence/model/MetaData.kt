package org.metplus.cruncher.persistence.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MetaData(
        var dataFields: HashMap<String, MetaDataField<*>>
)

@Document
data class MetaDataField<T>(var data: T)
