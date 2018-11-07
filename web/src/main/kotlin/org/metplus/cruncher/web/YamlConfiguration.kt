package org.metplus.cruncher.web

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.net.URISyntaxException

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "database-pets")
open class DatabaseConfig {
    lateinit var name: String
    lateinit var username: String
    lateinit var password: String
    var port: Int = 0
    lateinit var host: String
    var uri: String? = null
        set(uri) {
            field = uri
            try {
                val uriMongo = URI(this.uri!!)
                this.host = uriMongo.host
                this.port = uriMongo.port
                this.username = uriMongo.userInfo.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                this.password = uriMongo.userInfo.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                this.name = uriMongo.path.substring(1)

            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

        }

    fun asAuthentication(): Boolean {
        return username != null
    }
}
