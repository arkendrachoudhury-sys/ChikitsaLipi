package org.chikitsalipi.preprocessing

import android.graphics.Bitmap

data class PreprocessingResult(
    val processedBitmap: Bitmap,
    val isBlurry: Boolean,
    val isLowLight: Boolean,
    val isDocumentDetected: Boolean
)

interface ImagePreprocessor {
    suspend fun assessQuality(bitmap: Bitmap): PreprocessingResult
    suspend fun preprocess(bitmap: Bitmap): Bitmap
}
