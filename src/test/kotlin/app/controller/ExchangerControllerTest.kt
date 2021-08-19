package app.controller

import DatabaseForTest
import app.model.ExchangeRatesEntity
import app.model.ExchangeRatesTable
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.postgresql.core.ConnectionFactory
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import kotlin.test.assertFailsWith

internal class ExchangerControllerTest: DatabaseForTest("exchange_controller_test_db", arrayOf(ExchangeRatesTable)){

    private val testExchangerController: ExchangerController = ExchangerController()

    @Test
    fun `test retrieve exchange rate of newest timestamp`(){
        val testRows = 3
        for(i in 1..testRows){
            var dt = LocalDateTime.parse("200${i}-01-01T10:00:00")
            var rates = hashMapOf(
                "BRL" to 0f,
                "JPY" to 1f * i,
                "EUR" to 1f * i,
                "USD" to 1f * i
            )
            transaction {
                ExchangeRatesEntity.new {
                    datetime = dt
                    BRL = rates["BRL"]!!
                    JPY = rates["JPY"]!!
                    EUR = rates["EUR"]!!
                    USD = rates["USD"]!!
                }
            }
        }
        val result = testExchangerController.exchangeRateForLastTimestamp()
        assertEquals(result["BRL"], 0f)
        assertEquals(result["EUR"], 1f * testRows)
        assertEquals(result["JPY"], 1f * testRows)
        assertEquals(result["USD"], 1f * testRows)
    }

    @Test
    fun `test throws error when exchange rate table is empty`(){
        assertFailsWith<IndexOutOfBoundsException> {
            testExchangerController.exchangeRateForLastTimestamp()
        }
    }
}