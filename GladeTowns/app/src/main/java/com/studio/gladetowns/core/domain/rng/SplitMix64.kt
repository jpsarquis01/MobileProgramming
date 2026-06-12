package com.studio.gladetowns.core.domain.rng

/**
 * Deterministic seed derivation (TDD §8.1).
 *
 * masterSeed(town) → child(placementSeq) → child(stage/variant)…
 * SplitMix64 is tiny, fast, statistically solid for our purposes, and —
 * critically — produces identical results on every device and JVM, which is
 * the contract that makes saved towns replayable forever.
 */
object SplitMix64 {

    fun mix(seed: Long): Long {
        var z = seed + -0x61c8864680b583ebL // 0x9E3779B97F4A7C15
        z = (z xor (z ushr 30)) * -0x40a7b892e31b1a47L // 0xBF58476D1CE4E5B9
        z = (z xor (z ushr 27)) * -0x6b2fb644ecceee15L // 0x94D049BB133111EB
        return z xor (z ushr 31)
    }

    /** Derive a child seed from a parent and an index in the derivation tree. */
    fun child(parent: Long, index: Int): Long = mix(parent xor mix(index.toLong()))

    /** Derive a labelled child (e.g. per pipeline stage). */
    fun child(parent: Long, label: String): Long {
        var h = 1125899906842597L
        for (c in label) h = 31 * h + c.code
        return mix(parent xor mix(h))
    }
}
