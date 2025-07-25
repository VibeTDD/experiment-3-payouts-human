package com.payoutservice.config

interface PayoutConfiguration {
    fun getAllowedCurrencies(): Set<String>
    fun getMaxAmount(): Double
    fun getMaxUserTotal(): Double
}