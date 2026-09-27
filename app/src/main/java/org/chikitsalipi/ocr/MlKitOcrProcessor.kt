package org.chikitsalipi.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.io.File

object MlKitOcrProcessor {
    fun processImage(
        context: Context,
        imageFile: File,
        onComplete: (rawText: String, confidence: Float) -> Unit
    ) {
        try {
            val inputImage = InputImage.fromFilePath(context, Uri.fromFile(imageFile))
            val recognizer = TextRecognition.getClient()

            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text
                    val confidence = if (text.isNotBlank()) 0.88f else 0.0f
                    onComplete(text, confidence)
                }
                .addOnFailureListener { e ->
                    Log.e("MlKitOcrProcessor", "OCR Failed: ${e.message}", e)
                    onComplete("", 0.0f)
                }
        } catch (e: Exception) {
            Log.e("MlKitOcrProcessor", "Exception initializing OCR: ${e.message}", e)
            onComplete("", 0.0f)
        }
    }
}
