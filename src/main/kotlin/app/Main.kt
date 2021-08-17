package app
import io.javalin.Javalin;

data class User(val name: String, val email: String, val id: Int)

fun main(args: Array<String>){
    val app = Javalin.create().start(7000)
    val users = hashMapOf(
        0 to User(name = "Alice", email = "alice@alice.kt", id = 0),
        1 to User(name = "Bob", email = "bob@bob.kt", id = 1),
        2 to User(name = "Carol", email = "carol@carol.kt", id = 2),
        3 to User(name = "Dave", email = "dave@dave.kt", id = 3)
    )
    app.get("/") { ctx -> ctx.result(user1.toString()) }
}