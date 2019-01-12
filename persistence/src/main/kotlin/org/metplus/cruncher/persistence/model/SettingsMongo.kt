package org.metplus.cruncher.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigInteger

@Document(collection = "settings")
class SettingsMongo(
        @Id val id: BigInteger,
        val appSettingsMongo: SettingsListMongo,
        val cruncherSettingsMongo: CruncherSettingsMongo
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

class CruncherSettingsMongo(
        val database: Map<String, List<String>>,
        val cleanExpressions: List<String>
)