package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.structure.RoomClassifier
import com.studio.gladetowns.core.domain.model.structure.RoomType
import com.studio.gladetowns.core.domain.model.structure.StructureArchetype
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomTypeTest {

    private val rect = StructureArchetype.HOUSE
    private val longRect = StructureArchetype.WAREHOUSE
    private val circle = StructureArchetype.TOWER
    private val triangle = StructureArchetype.CHAPEL
    private val irregular = StructureArchetype.FOLLY

    @Test fun `small rectangle is a bathroom`() =
        assertEquals(RoomType.BATHROOM, RoomClassifier.classify(rect, 3, 3))

    @Test fun `mid rectangle is a bedroom and never exceeds 7`() {
        assertEquals(RoomType.BEDROOM, RoomClassifier.classify(rect, 5, 5))
        assertEquals(RoomType.BEDROOM, RoomClassifier.classify(rect, 7, 6))
    }

    @Test fun `large rectangle is a kitchen`() =
        assertEquals(RoomType.KITCHEN, RoomClassifier.classify(longRect, 9, 4))

    @Test fun `circle is a common area`() =
        assertEquals(RoomType.COMMON_AREA, RoomClassifier.classify(circle, 6, 6))

    @Test fun `triangle is a tv area`() =
        assertEquals(RoomType.TV_AREA, RoomClassifier.classify(triangle, 5, 8))

    @Test fun `irregular fills the lounge slot`() =
        assertEquals(RoomType.LOUNGE, RoomClassifier.classify(irregular, 7, 4))

    @Test fun `anything 10 or larger is a garden`() {
        assertEquals(RoomType.GARDEN, RoomClassifier.classify(circle, 10, 10))
        assertEquals(RoomType.GARDEN, RoomClassifier.classify(triangle, 12, 3))
        assertEquals(RoomType.GARDEN, RoomClassifier.classify(rect, 11, 11))
    }

    @Test fun `thin or tiny rectangle is a doorway`() {
        assertEquals(RoomType.DOOR, RoomClassifier.classify(rect, 1, 4))
        assertEquals(RoomType.DOOR, RoomClassifier.classify(rect, 2, 2))
    }

    @Test fun `bounding box is inclusive of both ends`() {
        val cells = listOf(Footprint.pack(2, 2), Footprint.pack(4, 5))
        assertEquals(3 to 4, RoomClassifier.boundingBox(cells))
    }
}
