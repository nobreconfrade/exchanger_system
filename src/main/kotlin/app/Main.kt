package app
import app.controller.ExchangerController
import app.controller.TransactionController
import app.model.ExchangeRatesTable
import app.model.TransactionTable
import app.scheduler.ExchangerScheduler
import io.javalin.Javalin
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger


data class InputFormat(
    val key: String,
    val from: String,
    val to: String,
    val value: Float
)

val logger = Logger.getLogger("root")

fun database_setup() {
    Database.connect("jdbc:postgresql://localhost:5432/", driver = "org.postgresql.Driver",
        user = "postgres", password = "1234")
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ExchangeRatesTable, TransactionTable)
    }
}



fun main(args: Array<String>) {
    val app = Javalin.create().apply {
        exception(Exception::class.java) {e, ctx -> e.printStackTrace()}
        error(404) {ctx -> ctx.json("not found")}
    }.start(7000)

    logger.info("Setting up Database connection and tables")
    database_setup()
    logger.info("Database connected and created")

    val exchanger = ExchangerController()
    val transactions = TransactionController()
    val exchangerTask = ExchangerScheduler(exchanger, COROUTINE_INTERVAL)

    logger.info("Setting up async polling")
    CompletableFuture.supplyAsync {
        runBlocking {
            exchangerTask.startRoutine()
        }
    }
    logger.info("Async polling ready")


    logger.info("Setting up routes")
    app.post("/transaction"){ ctx ->
        var data = ctx.body<InputFormat>()
        val rates = exchanger.exchangeRateForLastTimestamp()
        val resp = transactions.calculateConversion(data, rates)
        ctx.json(resp)
        ctx.status(200)
    }

    app.get("/transaction/:user_key") { ctx ->
        val resp = transactions.getTransactionByKey(ctx.pathParam("user_key"))
        ctx.json(resp)
    }
    logger.info("Routes ready")
}
