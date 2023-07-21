import java.io.File
import java.util.Properties
import org.gradle.api.GradleException

object LocalProperties {

    const val PUBLISH_URI = "org.openwallet.kit.publish.uri"

    const val USER = "USER"
    const val PASSWORD = "PASSWORD"
    const val SIGNING_KEY_ID = "SIGNING_KEY_ID"
    const val SIGNING_PASSWORD = "SIGNING_PASSWORD"
    const val SIGNING_SECRET_KEY_RING_FILE = "SIGNING_SECRET_KEY_RING_FILE"

    private val props = mutableMapOf<String, Properties>()

    private fun getProperties(dir: File): Properties {
        return props.getOrPut(dir.canonicalPath) {
            val localPropertiesFile = dir.resolve("local.properties")
            val properties = Properties()

            if (localPropertiesFile.exists()) {
                properties.load(localPropertiesFile.inputStream())
            }

            properties
        }
    }

    fun find(dir: File, key: String): String {
        return getProperties(dir).getProperty(key) ?: ""
    }

    fun get(dir: File, key: String): String {
        return getProperties(dir).getProperty(key)
            ?: throw GradleException(
                "Key `$key` is undefine, Please add it to local.properties! " +
                    "Check README.MD -> Building#3 to get more info.",
            )
    }
}
