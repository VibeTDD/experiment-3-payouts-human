package com.payoutservice.validator

import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode
import com.payoutservice.mother.PayoutMother
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class UserIdValidatorTest {

    @InjectMockKs
    private lateinit var validator: UserIdValidator

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "\t", "\n"])
    fun `should throw exception when UserId is empty or whitespace`(userId: String) {
        val payout = PayoutMother.of(userId = userId.trim())

        val exception = shouldThrow<InvalidPayoutException> {
            validator.validate(payout)
        }
        exception.code shouldBe PayoutErrorCode.EMPTY_USER_ID
    }

    @Test
    fun `should not throw exception when UserId is valid`() {
        val payout = PayoutMother.of()

        validator.validate(payout)
    }
}