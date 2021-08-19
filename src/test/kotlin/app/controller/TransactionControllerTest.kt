package app.controller

import DatabaseForTest
import app.InputFormat
import app.model.TransactionEntity
import app.model.TransactionTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TransactionControllerTest: DatabaseForTest("transaction_controller_test_db", arrayOf(TransactionTable)){

    private val testTransactionController: TransactionController = TransactionController()

    @Test
    fun `test transaction by key empty`(){
        val result = testTransactionController.getTransactionByKey("empty")
        assertEquals(result.size, 0)
    }

    @Test
    fun `test transaction by key`(){
        val testUsers = 4
        for(i in 1..testUsers){
            transaction {
                TransactionEntity.new {
                    user_key = "key$i"
                    currency_orig = "USD"
                    value_orig = 100f * i
                    currency_dest = "BRL"
                    conversion_rate = 5.2f
                    date = LocalDateTime.parse("2004-01-01T10:00:00")
                }
            }
        }
        val result = testTransactionController.getTransactionByKey("key1")
        assertEquals(result.size, 1)
        assertEquals(result[0]!!["value_orig"], 100f)
    }

    @Test
    fun `test conversion method`(){
        val rates = hashMapOf<String, Float>(
            "BRL" to 5.401665210723877f,
            "EUR" to 0.8546720147132874f,
            "JPY" to 109.72966766357422f,
            "USD" to 0f,
        )
        val inputs = InputFormat(
            from = "BRL",
            to = "JPY",
            value = 10f,
            key = "key1"
        )
        val result = testTransactionController.calculateConversion(
            inputs, rates
        )
        assertEquals(result["user_key"], "key1")
        assertEquals(result["value_dest"], 203.14044f)
        assertEquals(result["value_orig"], 10f)
    }

    @Test
    fun `test conversion method does not accept JPY as base currency`(){
        val rates = hashMapOf<String, Float>(
            "BRL" to 200f,
            "EUR" to 100f,
            "JPY" to 0f,
            "USD" to 150f,
        )
        val inputs = InputFormat(
            from = "BRL",
            to = "JPY",
            value = 10f,
            key = "key1"
        )
        assertFailsWith<NotImplementedError>{
            testTransactionController.calculateConversion(
                inputs, rates
            )
        }
    }

}