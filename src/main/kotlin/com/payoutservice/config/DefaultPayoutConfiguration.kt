package com.payoutservice.config

class DefaultPayoutConfiguration : PayoutConfiguration {

    override fun getAllowedCurrencies(): Set<String> = setOf("EUR", "USD", "GBP")

    override fun getMaxAmount(): Double = 30.0

    override fun getMaxUserTotal(): Double = 100.0
}