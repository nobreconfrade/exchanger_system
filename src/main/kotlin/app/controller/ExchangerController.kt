package app.controller

import app.API_KEY
import app.API_URL
import app.model.ExchangeRatesEntity
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.logging.Logger

class ExchangerController {
    private val logger = Logger.getLogger("ExchangerController")

    private var ts: Long = 0
    private var rates = hashMapOf<String, Float>(
        "BRL" to 0.0f,
        "EUR" to 0.0f,
        "JPY" to 0.0f,
        "USD" to 0.0f
    )

    private fun exchangerSerializer(inputLine: String) {
        var parts = inputLine.split(":")
        if (parts.size == 2) {
            // Clean the id string, remove whitespaces and double-quotes for identification
            var id = parts[0].trim().replace("\"","")
            // Clean the value string, remove whitespaces and trailing comma them transform to float
            var value = parts[1].trim().trim(',')
            if (id == "timestamp"){
                ts = value.toLong()
            }else if (id == "base"){
                value = value.replace("\"", "")
                if (value == "USD"){
                    rates["USD"] = 0f
                } else {
                    throw NotImplementedError("Supported base currencies: 'USD'")
                }
            } else if (arrayOf("BRL","JPY","EUR").contains(id)){
                rates[id] = value.toFloat()
            }
        }
    }

    fun sendExchangeRequest(){
        var params = "app_id=" + URLEncoder.encode(API_KEY, "UTF-8")
        params += "&base=USD"
        params += "&symbols=EUR,JPY,BRL"
        val mURL = URL(API_URL +params)
        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "POST"
            doOutput = true

            val wr = OutputStreamWriter(getOutputStream());
            wr.flush();

            logger.info("URL : $url")
            logger.info("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    exchangerSerializer(inputLine)
                    response.append(inputLine)
                    inputLine = it.readLine()
                }

                transaction {
                    ExchangeRatesEntity.new {
                        datetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),
                            ZoneId.of("America/Sao_Paulo"))
                        BRL = rates["BRL"]!!  // Without (!!) it assumes optional variable
                        JPY = rates["JPY"]!!
                        EUR = rates["EUR"]!!
                        USD = rates["USD"]!!
                    }
                }
                logger.info("New exchange rate added")
            }
        }
    }
}
