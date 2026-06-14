package com.studio.gladetowns.core.domain.model.shape

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 * A single sampled finger point in GRID coordinates, stored as fixed-point
 * ints (1/64 cell) so a serialized stroke is bit-identical across devices.
 */
@Serializable
data class StrokePoint(val xFp: Int, val yFp: Int) {
    val x: Float get() = xFp / FP
    val y: Float get() = yFp / FP

    companion object {
        const val FP = 64f
        fun of(gx: Float, gy: Float) =
            StrokePoint((gx * FP).roundToInt(), (gy * FP).roundToInt())
    }
}

/**
 * The serialized drawing the player made (TDD §6.2). Persisted inside
 * PlaceStructure purely as a record of intent; generation never reads it back
 * (the recognized footprint is what drives buildings), but keeping it makes
 * future re-recognition / analytics / timelapse possible.
 */
@Serializable
data class RawStroke(
    val points: List<StrokePoint>,
    val gridCells: Int,
)
