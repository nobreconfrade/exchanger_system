package app
import app.model.User
import app.controller.UserDao
import io.javalin.Javalin;

fun main(args: Array<String>){
    val userDao = UserDao()
    val app = Javalin.create().apply {
        exception(Exception::class.java) {e, ctx -> e.printStackTrace()}
        error(404) {ctx -> ctx.json("not found")}
    }.start(7000)

    app.get("/users") { ctx ->
        ctx.json(userDao.users)
    }
    app.get("/users/:user-id") { ctx ->
        ctx.json(userDao.findById(ctx.pathParam("user-id").toInt())!!)
    }
    app.get("/users/email/:email") { ctx ->
        ctx.json(userDao.findByEmail(ctx.pathParam("email"))!!)
    }

    app.post("/users") { ctx ->
        val user = ctx.body<User>()
        userDao.save(name = user.name, email = user.email)
        ctx.status(201)
    }

    app.patch("/users/:user-id") { ctx ->
        val user = ctx.body<User>()
        userDao.update(
            id = ctx.pathParam("user-id").toInt(),
            user = user
        )
        ctx.status(204)
    }

    app.delete("/users/:user-id") { ctx ->
        userDao.delete(ctx.pathParam("user-id").toInt())
        ctx.status(204)
    }
}