package nz.co.bibble.ktmcptest.config

import io.github.cdimascio.dotenv.Dotenv

object ConfigLoader {
    private val dotenv =
        Dotenv.configure()
            .directory(getEnvDirectory()) // Dynamically determine the directory
            .ignoreIfMissing() // Optional: Ignore if the .env file is missing
            .load()

    fun loadConfig(): AppConfig {
        val bridgeIp = dotenv["HUE_BRIDGE_IP"] ?: error("HUE_BRIDGE_IP is not set in the .env file")
        val applicationKey = dotenv["HUE_APPLICATION_KEY"] ?: error("HUE_APPLICATION_KEY is not set in the .env file")
        return AppConfig(bridgeIp, applicationKey)
    }

    private fun getEnvDirectory(): String {
        // Get the directory where the binary is running
        val currentDir = System.getProperty("user.dir")
        println("Loading .env file from directory: $currentDir") // Debug log
        return currentDir
    }
}
