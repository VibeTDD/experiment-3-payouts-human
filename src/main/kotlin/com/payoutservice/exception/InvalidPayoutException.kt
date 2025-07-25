package com.payoutservice.exception

class InvalidPayoutException(
    val code: PayoutErrorCode,
    message: String
) : Exception(message)

enum class PayoutErrorCode {
    EMPTY_USER_ID,
    INVALID_AMOUNT,
    INVALID_CURRENCY,
    USER_LIMIT_EXCEEDED
}