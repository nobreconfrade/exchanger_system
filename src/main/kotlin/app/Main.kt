package app
import app.controller.ExchangerController
import app.model.ExchangeRatesTable
import io.javalin.Javalin
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IndexOutOfBoundsException
import java.time.LocalDateTime
import java.util.logging.Logger


data class InputFormat(val id: String,
                       val from: String,
                       val to: String,
                       val value: Float)

val logger = Logger.getLogger("root")

fun database_setup() {
    Database.connect("jdbc:postgresql://localhost:5432/", driver = "org.postgresql.Driver",
        user = "postgres", password = "1234")
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ExchangeRatesTable)
    }
}

fun get_last_timestamp(): HashMap<String, Float>{
    var rates: HashMap<String, Float>
    var resultRows = transaction {
        ExchangeRatesTable.selectAll().sortedByDescending { ExchangeRatesTable.datetime }
    }
    var newestRow: ResultRow
    try {
        newestRow = resultRows[0]
    } catch (e: IndexOutOfBoundsException){
        throw IndexOutOfBoundsException("Exchange rates table is empty! To perform a conversion at least one row is necessary")
    }
    for (el in resultRows){
        if (el[ExchangeRatesTable.datetime] > newestRow[ExchangeRatesTable.datetime])
            newestRow = el
    }
    rates = hashMapOf(
        "BRL" to newestRow[ExchangeRatesTable.BRL],
        "JPY" to newestRow[ExchangeRatesTable.JPY],
        "EUR" to newestRow[ExchangeRatesTable.EUR],
        "USD" to newestRow[ExchangeRatesTable.USD]
    )
    return rates
}

fun main(args: Array<String>) = runBlocking{
    val app = Javalin.create().apply {
        exception(Exception::class.java) {e, ctx -> e.printStackTrace()}
        error(404) {ctx -> ctx.json("not found")}
    }.start(7000)

    logger.info("Setting up Database connection and tables")
    database_setup()
    logger.info("Database connected and created")

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
        val rates = get_last_timestamp()
        val convertion_rate: Float
        if (data.from == "USD"){
            value_dest = data.value * rates[data.to]!! //TODO: Check why (!!) are necessary
            convertion_rate = rates[data.to]!!
        } else {
            var value_in_usd = data.value / rates[data.from]!!
            value_dest = value_in_usd * rates[data.to]!!
            convertion_rate = value_dest / data.value
        }

        val resp = hashMapOf<String, Any>(
            "id_transaction" to 1, //TODO: Incremental last ID
            "id_user" to data.id,
            "currency_orig" to data.from,
            "value_orig" to data.value,
            "currency_dest" to data.to,
            "value_dest" to value_dest,
            "convertion_rate" to convertion_rate,
            "date" to LocalDateTime.now().toString()
        )
        ctx.json(resp)
        ctx.status(200)
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
