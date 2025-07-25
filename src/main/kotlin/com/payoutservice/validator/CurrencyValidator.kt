package com.payoutservice.validator

import PayoutValidator
import com.payoutservice.config.PayoutConfiguration
import com.payoutservice.domain.Payout
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode

class CurrencyValidator(
    private val configuration: PayoutConfiguration
) : PayoutValidator {

    override fun validate(payout: Payout) {
        if (payout.currency.isEmpty()) {
            throw InvalidPayoutException(
                PayoutErrorCode.INVALID_CURRENCY,
                "Currency cannot be empty"
            )
        }

        val allowedCurrencies = configuration.getAllowedCurrencies()
        if (payout.currency !in allowedCurrencies) {
            throw InvalidPayoutException(
                PayoutErrorCode.INVALID_CURRENCY,
                "Currency must be one of: ${allowedCurrencies.joinToString(", ")}"
            )
        }
    }
}