package app.controller

import app.API_KEY
import app.API_URL
import app.model.ExchangeRatesEntity
import app.model.ExchangeRatesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.IndexOutOfBoundsException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.logging.Logger

/**
 * Exchange rates class with serialization, calculation and fetching knowledge.
 */
class ExchangerController {
    private val logger = Logger.getLogger("ExchangerController")

    private var ts: Long = 0
    private var rates = hashMapOf<String, Float>(
        "BRL" to 0.0f,
        "EUR" to 0.0f,
        "JPY" to 0.0f,
        "USD" to 0.0f
    )

    /**
     * Serialize data from Open Exchange Rates API response.
     *
     * @property inputLine Next line to be parsed.
     */
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

    /**
     * Return the most recent currency exchange rate from database.
     *
     * @return A HashMap with the currency abbreviation to rate,
     * these rates are based in a base currency that will have rate 0.
     */
    fun exchangeRateForLastTimestamp(): HashMap<String, Float>{
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

    /**
     * Request the exchange rates to a external API and put the results on the database.
    */
    fun sendExchangeRequest(){
        var params = "app_id=" + URLEncoder.encode(API_KEY, "UTF-8")
        params += "&base=USD"
        params += "&symbols=EUR,JPY,BRL"
        val mURL = URL(API_URL +params)
        with(mURL.openConnection() as HttpURLConnection) {
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
