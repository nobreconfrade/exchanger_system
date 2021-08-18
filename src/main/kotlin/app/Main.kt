package app
import app.controller.ExchangerController
import app.model.Transaction
import io.javalin.Javalin
import kotlinx.coroutines.*
import java.util.*
import java.util.logging.Logger


data class InputFormat(val id: String,
                       val from: String,
                       val to: String,
                       val value: Float)

val logger = Logger.getLogger("root")


fun main(args: Array<String>) = runBlocking{
    val app = Javalin.create().apply {
        exception(Exception::class.java) {e, ctx -> e.printStackTrace()}
        error(404) {ctx -> ctx.json("not found")}
    }.start(7000)

    val exchanger = ExchangerController()

//    Initially I wanted to separate it in a different class, but the coroutine is locking the main thread
//    val exchangerTask = ExchangerScheduler(exchanger, COROUTINE_INTERVAL)
//    exchangerTask.startRoutine()

    launch {
        while (true){
            exchanger.sendExchangeRequest()
            delay(COROUTINE_INTERVAL)
        }
    }

    logger.info("Setting up routes")
    app.post("/transaction"){ ctx ->
        var data = ctx.body<InputFormat>()
        val value_dest: Float
        if (data.from == "USD"){
            value_dest = data.value * rates[data.to]!! //TODO: remove !! since it will have values
        } else {
            var value_in_usd = data.value / rates[data.from]!! //TODO: remove !! since it will have values
            value_dest = value_in_usd * rates[data.to]!! //TODO: remove !! since it will have values
        }
        val resp = hashMapOf<String, Any>(
            "id_transaction" to 1, //TODO: Incremental last ID
            "id_user" to data.id,
            "currency_orig" to data.from,
            "value_orig" to data.value,
            "currency_dest" to data.to,
            "value_dest" to value_dest,
            "convertion_rate" to "WIP", //TODO: calculate convertion rate for non-USD currencies
//            "date" to Calendar.getInstance(),
        )
        ctx.json(resp)
        ctx.status(201)
    }
    logger.info("Routes ready")

//    app.get("/users") { ctx ->
//        ctx.json(userController.users)
//    }
//    app.get("/users/:user-id") { ctx ->
//        ctx.json(userController.findById(ctx.pathParam("user-id").toInt())!!)
//    }
//    app.get("/users/email/:email") { ctx ->
//        ctx.json(userController.findByEmail(ctx.pathParam("email"))!!)
//    }
//
//    app.post("/users") { ctx ->
//        val user = ctx.body<User>()
//        userController.save(name = user.name, email = user.email)
//        ctx.status(201)
//    }
//
//    app.patch("/users/:user-id") { ctx ->
//        val user = ctx.body<User>()
//        userController.update(
//            id = ctx.pathParam("user-id").toInt(),
//            user = user
//        )
//        ctx.status(204)
//    }
//
//    app.delete("/users/:user-id") { ctx ->
//        userController.delete(ctx.pathParam("user-id").toInt())
//        ctx.status(204)
//    }
}
