package top.youngxhui

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.util.*

/**
 * Create by young on 2020/11/3
 * Copyright © 2020 young. All rights reserved.
 */
@KtorExperimentalAPI
class NacosConfig {

    private val config = HoconApplicationConfig(ConfigFactory.load())
    private val nacosConfig = config.config("nacos")
    val nacosPort = nacosConfig.propertyOrNull("port")?.getString() ?: "8848"
    val nacosAddress = nacosConfig.propertyOrNull("address")?.getString() ?: "127.0.0.1"
}