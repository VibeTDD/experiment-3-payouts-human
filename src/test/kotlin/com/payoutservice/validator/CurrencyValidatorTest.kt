package com.payoutservice.validator

import com.payoutservice.config.PayoutConfiguration
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode
import com.payoutservice.mother.PayoutMother
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class CurrencyValidatorTest {

    @InjectMockKs
    private lateinit var validator: CurrencyValidator

    @MockK
    private lateinit var payoutConfiguration: PayoutConfiguration

    @Test
    fun `should throw exception when Currency is empty`() {
        val payout = PayoutMother.of(currency = "")

        val exception = shouldThrow<InvalidPayoutException> {
            validator.validate(payout)
        }
        exception.code shouldBe PayoutErrorCode.INVALID_CURRENCY
    }

    @ParameterizedTest
    @ValueSource(strings = ["JPY", "CHF", "CAD", "eur", "usd", "gbp"])
    fun `should throw exception when Currency is not allowed`(currency: String) {
        every { payoutConfiguration.getAllowedCurrencies() } returns setOf("EUR", "USD", "GBP")
        val payout = PayoutMother.of(currency = currency)

        val exception = shouldThrow<InvalidPayoutException> {
            validator.validate(payout)
        }
        exception.code shouldBe PayoutErrorCode.INVALID_CURRENCY
    }

    @ParameterizedTest
    @ValueSource(strings = ["EUR", "USD", "GBP"])
    fun `should not throw exception when Currency is allowed`(currency: String) {
        every { payoutConfiguration.getAllowedCurrencies() } returns setOf("EUR", "USD", "GBP")
        val payout = PayoutMother.of(currency = currency)

        validator.validate(payout)
    }
}