package com.studio.gladetowns.core.domain.procgen.generate

import com.studio.gladetowns.core.domain.model.structure.BuildingMass
import com.studio.gladetowns.core.domain.model.structure.GridRect
import com.studio.gladetowns.core.domain.model.structure.RoofKind
import com.studio.gladetowns.core.domain.model.structure.Structure
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.rng.SplitMix64
import kotlin.math.max
import kotlin.math.min

/**
 * Deterministic building assembly (TDD §10).
 *
 * Given a structure (archetype, footprint, seed, variantIndex) plus neighbor
 * context, produce a StructurePlan. Same inputs → identical plan, every device,
 * forever. Variation comes only from variantIndex (re-roll) and the seed tree.
 */
object BuildingGenerator {

    /** Context supplied by the growth engine (deterministically computed). */
    data class GenContext(
        val neighborMaxFloors: Int,
        val paletteIndex: Int,
    )

    private class Rng(seed: Long) {
        private var s = seed
        fun nextLong(): Long { s = SplitMix64.mix(s); return s }
        fun nextInt(bound: Int): Int = ((nextLong() ushr 1) % bound).toInt()
        fun chance(pct: Int): Boolean = nextInt(100) < pct
    }

    fun generate(structure: Structure, ctx: GenContext): StructurePlan {
        val rng = Rng(SplitMix64.child(structure.structureSeed, structure.variantIndex))
        val bbox = boundingRect(structure.footprint)
        val masses = when (structure.archetype) {
            StructureArchetype.HOUSE -> house(bbox, rng, ctx)
            StructureArchetype.WAREHOUSE -> warehouse(bbox, rng, ctx)
            StructureArchetype.TOWER -> tower(bbox, rng, ctx)
            StructureArchetype.CHAPEL -> chapel(bbox, rng, ctx)
            StructureArchetype.FOLLY -> folly(bbox, rng, ctx)
            StructureArchetype.L_SHAPE, StructureArchetype.PROP ->
                listOf(BuildingMass(bbox, 1, RoofKind.FLAT))
        }
        return StructurePlan(
            structureId = structure.id,
            archetype = structure.archetype,
            masses = masses,
            paletteIndex = ctx.paletteIndex,
            footprint = structure.footprint.packedCells,
        )
    }

    // --- per-archetype rules -------------------------------------------------

    private fun house(b: GridRect, rng: Rng, ctx: GenContext): List<BuildingMass> {
        var floors = 1 + rng.nextInt(2)              // 1..2
        floors = harmonize(floors, ctx, rng)
        val roof = if (b.w == b.h) RoofKind.HIP else RoofKind.GABLE
        return listOf(BuildingMass(b, floors, roof))
    }

    private fun warehouse(b: GridRect, rng: Rng, ctx: GenContext): List<BuildingMass> {
        val floors = harmonize(1, ctx, rng)
        return listOf(BuildingMass(b, floors, RoofKind.FLAT))
    }

    private fun tower(b: GridRect, rng: Rng, ctx: GenContext): List<BuildingMass> {
        val diameter = min(b.w, b.h)
        var floors = 3 + diameter / 2 + rng.nextInt(2) // taller with size
        floors = harmonize(floors, ctx, rng).coerceAtMost(9)
        return listOf(BuildingMass(b, floors, RoofKind.CONE))
    }

    private fun chapel(b: GridRect, rng: Rng, ctx: GenContext): List<BuildingMass> {
        val floors = harmonize(1 + rng.nextInt(2), ctx, rng)
        return listOf(BuildingMass(b, floors, RoofKind.SPIRE))
    }

    private fun folly(b: GridRect, rng: Rng, ctx: GenContext): List<BuildingMass> {
        // Decompose the bbox into up to 2 sub-masses for a "grown over time" look.
        val masses = ArrayList<BuildingMass>(2)
        if (b.w >= 4 && rng.chance(60)) {
            val cut = 1 + rng.nextInt(max(1, b.w - 1))
            masses += BuildingMass(GridRect(b.x, b.y, cut, b.h), harmonize(1 + rng.nextInt(2), ctx, rng), pickRoof(rng))
            masses += BuildingMass(GridRect(b.x + cut, b.y, b.w - cut, b.h), harmonize(1 + rng.nextInt(3), ctx, rng), pickRoof(rng))
        } else if (b.h >= 4 && rng.chance(60)) {
            val cut = 1 + rng.nextInt(max(1, b.h - 1))
            masses += BuildingMass(GridRect(b.x, b.y, b.w, cut), harmonize(1 + rng.nextInt(2), ctx, rng), pickRoof(rng))
            masses += BuildingMass(GridRect(b.x, b.y + cut, b.w, b.h - cut), harmonize(1 + rng.nextInt(3), ctx, rng), pickRoof(rng))
        } else {
            masses += BuildingMass(b, harmonize(1 + rng.nextInt(2), ctx, rng), pickRoof(rng))
        }
        return masses
    }

    private fun pickRoof(rng: Rng): RoofKind =
        when (rng.nextInt(3)) { 0 -> RoofKind.GABLE; 1 -> RoofKind.HIP; else -> RoofKind.FLAT }

    /** Height harmonization (TDD §11): nudge taller near taller neighbors. */
    private fun harmonize(base: Int, ctx: GenContext, rng: Rng): Int {
        var f = base
        if (ctx.neighborMaxFloors > base && rng.chance(40)) f += 1
        return f.coerceAtLeast(1)
    }

    private fun boundingRect(footprint: Footprint): GridRect {
        var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE; var maxY = Int.MIN_VALUE
        for (p in footprint.packedCells) {
            val x = Footprint.unpackX(p); val y = Footprint.unpackY(p)
            minX = min(minX, x); maxX = max(maxX, x)
            minY = min(minY, y); maxY = max(maxY, y)
        }
        return GridRect(minX, minY, maxX - minX + 1, maxY - minY + 1)
    }
}
