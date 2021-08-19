package app.scheduler
import kotlinx.coroutines.*

import app.controller.ExchangerController

/**
 * Class with polling knowledge.
 *
 * @property ctrl The exchanger rate controller.
 * @property interval Delay for each polling, in milliseconds.
 */
class ExchangerPolling(val ctrl: ExchangerController, val interval: Long){
    suspend fun startRoutine() = coroutineScope {
        async {
            while (true){
                ctrl.sendExchangeRequest()
                delay(interval)
            }
        }
    }
}