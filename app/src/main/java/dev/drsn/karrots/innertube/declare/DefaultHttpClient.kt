package dev.drsn.karrots.innertube.declare

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun defaultHttpClient(fn: (DefaultRequest.DefaultRequestBuilder) -> Unit): HttpClient {
	return HttpClient(CIO) {
		followRedirects = true
		expectSuccess = true

		BrowserUserAgent()

		install(HttpCache)
		install(HttpCookies)

		install(HttpRequestRetry) {
			retryOnException()
			constantDelay(3_000)
		}

		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
				explicitNulls = false
				encodeDefaults = true
			})
		}

		install(ContentEncoding) {
			gzip(0.9f)
			deflate(0.8f)
		}

		defaultRequest {
			fn(this)
			contentType(ContentType.Application.Json)
		}
	}
}