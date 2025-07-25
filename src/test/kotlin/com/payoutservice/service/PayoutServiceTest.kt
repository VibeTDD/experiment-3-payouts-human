package com.payoutservice.service

import PayoutValidator
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode
import com.payoutservice.mother.PayoutMother
import com.payoutservice.storage.PayoutStorage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PayoutServiceTest {

    private lateinit var payoutService: PayoutService
    private lateinit var mockPayoutStorage: PayoutStorage

    @BeforeEach
    fun setUp() {
        mockPayoutStorage = mockk(relaxed = true)
    }

    @Test
    fun `should validate and store payout when all validations pass`() {
        val validator1 = mockk<PayoutValidator>(relaxed = true)
        val validator2 = mockk<PayoutValidator>(relaxed = true)
        payoutService = PayoutService(mockPayoutStorage, listOf(validator1, validator2))
        val payout = PayoutMother.of()

        payoutService.process(payout)

        verify { validator1.validate(payout) }
        verify { validator2.validate(payout) }
        verify { mockPayoutStorage.store(payout) }
    }

    @Test
    fun `should throw exception and not store when validation fails`() {
        val validator1 = mockk<PayoutValidator>(relaxed = true)
        val validator2 = mockk<PayoutValidator>()
        every { validator2.validate(any()) } throws InvalidPayoutException(
            PayoutErrorCode.INVALID_AMOUNT,
            "Amount exceeds maximum"
        )
        payoutService = PayoutService(mockPayoutStorage, listOf(validator1, validator2))
        val payout = PayoutMother.of()

        val exception = shouldThrow<InvalidPayoutException> {
            payoutService.process(payout)
        }

        exception.code shouldBe PayoutErrorCode.INVALID_AMOUNT
        verify { validator1.validate(payout) }
        verify { validator2.validate(payout) }
        verify(exactly = 0) { mockPayoutStorage.store(any()) }
    }

    @Test
    fun `should process payout when no validators provided`() {
        payoutService = PayoutService(mockPayoutStorage, emptyList())
        val payout = PayoutMother.of()

        payoutService.process(payout)

        verify { mockPayoutStorage.store(payout) }
    }
}