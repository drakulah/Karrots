package dev.drsn.karrots.innertube

import dev.drsn.karrots.innertube.declare.AndroidReqClient
import dev.drsn.karrots.innertube.declare.AndroidReqContext
import dev.drsn.karrots.innertube.declare.ApiKey
import dev.drsn.karrots.innertube.declare.WebReqClient
import dev.drsn.karrots.innertube.declare.WebReqContext
import dev.drsn.karrots.innertube.declare.defaultHttpClient
import dev.drsn.karrots.innertube.declare.defaultVisitorData

class Innertube(
	visitorData: String = defaultVisitorData
) {

	val webHttpClient = defaultHttpClient {
		it.url(scheme = "https", host = "music.youtube.com") {
			parameters.append("prettyPrint", "false")
			parameters.append("key", ApiKey.Web)
		}
	}

	val androidHttpClient = defaultHttpClient {
		it.url(scheme = "https", host = "music.youtube.com") {
			parameters.append("prettyPrint", "false")
			parameters.append("key", ApiKey.Android)
		}
	}

	val webContext = WebReqContext(
		client = WebReqClient("US", "en", "WEB_REMIX", "1.20230104.01.00", visitorData)
	)

	val androidContext = AndroidReqContext(
		client = AndroidReqClient("US", "en", "ANDROID_MUSIC", "5.39.52", 32, visitorData)
	)
}