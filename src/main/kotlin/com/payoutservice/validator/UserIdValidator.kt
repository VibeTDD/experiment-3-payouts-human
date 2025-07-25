package com.payoutservice.validator

import PayoutValidator
import com.payoutservice.domain.Payout
import com.payoutservice.exception.InvalidPayoutException
import com.payoutservice.exception.PayoutErrorCode

class UserIdValidator : PayoutValidator {

    override fun validate(payout: Payout) {
        if (payout.userId.isEmpty()) {
            throw InvalidPayoutException(
                PayoutErrorCode.EMPTY_USER_ID,
                "UserId cannot be empty"
            )
        }
    }
}