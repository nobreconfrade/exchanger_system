package app.scheduler
import kotlinx.coroutines.*

import app.controller.ExchangerController

class ExchangerScheduler(val service: ExchangerController, val intervalMili: Long){

    suspend fun startRoutine() = coroutineScope {
        launch {
            service.sendExchangeRequest()
            delay(intervalMili)
        }
    }
}