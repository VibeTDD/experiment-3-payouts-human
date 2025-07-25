package com.payoutservice.storage

import com.payoutservice.domain.Payout

class InMemoryPayoutStorage : PayoutStorage {

    private val payouts = mutableListOf<Payout>()

    override fun store(payout: Payout) {
        payouts.add(payout)
    }

    override fun findByUserId(userId: String): List<Payout> {
        return payouts.filter { it.userId == userId }
    }
}