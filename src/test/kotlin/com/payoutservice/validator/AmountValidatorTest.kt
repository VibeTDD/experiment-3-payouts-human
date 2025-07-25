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
class AmountValidatorTest {

    @InjectMockKs
    private lateinit var validator: AmountValidator

    @MockK
    private lateinit var payoutConfiguration: PayoutConfiguration

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -5.0, -100.0, -0.01])
    fun `should throw exception when Amount is zero or negative`(amount: Double) {
        val payout = PayoutMother.of(amount = amount)

        val exception = shouldThrow<InvalidPayoutException> {
            validator.validate(payout)
        }
        exception.code shouldBe PayoutErrorCode.INVALID_AMOUNT
    }

    @Test
    fun `should throw exception when Amount exceeds maximum`() {
        every { payoutConfiguration.getMaxAmount() } returns 30.0
        val payout = PayoutMother.of(amount = 30.01)

        val exception = shouldThrow<InvalidPayoutException> {
            validator.validate(payout)
        }
        exception.code shouldBe PayoutErrorCode.INVALID_AMOUNT
    }

    @ParameterizedTest
    @ValueSource(doubles = [30.0, 25.0, 0.01, 15.5])
    fun `should not throw exception when Amount is valid`(amount: Double) {
        every { payoutConfiguration.getMaxAmount() } returns 30.0
        val payout = PayoutMother.of(amount = amount)

        validator.validate(payout)
    }
}