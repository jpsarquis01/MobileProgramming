package com.studio.gladetowns.core.domain.model.town

import com.studio.gladetowns.core.domain.model.structure.Footprint

/**
 * A compact, fixed-resolution downsample of a [TownLayout] used to paint
 * snow-globe gallery previews cheaply (no per-card replay on the draw path).
 * Built once per town and cached by the gallery ViewModel.
 */
class TownPreview(
    val sample: Int,
    val palette: IntArray,   // size sample*sample; building palette index or -1
    val roads: BooleanArray, // size sample*sample
) {
    companion object {
        const val SAMPLE = 28

        fun from(layout: TownLayout, sample: Int = SAMPLE): TownPreview {
            val pal = IntArray(sample * sample) { -1 }
            val road = BooleanArray(sample * sample)
            val cells = layout.gridCells.coerceAtLeast(1)

            fun idx(x: Int, y: Int): Int {
                val sx = (x * sample / cells).coerceIn(0, sample - 1)
                val sy = (y * sample / cells).coerceIn(0, sample - 1)
                return sy * sample + sx
            }

            for (c in layout.roads) road[idx(Footprint.unpackX(c), Footprint.unpackY(c))] = true
            // Placement order preserved: later buildings overwrite (visually on top).
            for (plan in layout.structures) {
                for (c in plan.footprint) pal[idx(Footprint.unpackX(c), Footprint.unpackY(c))] = plan.paletteIndex
            }
            return TownPreview(sample, pal, road)
        }
    }
}
