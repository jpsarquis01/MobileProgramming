package com.studio.gladetowns.core.domain.procgen.recognize

import com.studio.gladetowns.core.domain.model.shape.RawStroke
import com.studio.gladetowns.core.domain.model.shape.RecognitionResult
import com.studio.gladetowns.core.domain.model.shape.ShapeClass
import com.studio.gladetowns.core.domain.model.shape.ShapeFeatures
import com.studio.gladetowns.core.domain.model.structure.Footprint
import com.studio.gladetowns.core.domain.model.town.GridSpec
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Stroke → shape class + grid footprint (TDD §9).
 *
 * Forgiving rule-cascade classifier: every stroke yields *something*; weird
 * strokes route to IRREGULAR rather than failing. Floats are fine here because
 * the RESULT (footprint + class) is persisted, so replay never re-runs this.
 */
object ShapeRecognizer {

    private const val RESAMPLE = 48
    private const val COVERAGE_INSET = 0.5f // sample cell centers

    data class Recognized(
        val result: RecognitionResult,
        val footprint: Footprint,
    )

    private class Pt(val x: Float, val y: Float)

    /** Returns null if the stroke is too small to form a footprint. */
    fun recognize(stroke: RawStroke, gridSpec: GridSpec): Recognized? {
        val raw = stroke.points.map { Pt(it.x, it.y) }
        if (raw.size < 3) return null

        val resampled = resample(raw, RESAMPLE)
        val poly = closeLoop(resampled)
        if (poly.size < 3) return null

        val features = extractFeatures(poly)
        if (features.area < 1.5f) return null // smaller than ~1.5 cells → ignore

        val (shapeClass, confidence) = classify(features)
        val cells = rasterize(poly, gridSpec)
        if (cells.size < MIN_CELLS) return null

        return Recognized(
            RecognitionResult(shapeClass, features, confidence),
            Footprint(cells),
        )
    }

    const val MIN_CELLS = 2

    // ---- Stage 1: normalize -------------------------------------------------

    private fun resample(points: List<Pt>, n: Int): List<Pt> {
        val total = pathLength(points)
        if (total <= 0f) return points
        val step = total / (n - 1)
        val out = ArrayList<Pt>(n)
        out.add(points.first())
        var prev = points.first()
        var acc = 0f
        var i = 1
        while (i < points.size && out.size < n) {
            val cur = points[i]
            val d = dist(prev, cur)
            if (acc + d >= step) {
                val t = (step - acc) / d
                val np = Pt(prev.x + (cur.x - prev.x) * t, prev.y + (cur.y - prev.y) * t)
                out.add(np)
                prev = np
                acc = 0f
            } else {
                acc += d
                prev = cur
                i++
            }
        }
        while (out.size < n) out.add(points.last())
        return out
    }

    private fun closeLoop(points: List<Pt>): List<Pt> {
        // Cozy design: always auto-close (the canvas only supports closed shapes this phase).
        return points + points.first()
    }

    // ---- Stage 2: features --------------------------------------------------

    private fun extractFeatures(poly: List<Pt>): ShapeFeatures {
        val area = abs(shoelace(poly))
        val perim = pathLength(poly)
        val circularity = if (perim > 0f) (4f * Math.PI.toFloat() * area) / (perim * perim) else 0f

        var minX = Float.MAX_VALUE; var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
        for (p in poly) {
            minX = min(minX, p.x); maxX = max(maxX, p.x)
            minY = min(minY, p.y); maxY = max(maxY, p.y)
        }
        val w = max(0.001f, maxX - minX)
        val h = max(0.001f, maxY - minY)
        val rectangularity = (area / (w * h)).coerceIn(0f, 1f)
        val aspect = max(w, h) / min(w, h)

        val diag = hypot(w, h)
        val corners = rdpCount(poly.dropLast(1), 0.045f * diag)

        return ShapeFeatures(area, perim, circularity, rectangularity, aspect, corners)
    }

    // ---- Stage 3: classify --------------------------------------------------

    private fun classify(f: ShapeFeatures): Pair<ShapeClass, Float> = when {
        f.cornerCount == 3 ->
            ShapeClass.TRIANGLE to 0.80f

        (f.cornerCount == 4 && f.rectangularity >= 0.70f) || f.rectangularity >= 0.85f ->
            ShapeClass.RECTANGLE to f.rectangularity

        f.circularity >= 0.72f ->
            ShapeClass.CIRCLE to f.circularity.coerceIn(0f, 1f)

        else ->
            ShapeClass.IRREGULAR to 0.5f
    }

    // ---- Footprint rasterization (point-in-polygon over bbox) ---------------

    private fun rasterize(poly: List<Pt>, gridSpec: GridSpec): List<Int> {
        var minX = Float.MAX_VALUE; var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
        for (p in poly) {
            minX = min(minX, p.x); maxX = max(maxX, p.x)
            minY = min(minY, p.y); maxY = max(maxY, p.y)
        }
        val cells = ArrayList<Int>()
        val x0 = minX.toInt(); val x1 = maxX.toInt()
        val y0 = minY.toInt(); val y1 = maxY.toInt()
        for (cy in y0..y1) {
            for (cx in x0..x1) {
                if (!gridSpec.isInside(cx, cy)) continue
                if (pointInPoly(poly, cx + COVERAGE_INSET, cy + COVERAGE_INSET)) {
                    cells.add(Footprint.pack(cx, cy))
                }
            }
        }
        return cells.sorted() // stable order
    }

    // ---- geometry helpers ---------------------------------------------------

    private fun shoelace(poly: List<Pt>): Float {
        var s = 0f
        for (i in 0 until poly.size - 1) {
            val a = poly[i]; val b = poly[i + 1]
            s += a.x * b.y - b.x * a.y
        }
        return s / 2f
    }

    private fun pathLength(p: List<Pt>): Float {
        var s = 0f
        for (i in 1 until p.size) s += dist(p[i - 1], p[i])
        return s
    }

    private fun dist(a: Pt, b: Pt) = hypot(a.x - b.x, a.y - b.y)

    private fun pointInPoly(poly: List<Pt>, px: Float, py: Float): Boolean {
        var inside = false
        var j = poly.size - 1
        for (i in poly.indices) {
            val xi = poly[i].x; val yi = poly[i].y
            val xj = poly[j].x; val yj = poly[j].y
            if (((yi > py) != (yj > py)) &&
                (px < (xj - xi) * (py - yi) / ((yj - yi).takeIf { it != 0f } ?: 1e-6f) + xi)
            ) inside = !inside
            j = i
        }
        return inside
    }

    /** Ramer–Douglas–Peucker simplified vertex count for an open chain. */
    private fun rdpCount(points: List<Pt>, eps: Float): Int {
        if (points.size < 3) return points.size
        val isClosed = dist(points.first(), points.last()) < eps
        val keep = BooleanArray(points.size)
        keep[0] = true; keep[points.size - 1] = true
        rdp(points, 0, points.size - 1, eps, keep)
        val count = keep.count { it }
        return if (isClosed && count > 1) count - 1 else count
    }

    private fun rdp(p: List<Pt>, s: Int, e: Int, eps: Float, keep: BooleanArray) {
        var maxD = 0f; var idx = -1
        for (i in s + 1 until e) {
            val d = perpDist(p[i], p[s], p[e])
            if (d > maxD) { maxD = d; idx = i }
        }
        if (maxD > eps && idx != -1) {
            keep[idx] = true
            rdp(p, s, idx, eps, keep)
            rdp(p, idx, e, eps, keep)
        }
    }

    private fun perpDist(pt: Pt, a: Pt, b: Pt): Float {
        val dx = b.x - a.x; val dy = b.y - a.y
        val len = sqrt(dx * dx + dy * dy)
        if (len == 0f) return dist(pt, a)
        return abs(dy * pt.x - dx * pt.y + b.x * a.y - b.y * a.x) / len
    }
}
