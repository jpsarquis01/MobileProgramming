package com.studio.gladetowns.core.domain.procgen.merge

import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.Structure
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.structure.StructurePlan
import com.studio.gladetowns.core.domain.model.town.GridSpec
import com.studio.gladetowns.core.domain.model.town.TownLayout
import com.studio.gladetowns.core.domain.procgen.generate.BuildingGenerator
import com.studio.gladetowns.core.domain.rng.SplitMix64
import kotlin.math.abs
import kotlin.math.max

/**
 * Town growth & settlement cohesion (TDD §11). Stateful but PURE: results
 * depend only on (gridSpec, masterSeed, structures-in-placement-order), so
 * [append]-ing incrementally yields the same layout as [rebuild]-ing from
 * scratch — which is what makes revisited saved towns identical.
 *
 *  - Neighbor detection via a chunk-free cell occupancy grid.
 *  - Height harmonization + palette gravity (cohesion) feed the generator.
 *  - Roads: each new building is connected to its nearest predecessor with a
 *    deterministic Manhattan (L-shaped) path, forming a nearest-neighbor road
 *    forest that reads as a settlement.
 */
class TownGrowthEngine(
    private val gridSpec: GridSpec,
    private val masterSeed: Long,
) {
    private val n = gridSpec.cellsPerSide
    private val occupancy = IntArray(n * n) { -1 } // order index or -1
    private val floorAt = IntArray(n * n) { 0 }

    private val plans = LinkedHashMap<StructureId, StructurePlan>()
    private val centroids = LinkedHashMap<StructureId, Int>() // packed centroid
    private val roads = LinkedHashSet<Int>()
    private val ownerByCell = HashMap<Int, Long>()
    private val manualPaths = LinkedHashSet<Int>() // player-drawn hallway cells

    private var orderIndex = 0
    private val townPaletteBase = (abs(masterSeed) % PALETTE_COUNT).toInt()

    fun reset() {
        occupancy.fill(-1); floorAt.fill(0)
        plans.clear(); centroids.clear(); roads.clear(); ownerByCell.clear()
        manualPaths.clear()
        orderIndex = 0
    }

    /**
     * Player-drawn hallway cells to union into roads. Set AFTER [rebuild]/[append]
     * (reset clears them) and BEFORE [snapshot]; cells under a building are
     * ignored so a hallway never overwrites a room.
     */
    fun setManualPaths(cells: Set<Int>) {
        manualPaths.clear()
        manualPaths.addAll(cells)
    }

    fun rebuild(structures: List<Structure>) {
        reset()
        for (s in structures) append(s)
    }

    fun append(structure: Structure) {
        val cells = structure.footprint.packedCells
        if (cells.isEmpty()) return

        val centroid = centroidOf(cells)
        val neighborMaxFloors = neighborMaxFloors(cells)
        val paletteIndex = paletteFor(structure, centroid)

        val plan = BuildingGenerator.generate(
            structure,
            BuildingGenerator.GenContext(neighborMaxFloors, paletteIndex),
        )

        // Stamp occupancy + height; wall fusion is implicit (shared cells coalesce).
        val planFloors = plan.maxFloors
        for (p in cells) {
            val i = idx(p)
            occupancy[i] = orderIndex
            floorAt[i] = max(floorAt[i], planFloors)
            ownerByCell[p] = structure.id.value
        }

        // Roads: connect to nearest already-placed building (settlement cohesion).
        val nearest = nearestPriorCentroid(structure.id, centroid)
        if (nearest != null) routeRoad(centroid, nearest)

        plans[structure.id] = plan
        centroids[structure.id] = centroid
        orderIndex++
    }

    fun snapshot(): TownLayout {
        val allRoads = LinkedHashSet(roads)
        for (p in manualPaths) {
            val x = Footprint.unpackX(p); val y = Footprint.unpackY(p)
            if (gridSpec.isInside(x, y) && occupancy[y * n + x] == -1) allRoads.add(p)
        }
        return TownLayout(
            gridCells = n,
            structures = plans.values.toList(),
            roads = allRoads,
            ownerByCell = HashMap(ownerByCell),
        )
    }

    // --- cohesion helpers ----------------------------------------------------

    private fun neighborMaxFloors(cells: List<Int>): Int {
        var maxFloors = 0
        for (p in cells) {
            val cx = Footprint.unpackX(p); val cy = Footprint.unpackY(p)
            for (dy in -RADIUS..RADIUS) for (dx in -RADIUS..RADIUS) {
                val nx = cx + dx; val ny = cy + dy
                if (gridSpec.isInside(nx, ny)) {
                    val i = ny * n + nx
                    if (occupancy[i] != -1) maxFloors = max(maxFloors, floorAt[i])
                }
            }
        }
        return maxFloors
    }

    /** Palette gravity: mostly the town base, occasionally inherit a neighbor. */
    private fun paletteFor(structure: Structure, centroid: Int): Int {
        val rng = SplitMix64.child(structure.structureSeed, "palette")
        val inherit = (rng ushr 1) % 100 < 35
        if (inherit) {
            val nearest = nearestPriorCentroid(structure.id, centroid)
            if (nearest != null) {
                val owner = ownerNear(nearest)
                if (owner != null) return plans[owner]?.paletteIndex ?: townPaletteBase
            }
        }
        val jitter = ((SplitMix64.mix(rng) ushr 1) % 2).toInt()
        return (townPaletteBase + jitter) % PALETTE_COUNT
    }

    private fun nearestPriorCentroid(self: StructureId, c: Int): Int? {
        var best: Int? = null
        var bestD = Int.MAX_VALUE
        val cx = Footprint.unpackX(c); val cy = Footprint.unpackY(c)
        for ((id, pc) in centroids) {
            if (id == self) continue
            val d = abs(Footprint.unpackX(pc) - cx) + abs(Footprint.unpackY(pc) - cy)
            if (d < bestD) { bestD = d; best = pc }
        }
        return best
    }

    private fun ownerNear(packedCentroid: Int): StructureId? {
        for ((id, pc) in centroids) if (pc == packedCentroid) return id
        return null
    }

    // --- roads ---------------------------------------------------------------

    private fun routeRoad(from: Int, to: Int) {
        var x = Footprint.unpackX(from); val y0 = Footprint.unpackY(from)
        val x1 = Footprint.unpackX(to); val y1 = Footprint.unpackY(to)
        val sx = if (x1 >= x) 1 else -1
        while (x != x1) { addRoadCell(x, y0); x += sx }
        var y = y0
        val sy = if (y1 >= y) 1 else -1
        while (y != y1) { addRoadCell(x1, y); y += sy }
        addRoadCell(x1, y1)
    }

    private fun addRoadCell(x: Int, y: Int) {
        if (!gridSpec.isInside(x, y)) return
        if (occupancy[y * n + x] != -1) return // never under a building
        roads.add(Footprint.pack(x, y))
    }

    // --- geometry ------------------------------------------------------------

    private fun centroidOf(cells: List<Int>): Int {
        var sx = 0; var sy = 0
        for (p in cells) { sx += Footprint.unpackX(p); sy += Footprint.unpackY(p) }
        return Footprint.pack(sx / cells.size, sy / cells.size)
    }

    private fun idx(packed: Int) = Footprint.unpackY(packed) * n + Footprint.unpackX(packed)

    companion object {
        const val PALETTE_COUNT = 6
        private const val RADIUS = 2
    }
}
