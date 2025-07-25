package com.payoutservice.storage

import com.payoutservice.domain.Payout

interface PayoutStorage {
    fun store(payout: Payout)
    fun findByUserId(userId: String): List<Payout>
}