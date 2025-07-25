package com.payoutservice.validator

import PayoutValidator
import com.payoutservice.config.PayoutConfiguration
import com.payoutservice.domain.Payout
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode
import com.payoutservice.storage.PayoutStorage

class UserTotalLimitValidator(
    private val configuration: PayoutConfiguration,
    private val storage: PayoutStorage
) : PayoutValidator {

    override fun validate(payout: Payout) {
        val existingPayouts = storage.findByUserId(payout.userId)
        val currentTotal = existingPayouts.sumOf { it.amount }
        val newTotal = currentTotal + payout.amount
        val maxUserTotal = configuration.getMaxUserTotal()

        if (newTotal > maxUserTotal) {
            throw InvalidPayoutException(
                PayoutErrorCode.USER_LIMIT_EXCEEDED,
                "User total payouts cannot exceed $maxUserTotal. Current total: $currentTotal"
            )
        }
    }
}