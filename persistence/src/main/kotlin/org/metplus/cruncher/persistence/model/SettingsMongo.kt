package org.metplus.cruncher.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigInteger

@Document
@TypeAlias("settings")
class SettingsMongo(
        @Id val id: BigInteger,
        val appSettingsMongo: SettingsListMongo
)

class SettingsListMongo(
        @Id val id: BigInteger,
        val settings: HashMap<String, SettingMongo<*>>,
        val mandatory: List<String>
)

class SettingMongo<DataType>(
        val name: String,
        val data: DataType
)