package org.metplus.cruncher.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigInteger

@Document
@TypeAlias("settings")
class Settings(
        @Id val id: BigInteger,
        val appSettings: SettingsList
)

class SettingsList(
        @Id val id: BigInteger,
        val settings: HashMap<String, Setting<*>>,
        val mandatory: List<String>
)

class Setting<DataType>(
        val name: String,
        val data: DataType
)