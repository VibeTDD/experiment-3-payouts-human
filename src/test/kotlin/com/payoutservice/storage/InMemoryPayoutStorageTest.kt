package com.payoutservice.storage

import com.payoutservice.mother.PayoutMother
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InMemoryPayoutStorageTest {

    private lateinit var storage: InMemoryPayoutStorage

    @BeforeEach
    fun setUp() {
        storage = InMemoryPayoutStorage()
    }

    @Test
    fun `should store and retrieve payout by userId`() {
        val payout = PayoutMother.of()

        storage.store(payout)
        val result = storage.findByUserId(payout.userId)

        result shouldHaveSize 1
        result shouldContain payout
    }

    @Test
    fun `should return empty list when no payouts exist for user`() {
        val result = storage.findByUserId(Rand.string())

        result.shouldBeEmpty()
    }

    @Test
    fun `should store multiple payouts for same user`() {
        val userId = Rand.string()
        val payout1 = PayoutMother.of(userId = userId)
        val payout2 = PayoutMother.of(userId = userId)

        storage.store(payout1)
        storage.store(payout2)
        val result = storage.findByUserId(userId)

        result shouldHaveSize 2
        result shouldContain payout1
        result shouldContain payout2
    }

    @Test
    fun `should store payouts for different users separately`() {
        val payout1 = PayoutMother.of()
        val payout2 = PayoutMother.of()

        storage.store(payout1)
        storage.store(payout2)

        storage.findByUserId(payout1.userId) shouldHaveSize 1
        storage.findByUserId(payout2.userId) shouldHaveSize 1
        storage.findByUserId(payout1.userId) shouldContain payout1
        storage.findByUserId(payout2.userId) shouldContain payout2
    }
}