# Kotlin MCP Server

This project is a Kotlin-based MCP (Model Context Protocol) server, inspired by [kotlin-mcp-hue-light-sample](https://github.com/SebastianAigner/kotlin-mcp-hue-light-sample). It integrates with the Phillips Hue system to control smart lights.

---

## Features

This application allows users to:
- Turn devices on/off by room.
- Adjust brightness levels.
- Change light colors.
- Set dimming values.

---

## Requirements

- **Java Development Kit (JDK)**: Version 11 or higher.
- **Gradle**: Build tool for managing dependencies and building the project.
- **Phillips Hue Controller**: Required for interacting with Hue devices.

---

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/bibble235/mcpServer.git
    cd mcpServer/ktMcpTest
    ```

2. Build the project:
    ```bash
    ./gradlew build
    ```

3. Run the application:
    ```bash
    ./gradlew run
    ```

---

## Configuration

The application uses a `.env` file for configuration. Create a `.env` file in the root directory with the following keys:

```plaintext
BRIDGE_IP=<your-hue-bridge-ip>
APPLICATION_KEY=<your-application-key>
```

- **BRIDGE_IP**: The IP address of the Hue Bridge.
- **APPLICATION_KEY**: The application key for accessing the Hue API.

---

## Testing

This project has been tested using:
- [MCP Inspector](https://modelcontextprotocol.io/docs/tools/inspector)
- [ClaudeAI Desktop](https://claude.ai/download)

Run tests using:
```bash
./gradlew test
```

---

## Dependencies

Key dependencies used in this project:
- **[Ktor](https://ktor.io/)**: HTTP client and server framework.
- **[Koin](https://insert-koin.io/)**: Dependency injection framework.
- **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)**: JSON serialization library.
- **[Dotenv Kotlin](https://github.com/cdimascio/dotenv-kotlin)**: Environment variable management.

For a full list of dependencies, see the [`build.gradle.kts`](app/build.gradle.kts) file.

---

## License

This project is licensed under the MIT License. See the [`LICENSE`](LICENSE) file for details.

---

## Acknowledgments

This project is based on the work of [Sebastian Aigner](https://github.com/SebastianAigner) and his [kotlin-mcp-hue-light-sample](https://github.com/SebastianAigner/kotlin-mcp-hue-light-sample).