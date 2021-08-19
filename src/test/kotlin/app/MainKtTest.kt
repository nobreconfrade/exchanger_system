package app

import app.scheduler.ExchangerPolling
import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MainKtTest {
    private val ctx = mockk<Context>(relaxed = true)

    @BeforeEach
    fun tests_setup(){
        mockkStatic("app.MainKt")
        every { database_setup() } returns Unit
        mockk<ExchangerPolling>(relaxed = true)
    }

    @Test
    fun `main javalin app running`(){
        main(arrayOf())
    }
}