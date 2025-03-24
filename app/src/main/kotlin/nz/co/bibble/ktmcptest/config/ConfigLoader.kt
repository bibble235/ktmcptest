package nz.co.bibble.ktmcptest.config

import io.github.cdimascio.dotenv.Dotenv

object ConfigLoader {
    private val dotenv =
        Dotenv.configure()
            .directory("/home/iwiseman/dev/projects/mcpServer/ktMcpTest") // Specify the directory containing the .env file
            .ignoreIfMissing() // Optional: Ignore if the .env file is missing
            .load()

    fun loadConfig(): AppConfig {
        val bridgeIp = dotenv["HUE_BRIDGE_IP"] ?: error("HUE_BRIDGE_IP is not set in the .env file")
        val applicationKey =
            dotenv["HUE_APPLICATION_KEY"]
                ?: error("HUE_APPLICATION_KEY is not set in the .env file")
        return AppConfig(bridgeIp, applicationKey)
    }
}
