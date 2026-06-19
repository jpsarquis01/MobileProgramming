package com.studio.gladetowns.core.ui.render

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.studio.gladetowns.core.domain.model.structure.RoomType
import kotlin.math.min

/**
 * Little top-down furniture drawn inside a room's interior, matched to its
 * type — beds in bedrooms, a toilet in bathrooms, a counter in kitchens, a sofa
 * in common areas, a TV in TV areas, trees in gardens. Pure Canvas (no assets);
 * used by the diorama (Explore) view to "decorate" finished towns.
 *
 * Each piece is laid out inside the interior rect (l, t, w, h) in pixels.
 */
fun DrawScope.drawFurniture(type: RoomType, l: Float, t: Float, w: Float, h: Float) {
    if (w <= 4f || h <= 4f) return
    when (type) {
        RoomType.BEDROOM -> bed(l, t, w, h)
        RoomType.BATHROOM -> bathroom(l, t, w, h)
        RoomType.KITCHEN -> kitchen(l, t, w, h)
        RoomType.COMMON_AREA, RoomType.LOUNGE -> sofa(l, t, w, h)
        RoomType.TV_AREA -> tvArea(l, t, w, h)
        RoomType.GARDEN -> garden(l, t, w, h)
        RoomType.DOOR -> Unit // a doorway needs no furniture
    }
}

private val Wood = Color(0xFF93704A)
private val WoodDark = Color(0xFF6E5236)
private val Sheet = Color(0xFFF4EFE6)
private val Pillow = Color(0xFFFBF7EF)
private val Blanket = Color(0xFF8FA9C4)
private val Fabric = Color(0xFF7C93B0)
private val FabricDark = Color(0xFF61748D)
private val Porcelain = Color(0xFFEDEFF0)
private val Metal = Color(0xFF8B8F96)
private val Screen = Color(0xFF2B2F38)
private val Trunk = Color(0xFF7A5230)
private val Leaf = Color(0xFF5E8C49)
private val LeafHi = Color(0xFF74A35C)
private val Flower = Color(0xFFE08AA0)

private fun box(w: Float, h: Float) = min(w, h)

private fun DrawScope.bed(l: Float, t: Float, w: Float, h: Float) {
    val pad = box(w, h) * 0.14f
    val x = l + pad; val y = t + pad; val bw = w - 2 * pad; val bh = h - 2 * pad
    val r = CornerRadius(box(bw, bh) * 0.10f)
    drawRoundRect(Wood, Offset(x, y), Size(bw, bh), r)
    val m = box(bw, bh) * 0.07f
    drawRoundRect(Sheet, Offset(x + m, y + m), Size(bw - 2 * m, bh - 2 * m), r)
    // Pillow band at the head (top), blanket over the rest.
    drawRoundRect(Pillow, Offset(x + m * 1.6f, y + m * 1.6f), Size(bw - 3.2f * m, bh * 0.20f), r)
    drawRoundRect(Blanket, Offset(x + m, y + bh * 0.42f), Size(bw - 2 * m, bh * 0.58f - m), r)
}

private fun DrawScope.bathroom(l: Float, t: Float, w: Float, h: Float) {
    val u = box(w, h)
    // Toilet: tank + bowl, parked top-left.
    val tx = l + u * 0.18f; val ty = t + u * 0.18f
    drawRoundRect(Porcelain, Offset(tx, ty), Size(u * 0.30f, u * 0.16f), CornerRadius(u * 0.04f))
    drawOval(Porcelain, Offset(tx, ty + u * 0.16f), Size(u * 0.30f, u * 0.34f))
    drawOval(Color(0xFFD7DBDD), Offset(tx + u * 0.05f, ty + u * 0.22f), Size(u * 0.20f, u * 0.22f))
    // Sink, parked to the right.
    val sx = l + w - u * 0.42f; val sy = t + u * 0.20f
    drawRoundRect(Porcelain, Offset(sx, sy), Size(u * 0.26f, u * 0.22f), CornerRadius(u * 0.05f))
    drawCircle(Metal, u * 0.02f, Offset(sx + u * 0.13f, sy + u * 0.05f))
}

private fun DrawScope.kitchen(l: Float, t: Float, w: Float, h: Float) {
    val pad = box(w, h) * 0.12f
    // L-counter along the top edge.
    val cH = h * 0.22f
    drawRoundRect(WoodDark, Offset(l + pad, t + pad), Size(w - 2 * pad, cH), CornerRadius(cH * 0.18f))
    drawRoundRect(Color(0xFFCDBfA3), Offset(l + pad, t + pad), Size(w - 2 * pad, cH * 0.45f), CornerRadius(cH * 0.18f))
    // Stove burners.
    val by = t + pad + cH * 0.5f
    val r = cH * 0.16f
    val n = 3
    for (i in 0 until n) {
        val bx = l + pad + (w - 2 * pad) * (0.18f + 0.30f * i)
        drawCircle(Color(0xFF3C3C3C), r, Offset(bx, by))
    }
    // Sink basin at the far right of the counter.
    drawRoundRect(Metal, Offset(l + w - pad - w * 0.16f, by - r), Size(w * 0.12f, r * 2f), CornerRadius(r * 0.4f))
}

private fun DrawScope.sofa(l: Float, t: Float, w: Float, h: Float) {
    val pad = box(w, h) * 0.16f
    val x = l + pad; val y = t + pad; val sw = w - 2 * pad; val sh = h - 2 * pad
    val r = CornerRadius(box(sw, sh) * 0.16f)
    // Backrest (top), then the seat with two cushions.
    drawRoundRect(FabricDark, Offset(x, y), Size(sw, sh), r)
    drawRoundRect(Fabric, Offset(x, y + sh * 0.30f), Size(sw, sh * 0.70f), r)
    val cw = (sw - sh * 0.18f) / 2f
    drawRoundRect(Pillow.copy(alpha = 0.85f), Offset(x + sh * 0.06f, y + sh * 0.38f), Size(cw - sh * 0.06f, sh * 0.5f), r)
    drawRoundRect(Pillow.copy(alpha = 0.85f), Offset(x + cw + sh * 0.06f, y + sh * 0.38f), Size(cw - sh * 0.06f, sh * 0.5f), r)
}

private fun DrawScope.tvArea(l: Float, t: Float, w: Float, h: Float) {
    val u = box(w, h)
    // Flat screen on a stand along the top.
    val tw = w * 0.6f; val tx = l + (w - tw) / 2f; val ty = t + u * 0.14f
    drawRoundRect(Screen, Offset(tx, ty), Size(tw, u * 0.18f), CornerRadius(u * 0.02f))
    drawRoundRect(Color(0xFF454B57), Offset(tx + tw / 2f - u * 0.03f, ty + u * 0.18f), Size(u * 0.06f, u * 0.06f), CornerRadius(0f))
    drawRoundRect(WoodDark, Offset(tx + tw * 0.2f, ty + u * 0.24f), Size(tw * 0.6f, u * 0.04f), CornerRadius(u * 0.02f))
    // A couch facing it (bottom).
    val cw = w * 0.6f; val cx = l + (w - cw) / 2f; val cy = t + h - u * 0.34f
    drawRoundRect(Fabric, Offset(cx, cy), Size(cw, u * 0.20f), CornerRadius(u * 0.06f))
}

private fun DrawScope.garden(l: Float, t: Float, w: Float, h: Float) {
    val u = box(w, h)
    // A few trees scattered + flower dots; scales with the patch.
    val spots = listOf(0.28f to 0.34f, 0.66f to 0.30f, 0.45f to 0.68f)
    val tr = u * 0.12f
    for ((fx, fy) in spots) {
        val cx = l + w * fx; val cy = t + h * fy
        drawRoundRect(Trunk, Offset(cx - tr * 0.12f, cy), Size(tr * 0.24f, tr * 0.8f), CornerRadius(tr * 0.1f))
        drawCircle(Leaf, tr, Offset(cx, cy - tr * 0.2f))
        drawCircle(LeafHi, tr * 0.6f, Offset(cx - tr * 0.4f, cy - tr * 0.5f))
    }
    val fd = u * 0.03f
    for (p in listOf(0.16f to 0.6f, 0.82f to 0.7f, 0.55f to 0.2f, 0.35f to 0.85f)) {
        drawCircle(Flower, fd, Offset(l + w * p.first, t + h * p.second))
    }
}
