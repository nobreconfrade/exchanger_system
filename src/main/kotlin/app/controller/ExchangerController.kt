package app.controller

import app.API_KEY
import app.API_URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.logging.Logger

class ExchangerController {
    val logger = Logger.getLogger("ExchangerController")

    var placeholder_db = hashMapOf<String, Float>(
        "USD" to 0.0f,
        "BRL" to 0.0f,
        "EUR" to 0.0f,
        "JPY" to 0.0f
    )

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
                    var parts = inputLine.split(":")
                    if (parts.size == 2) {
                        // Clean the id string, remove whitespaces and double-quotes for identification
                        var id = parts[0].trim().replace("\"","")
                        // Clean the value string, remove whitespaces and trailing comma them transform to float
                        var rate = parts[1].trim().trim(',')
                        if (arrayOf("BRL","JPY","EUR").contains(id)){
                            placeholder_db[id] = rate.toFloat()
                        }
                    }
//                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                logger.info("Response : $response")
                logger.info("Parsed value : $placeholder_db")
            }
        }
    }
}
