package com.payoutservice.validator

import PayoutValidator
import com.payoutservice.config.PayoutConfiguration
import com.payoutservice.domain.Payout
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode

class AmountValidator(
    private val configuration: PayoutConfiguration
) : PayoutValidator {

    override fun validate(payout: Payout) {
        if (payout.amount <= 0) {
            throw InvalidPayoutException(
                PayoutErrorCode.INVALID_AMOUNT,
                "Amount must be greater than zero"
            )
        }

        val maxAmount = configuration.getMaxAmount()
        if (payout.amount > maxAmount) {
            throw InvalidPayoutException(
                PayoutErrorCode.INVALID_AMOUNT,
                "Amount cannot exceed $maxAmount"
            )
        }
    }
}