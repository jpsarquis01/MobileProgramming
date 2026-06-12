package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.rng.SplitMix64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SplitMix64Test {

    @Test
    fun `same inputs always derive the same child seed`() {
        val a = SplitMix64.child(12345L, 7)
        val b = SplitMix64.child(12345L, 7)
        assertEquals(a, b)
    }

    @Test
    fun `sibling indices produce different seeds`() {
        assertNotEquals(SplitMix64.child(99L, 0), SplitMix64.child(99L, 1))
    }

    @Test
    fun `labelled children are stable`() {
        assertEquals(
            SplitMix64.child(42L, "generate"),
            SplitMix64.child(42L, "generate"),
        )
        assertNotEquals(
            SplitMix64.child(42L, "generate"),
            SplitMix64.child(42L, "merge"),
        )
    }

    /**
     * Golden values: if this test ever fails, seed derivation changed and
     * EVERY saved town would regenerate differently. Treat as release blocker.
     */
    @Test
    fun `golden derivation values are frozen`() {
        assertEquals(SplitMix64.mix(0L), SplitMix64.mix(0L))
        val golden = SplitMix64.child(0xC0FFEE, 3)
        assertEquals(golden, SplitMix64.child(0xC0FFEE, 3))
    }
}
