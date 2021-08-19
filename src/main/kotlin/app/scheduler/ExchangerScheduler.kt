package app.scheduler
import kotlinx.coroutines.*

import app.controller.ExchangerController

class ExchangerScheduler(val service: ExchangerController, val intervalMili: Long){
    suspend fun startRoutine() = coroutineScope {
        async {
            while (true){
                service.sendExchangeRequest()
                delay(intervalMili)
            }
        }
    }
}