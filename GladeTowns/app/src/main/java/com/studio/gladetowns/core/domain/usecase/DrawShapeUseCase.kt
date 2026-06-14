package com.studio.gladetowns.core.domain.usecase

import com.studio.gladetowns.core.domain.model.command.TownCommand
import com.studio.gladetowns.core.domain.model.shape.RawStroke
import com.studio.gladetowns.core.domain.model.shape.ShapeClass
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.StructureId
import com.studio.gladetowns.core.domain.model.town.TownState
import com.studio.gladetowns.core.domain.procgen.ArchetypeMapping
import com.studio.gladetowns.core.domain.procgen.recognize.ShapeRecognizer
import com.studio.gladetowns.core.domain.rng.SplitMix64
import javax.inject.Inject

/**
 * The micro-loop (TDD §2.2): stroke → recognize → footprint → command.
 *
 * Pure & synchronous (runs on Dispatchers.Default in the ViewModel). Applies
 * the overlap/merge policy by clipping the footprint against already-occupied
 * cells, then emits a deterministic PlaceStructure. Returns null when the
 * stroke yields nothing placeable (too small / fully overlapping).
 */
class DrawShapeUseCase @Inject constructor() {

    data class Result(
        val command: TownCommand.PlaceStructure,
        val shapeClass: ShapeClass,
        val confidence: Float,
    )

    operator fun invoke(stroke: RawStroke, state: TownState, masterSeed: Long, atMs: Long): Result? {
        val recognized = ShapeRecognizer.recognize(stroke, state.gridSpec) ?: return null

        // Overlap policy (TDD §11.2): clip cells already taken by other buildings.
        val occupied = HashSet<Int>()
        for (s in state.structures.values) occupied.addAll(s.footprint.packedCells)
        val cells = recognized.footprint.packedCells.filter { it !in occupied }
        if (cells.size < ShapeRecognizer.MIN_CELLS) return null

        val seq = state.nextLocalSeq
        val structureId = StructureId(SplitMix64.child(masterSeed, seq))
        val derivedSeed = SplitMix64.child(structureId.value, "gen")
        val archetype = ArchetypeMapping.map(recognized.result)

        return Result(
            command = TownCommand.PlaceStructure(
                seq = seq,
                atMs = atMs,
                structureId = structureId,
                archetype = archetype,
                footprint = Footprint(cells.sorted()),
                derivedSeed = derivedSeed,
                stroke = stroke,
            ),
            shapeClass = recognized.result.shapeClass,
            confidence = recognized.result.confidence,
        )
    }
}
