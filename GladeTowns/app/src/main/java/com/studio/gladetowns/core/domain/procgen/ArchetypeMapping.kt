package com.studio.gladetowns.core.domain.procgen

import com.studio.gladetowns.core.domain.model.shape.RecognitionResult
import com.studio.gladetowns.core.domain.model.shape.ShapeClass
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype

/**
 * Shape class (+ features) → building archetype (TDD §9.2 / §10).
 *   Rectangle (squarish) → House
 *   Rectangle (long)     → Warehouse
 *   Circle               → Tower
 *   Triangle             → Chapel
 *   Irregular            → Folly
 */
object ArchetypeMapping {
    private const val LONG_ASPECT = 2.2f

    fun map(result: RecognitionResult): StructureArchetype = when (result.shapeClass) {
        ShapeClass.RECTANGLE ->
            if (result.features.aspectRatio >= LONG_ASPECT) StructureArchetype.WAREHOUSE
            else StructureArchetype.HOUSE
        ShapeClass.CIRCLE -> StructureArchetype.TOWER
        ShapeClass.TRIANGLE -> StructureArchetype.CHAPEL
        ShapeClass.IRREGULAR -> StructureArchetype.FOLLY
    }
}
