package com.payoutservice.validator

import com.payoutservice.config.PayoutConfiguration
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode
import com.payoutservice.mother.PayoutMother
import com.payoutservice.storage.PayoutStorage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UserTotalLimitValidatorTest {

    @InjectMockKs
    private lateinit var validator: UserTotalLimitValidator

    @MockK
    private lateinit var payoutConfiguration: PayoutConfiguration

    @MockK
    private lateinit var payoutStorage: PayoutStorage

    @Test
    fun `should throw exception when user total would exceed maximum`() {
        val userId = "user123"
        every { payoutConfiguration.getMaxUserTotal() } returns 100.0
        every { payoutStorage.findByUserId(userId) } returns listOf(
            PayoutMother.of(userId = userId, amount = 80.0),
            PayoutMother.of(userId = userId, amount = 15.0)
        )
        val payout = PayoutMother.of(userId = userId, amount = 10.0)

        val exception = shouldThrow<InvalidPayoutException> {
            validator.validate(payout)
        }
        exception.code shouldBe PayoutErrorCode.USER_LIMIT_EXCEEDED
    }

    @Test
    fun `should not throw exception when user total equals maximum`() {
        val userId = "user123"
        every { payoutConfiguration.getMaxUserTotal() } returns 100.0
        every { payoutStorage.findByUserId(userId) } returns listOf(
            PayoutMother.of(userId = userId, amount = 70.0)
        )
        val payout = PayoutMother.of(userId = userId, amount = 30.0)

        validator.validate(payout)
    }

    @Test
    fun `should not throw exception when user has no previous payouts`() {
        val userId = "newuser"
        every { payoutConfiguration.getMaxUserTotal() } returns 100.0
        every { payoutStorage.findByUserId(userId) } returns emptyList()
        val payout = PayoutMother.of(userId = userId, amount = 50.0)

        validator.validate(payout)
    }

    @Test
    fun `should not throw exception when user total is below maximum`() {
        val userId = "user123"
        every { payoutConfiguration.getMaxUserTotal() } returns 100.0
        every { payoutStorage.findByUserId(userId) } returns listOf(
            PayoutMother.of(userId = userId, amount = 30.0),
            PayoutMother.of(userId = userId, amount = 20.0)
        )
        val payout = PayoutMother.of(userId = userId, amount = 25.0)

        validator.validate(payout)
    }
}