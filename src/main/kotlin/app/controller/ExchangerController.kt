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
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                logger.info("Response : $response")
            }
        }
    }
}
