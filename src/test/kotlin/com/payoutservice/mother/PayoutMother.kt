package com.payoutservice.mother

import com.payoutservice.domain.Payout

object PayoutMother {

    fun of(
        userId: String = Rand.string(),
        amount: Double = Rand.amount(),
        currency: String = Rand.currency(),
    ) = Payout(
        userId = userId,
        amount = amount,
        currency = currency,
    )
}