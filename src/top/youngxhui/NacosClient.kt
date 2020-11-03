package top.youngxhui

import com.alibaba.nacos.api.naming.NamingFactory
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.util.*


/**
 * Create by young on 2020/11/3
 * Copyright © 2020 young. All rights reserved.
 */

class NacosClient {
    class Config

    companion object Feature : HttpClientFeature<Config, NacosClient> {
        var currentNodeIndex: Int = 0

        override val key = AttributeKey<NacosClient>("ConsulFeature")

        override fun prepare(block: Config.() -> Unit): NacosClient = NacosClient()

        @KtorExperimentalAPI
        override fun install(feature: NacosClient, scope: HttpClient) {

            val nacosConfig = NacosConfig()
            val naming = NamingFactory.createNamingService("${nacosConfig.nacosAddress}:${nacosConfig.nacosPort}")
            scope.requestPipeline.intercept(HttpRequestPipeline.Render) {
                val instances = naming.getAllInstances(context.url.host).filter { it.isHealthy and it.isEnabled }
                val selectedNode = instances[currentNodeIndex]
                context.url.host = selectedNode.ip
                context.url.port = selectedNode.port
                currentNodeIndex = (currentNodeIndex + 1) % instances.size
            }
        }


    }
}