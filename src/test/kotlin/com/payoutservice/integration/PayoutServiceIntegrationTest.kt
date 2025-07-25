package com.payoutservice.integration

import com.payoutservice.validator.UserIdValidator
import com.payoutservice.config.DefaultPayoutConfiguration
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode
import com.payoutservice.mother.PayoutMother
import com.payoutservice.service.PayoutService
import com.payoutservice.storage.InMemoryPayoutStorage
import com.payoutservice.validator.AmountValidator
import com.payoutservice.validator.CurrencyValidator
import com.payoutservice.validator.UserTotalLimitValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PayoutServiceIntegrationTest {

    private lateinit var payoutService: PayoutService
    private lateinit var storage: InMemoryPayoutStorage
    private lateinit var configuration: DefaultPayoutConfiguration

    @BeforeEach
    fun setUp() {
        storage = InMemoryPayoutStorage()
        configuration = DefaultPayoutConfiguration()

        val validators = listOf(
            UserIdValidator(),
            CurrencyValidator(configuration),
            AmountValidator(configuration),
            UserTotalLimitValidator(configuration, storage)
        )

        payoutService = PayoutService(storage, validators)
    }

    @Test
    fun `should process valid payout successfully`() {
        val payout = PayoutMother.of(
            userId = Rand.string(),
            amount = 25.0,
            currency = "EUR"
        )

        payoutService.process(payout)

        val storedPayouts = storage.findByUserId(payout.userId)
        storedPayouts shouldHaveSize 1
        storedPayouts[0] shouldBe payout
    }

    @Test
    fun `should reject payout when userId is empty`() {
        val payout = PayoutMother.of(
            userId = "",
            amount = 25.0,
            currency = "EUR"
        )

        val exception = shouldThrow<InvalidPayoutException> {
            payoutService.process(payout)
        }

        exception.code shouldBe PayoutErrorCode.EMPTY_USER_ID
        storage.findByUserId("") shouldHaveSize 0
    }

    @Test
    fun `should reject payout when currency is not allowed`() {
        val payout = PayoutMother.of(
            amount = 25.0,
            currency = "JPY"
        )

        val exception = shouldThrow<InvalidPayoutException> {
            payoutService.process(payout)
        }

        exception.code shouldBe PayoutErrorCode.INVALID_CURRENCY
        storage.findByUserId(payout.userId) shouldHaveSize 0
    }

    @Test
    fun `should reject payout when amount exceeds maximum`() {
        val payout = PayoutMother.of(
            amount = 30.01,
            currency = "EUR"
        )

        val exception = shouldThrow<InvalidPayoutException> {
            payoutService.process(payout)
        }

        exception.code shouldBe PayoutErrorCode.INVALID_AMOUNT
        storage.findByUserId(payout.userId) shouldHaveSize 0
    }

    @Test
    fun `should reject payout when user total would exceed limit`() {
        val userId = Rand.string()

        // Add existing payouts that total 80 (each payout ≤ 30)
        val existingPayout1 = PayoutMother.of(userId = userId, amount = 30.0, currency = "EUR")
        val existingPayout2 = PayoutMother.of(userId = userId, amount = 30.0, currency = "USD")
        val existingPayout3 = PayoutMother.of(userId = userId, amount = 20.0, currency = "GBP")
        payoutService.process(existingPayout1)
        payoutService.process(existingPayout2)
        payoutService.process(existingPayout3)

        // Try to add another payout that would exceed 100 total (80 + 25 = 105)
        val newPayout = PayoutMother.of(userId = userId, amount = 25.0, currency = "EUR")

        val exception = shouldThrow<InvalidPayoutException> {
            payoutService.process(newPayout)
        }

        exception.code shouldBe PayoutErrorCode.USER_LIMIT_EXCEEDED
        storage.findByUserId(userId) shouldHaveSize 3 // Only the first three payouts
    }

    @Test
    fun `should allow payout when user total equals exactly the limit`() {
        val userId = Rand.string()

        // Add existing payouts that total 70
        val existingPayout1 = PayoutMother.of(userId = userId, amount = 30.0, currency = "EUR")
        val existingPayout2 = PayoutMother.of(userId = userId, amount = 25.0, currency = "USD")
        val existingPayout3 = PayoutMother.of(userId = userId, amount = 15.0, currency = "GBP")
        payoutService.process(existingPayout1)
        payoutService.process(existingPayout2)
        payoutService.process(existingPayout3)

        // Add payout that brings total to exactly 100 (70 + 30 = 100)
        val newPayout = PayoutMother.of(userId = userId, amount = 30.0, currency = "EUR")
        payoutService.process(newPayout)

        val storedPayouts = storage.findByUserId(userId)
        storedPayouts shouldHaveSize 4
    }

    @Test
    fun `should process multiple payouts for different users independently`() {
        val user1Payout = PayoutMother.of(amount = 25.0, currency = "EUR")
        val user2Payout = PayoutMother.of(amount = 20.0, currency = "USD")

        payoutService.process(user1Payout)
        payoutService.process(user2Payout)

        storage.findByUserId(user1Payout.userId) shouldHaveSize 1
        storage.findByUserId(user2Payout.userId) shouldHaveSize 1
    }
}