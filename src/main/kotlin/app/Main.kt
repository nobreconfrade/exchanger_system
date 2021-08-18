package app
import app.controller.ExchangerController
import app.controller.UserController
import app.scheduler.ExchangerScheduler
import io.javalin.Javalin
import java.util.logging.Logger


data class UserInput(val id: String,
                     val from: String,
                     val to: String,
                     val value: Float)

val logger = Logger.getLogger("root")


fun main(args: Array<String>){
    val app = Javalin.create().apply {
        exception(Exception::class.java) {e, ctx -> e.printStackTrace()}
        error(404) {ctx -> ctx.json("not found")}
    }.start(7000)

    val exchanger = ExchangerController()
    val exchangerTask = ExchangerScheduler(exchanger, COROUTINE_INTERVAL)
    exchangerTask.startRoutine()

    logger.info("Setting up routes")
    app.post("/transaction"){ ctx ->
        var data = ctx.body<UserInput>()
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
