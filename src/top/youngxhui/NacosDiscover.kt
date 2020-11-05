package top.youngxhui

import com.alibaba.nacos.api.naming.NamingFactory
import com.alibaba.nacos.api.naming.pojo.Instance
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.util.*
import java.net.InetAddress

/**
 * Create by young on 2020/11/3
 * Copyright © 2020 young. All rights reserved.
 */

class NacosDiscover(configuration: Configuration) {

    val serverName = configuration.serverName

    class Configuration {
        lateinit var serverName: String
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, NacosDiscover> {
        /**
         * Unique key that identifies a feature
         */
        override val key: AttributeKey<NacosDiscover>
            get() = AttributeKey("nacos")

        /**
         * Feature installation script
         */
        @KtorExperimentalAPI
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): NacosDiscover {
            val nacosConfig = NacosConfig()
            val config = HoconApplicationConfig(ConfigFactory.load())
            val ktorConfig = config.config("ktor")
            val ktorPort = ktorConfig.propertyOrNull("deployment.port")?.getString()?.toInt() ?: 8080
            val configuration = Configuration().apply(configure)
            val feature = NacosDiscover(configuration)
            val addr = InetAddress.getLocalHost();
            val serverAddress = addr.hostAddress
            // 应用启动时进行
            val naming = NamingFactory.createNamingService("${nacosConfig.nacosAddress}:${nacosConfig.nacosPort}")
            val instance = Instance()
            instance.ip = serverAddress
            instance.port = ktorPort
            instance.serviceName = feature.serverName
            instance.isHealthy = true
            val mateData = HashMap<String, String>()
            mateData["framework"] = "ktor"
            instance.metadata = mateData
            naming.registerInstance(feature.serverName, instance)
            return feature
        }

    }
}