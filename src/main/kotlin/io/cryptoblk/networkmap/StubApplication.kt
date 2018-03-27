package io.cryptoblk.networkmap

import io.cryptoblk.networkmap.handler.NetworkMapHandler
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import java.io.IOException
import java.util.Date
import java.util.EnumSet
import javax.servlet.DispatcherType
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

class CacheControlFilter(val expiration: Int) : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse,
        chain: FilterChain) {

        val resp = response as HttpServletResponse

        resp.setHeader("Cache-Control", "public, max-age=$expiration")
        resp.setHeader("Expires", (Date().time + expiration).toString())

        chain.doFilter(request, response)
    }

    override fun destroy() {}

    @Throws(ServletException::class)
    override fun init(arg0: FilterConfig) {
    }
}

class NetworkMapConfig(val name: String = "unknown", val expiration: Int = 30) : Configuration()

class StubApplication: Application<NetworkMapConfig>() {
    override fun run(configuration: NetworkMapConfig, environment: Environment) {
        println("Running ${configuration.name}! -- cache for ${configuration.expiration}s")
        val nmComp = NetworkMapHandler()
        environment.jersey().register(nmComp)
        environment.servlets().addFilter("CacheControlFilter", CacheControlFilter(configuration.expiration)).
            addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*")
    }
}

fun main(args: Array<String>) {
    StubApplication().run(*args)
}
