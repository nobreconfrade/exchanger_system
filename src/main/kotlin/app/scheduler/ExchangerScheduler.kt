package app.scheduler
import kotlinx.coroutines.*

import app.controller.ExchangerController

class ExchangerScheduler(val service: ExchangerController, val intervalMili: Long){
    fun startRoutine() = runBlocking {
        coroutineLoop()
    }

    private suspend fun coroutineLoop() = coroutineScope{
        launch {
            while (true){
                service.sendExchangeRequest()
                delay(intervalMili)
            }
        }
    }
}