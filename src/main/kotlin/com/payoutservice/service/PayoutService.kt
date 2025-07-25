package com.payoutservice.service

import PayoutValidator
import com.payoutservice.domain.Payout
import com.payoutservice.storage.PayoutStorage

class PayoutService(
    private val payoutStorage: PayoutStorage,
    private val validators: List<PayoutValidator>
) {

    fun process(payout: Payout) {
        validators.forEach { it.validate(payout) }
        payoutStorage.store(payout)
    }
}