package dev.drsn.karrots.util

import android.os.Handler
import android.os.Looper

class Timer {
	private var intervalTask = Runnable { }

	fun setInterval(intervalMs: Long, callback: () -> Unit) {

		val handler = Handler(Looper.getMainLooper())

		intervalTask = Runnable {
			callback()
			handler.postDelayed(intervalTask, intervalMs)
		}

		handler.postDelayed(intervalTask, intervalMs)

	}

//	fun clearInterval() {
//		Handler(Looper.getMainLooper()).removeCallbacks(intervalTask)
//	}
}

//fun setTimeout(timeoutMs: Long, callback: () -> Unit): Job {
//	return CoroutineScope(Dispatchers.IO).launch {
//		if (isActive) {
//			delay(timeoutMs)
//			callback()
//		}
//	}
//}