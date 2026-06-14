package com.studio.gladetowns.core.domain.model.shape

/** Shape classes the recognizer can emit (gameplay pipeline). */
enum class ShapeClass { RECTANGLE, CIRCLE, TRIANGLE, IRREGULAR }

/** Scalar features extracted from a normalized stroke. */
data class ShapeFeatures(
    val area: Float,
    val perimeter: Float,
    val circularity: Float,     // 4πA / P²  (1.0 = perfect circle)
    val rectangularity: Float,  // area / axis-aligned bbox area
    val aspectRatio: Float,     // longSide / shortSide of bbox
    val cornerCount: Int,       // RDP-simplified vertex count
)

/** Output of recognition: class + features + confidence. */
data class RecognitionResult(
    val shapeClass: ShapeClass,
    val features: ShapeFeatures,
    val confidence: Float,
)
