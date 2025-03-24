
package nz.co.bibble.ktmcptest.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import nz.co.bibble.ktmcptest.config.AppConfig
import nz.co.bibble.ktmcptest.config.ConfigLoader
import nz.co.bibble.ktmcptest.hueclient.HueClient
import nz.co.bibble.ktmcptest.hueclient.HueClientImpl
import org.koin.dsl.module

val appModule =
    module {

        // Provide the HttpClient
        single {
            HttpClient(CIO) {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                engine {
                    https {
                        trustManager =
                            object : javax.net.ssl.X509TrustManager {
                                override fun checkClientTrusted(
                                    chain: Array<out java.security.cert.X509Certificate>?,
                                    authType: String?,
                                ) {}

                                override fun checkServerTrusted(
                                    chain: Array<out java.security.cert.X509Certificate>?,
                                    authType: String?,
                                ) {}

                                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? = null
                            }
                    }
                }
            }
        }

        // Provide the AppConfig object
        single { ConfigLoader.loadConfig() }

        // Provide the HueClient implementation
        single<HueClient> {
            HueClientImpl(get(), get<AppConfig>().bridgeIp, get<AppConfig>().applicationKey)
        }
    }
