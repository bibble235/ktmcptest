package nz.co.bibble.app.huemcp

import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.json.*
import nz.co.bibble.ktmcptest.di.appModule
import nz.co.bibble.ktmcptest.hueclient.HueClient
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("McpServer")

// Tool names as constants
private const val TOOL_GET_LIGHT_IDS = "get_light_ids"
private const val TOOL_SET_LIGHT_STATE = "set-light-state"

fun main() {
    try {
        logger.info("Starting MCP Server...")

        // Initialize Koin
        startDependencyInjection()

        // Retrieve HueClient instance
        val hueClient: HueClient = getKoin().get()

        // Create and configure the server
        val server = createServer(hueClient)

        // Start the server with stdio transport
        startServer(server)
    } catch (e: Exception) {
        logger.error("An error occurred during server setup: ${e.message}", e)
    }
}

private fun startDependencyInjection() {
    logger.info("Initializing Koin...")
    startKoin { modules(appModule) }
    logger.info("Koin initialized successfully.")
}

private fun startServer(server: Server) {
    val stdioServerTransport =
        StdioServerTransport(System.`in`.asSource().buffered(), System.out.asSink().buffered())

    runBlocking {
        logger.info("Connecting server to transport...")
        server.connect(stdioServerTransport)

        val job = Job()
        server.onCloseCallback = {
            logger.info("Server closed.")
            job.complete()
        }

        logger.info("Waiting for server to close...")
        job.join()
        logger.info("Server has shut down.")
    }
}

fun createServer(hueClient: HueClient): Server {
    logger.info("Initializing server capabilities...")

    // Server metadata
    val info = Implementation(name = "huemcp", version = "0.1.0")

    // Server options
    val options =
        ServerOptions(
            capabilities =
                ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = true)),
        )

    val server = Server(info, options)

    // Add tools to the server
    addGetLightIdsTool(server, hueClient)
    addSetLightStateTool(server, hueClient)

    logger.info("Server setup complete.")
    return server
}

private fun addGetLightIdsTool(
    server: Server,
    hueClient: HueClient,
) {
    logger.info("Adding tool: $TOOL_GET_LIGHT_IDS")

    server.addTool(
        name = TOOL_GET_LIGHT_IDS,
        description = "Returns a list of light IDs for a given room.",
        inputSchema =
            Tool.Input(
                properties =
                    JsonObject(
                        mapOf(
                            "room" to
                                JsonObject(
                                    mapOf(
                                        "type" to
                                            JsonPrimitive(
                                                "string",
                                            ),
                                        "description" to
                                            JsonPrimitive(
                                                "Room name (e.g., Hall)",
                                            ),
                                    ),
                                ),
                        ),
                    ),
                required = listOf("room"),
            ),
    ) { input ->
        val roomName = input.arguments["room"]?.jsonPrimitive?.contentOrNull
        if (roomName.isNullOrBlank()) {
            logger.error("Room name is missing or invalid.")
            return@addTool CallToolResult(
                content = listOf(TextContent("Error: Missing or invalid room name")),
            )
        }

        val lightIds = hueClient.findLightIdsForRoom(roomName).joinToString()
        logger.info("Found light IDs for room '$roomName': $lightIds")

        CallToolResult(content = listOf(TextContent(lightIds)))
    }
}

private fun addSetLightStateTool(
    server: Server,
    hueClient: HueClient,
) {
    logger.info("Adding tool: $TOOL_SET_LIGHT_STATE")

    val inputSchema =
        Tool.Input(
            properties =
                JsonObject(
                    mapOf(
                        "lightId" to
                            JsonObject(
                                mapOf("type" to JsonPrimitive("string")),
                            ),
                        "on" to
                            JsonObject(
                                mapOf(
                                    "type" to
                                        JsonPrimitive("boolean"),
                                ),
                            ),
                        "brightness" to
                            JsonObject(
                                mapOf("type" to JsonPrimitive("number")),
                            ),
                        "colorGamutX" to
                            JsonObject(
                                mapOf("type" to JsonPrimitive("number")),
                            ),
                        "colorGamutY" to
                            JsonObject(
                                mapOf("type" to JsonPrimitive("number")),
                            ),
                        "dimming" to
                            JsonObject(
                                mapOf("type" to JsonPrimitive("number")),
                            ),
                    ),
                ),
            required = listOf("lightId"),
        )

    server.addTool(
        name = TOOL_SET_LIGHT_STATE,
        description =
            "Sets the light state for a given light. Brightness ranges from 0 to 100.",
        inputSchema = inputSchema,
    ) { input ->
        val lightId = input.arguments["lightId"]?.jsonPrimitive?.content
        if (lightId.isNullOrBlank()) {
            logger.error("Light ID is missing or invalid.")
            return@addTool CallToolResult(
                content = listOf(TextContent("Error: Missing or invalid light ID")),
            )
        }

        val on = input.arguments["on"]?.jsonPrimitive?.booleanOrNull
        val brightness = input.arguments["brightness"]?.jsonPrimitive?.doubleOrNull
        val colorGamutX = input.arguments["colorGamutX"]?.jsonPrimitive?.doubleOrNull
        val colorGamutY = input.arguments["colorGamutY"]?.jsonPrimitive?.doubleOrNull
        val dimming = input.arguments["dimming"]?.jsonPrimitive?.doubleOrNull

        hueClient.setLightState(lightId, on, brightness, colorGamutX, colorGamutY, dimming)
        logger.info("Light state updated for lightId=$lightId")

        CallToolResult(content = listOf(TextContent("Done!")))
    }
}
