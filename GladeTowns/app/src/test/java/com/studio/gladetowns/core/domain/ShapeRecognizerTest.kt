package com.studio.gladetowns.core.domain

import com.studio.gladetowns.core.domain.model.shape.RawStroke
import com.studio.gladetowns.core.domain.model.shape.ShapeClass
import com.studio.gladetowns.core.domain.model.shape.StrokePoint
import com.studio.gladetowns.core.domain.model.town.GridSize
import com.studio.gladetowns.core.domain.model.town.GridSpec
import com.studio.gladetowns.core.domain.procgen.recognize.ShapeRecognizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class ShapeRecognizerTest {

    private val spec = GridSpec(GridSize.MEDIUM)

    private fun stroke(pts: List<Pair<Float, Float>>) =
        RawStroke(pts.map { StrokePoint.of(it.first, it.second) }, spec.cellsPerSide)

    private fun edge(a: Pair<Float, Float>, b: Pair<Float, Float>, n: Int) =
        (0..n).map { i ->
            val t = i / n.toFloat()
            (a.first + (b.first - a.first) * t) to (a.second + (b.second - a.second) * t)
        }

    @Test
    fun `square is recognized as rectangle`() {
        val pts = edge(10f to 10f, 20f to 10f, 20) +
            edge(20f to 10f, 20f to 20f, 20) +
            edge(20f to 20f, 10f to 20f, 20) +
            edge(10f to 20f, 10f to 10f, 20)
        val r = ShapeRecognizer.recognize(stroke(pts), spec)!!
        assertEquals(ShapeClass.RECTANGLE, r.result.shapeClass)
        assertTrue(r.footprint.cellCount > 30)
    }

    @Test
    fun `circle is recognized as circle`() {
        val pts = (0..72).map {
            val a = it / 72f * 2f * Math.PI.toFloat()
            (25f + 6f * cos(a)) to (25f + 6f * sin(a))
        }
        val r = ShapeRecognizer.recognize(stroke(pts), spec)!!
        assertEquals(ShapeClass.CIRCLE, r.result.shapeClass)
    }

    @Test
    fun `triangle is recognized as triangle`() {
        val pts = edge(15f to 25f, 25f to 25f, 24) +
            edge(25f to 25f, 20f to 15f, 24) +
            edge(20f to 15f, 15f to 25f, 24)
        val r = ShapeRecognizer.recognize(stroke(pts), spec)!!
        assertEquals(ShapeClass.TRIANGLE, r.result.shapeClass)
    }
}
