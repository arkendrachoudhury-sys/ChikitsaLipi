package org.chikitsalipi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VerificationStatus {
    HIGH_CONFIDENCE,
    NEEDS_REVIEW,
    USER_VERIFIED,
    EXTRACTION_FAILED,
    TRANSLATION_UNAVAILABLE
}

enum class DocumentCategory {
    PRESCRIPTION,
    LABORATORY_REPORT,
    CLINICAL_NOTE,
    DIAGNOSTIC_REPORT,
    REFERRAL_DOCUMENT,
    HANDWRITTEN_RECORD,
    OTHER
}

data class BoundingBoxInfo(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

data class TranslationPair(
    val languageCode: String, // "bn", "hi", "en"
    val translatedText: String
)

data class ExtractedField(
    val fieldId: String,
    val categoryName: String,
    val fieldName: String,
    val extractedValue: String,
    val unit: String?,
    val confidenceScore: Float,
    val sourceText: String,
    val sourceBoundingBox: BoundingBoxInfo?,
    val status: VerificationStatus,
    val translations: List<TranslationPair>
)

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey val recordId: String,
    val timestamp: Long,
    val category: DocumentCategory,
    val imagePath: String,
    val rawOcrText: String,
    val ocrConfidence: Float,
    val isHandwritten: Boolean,
    val fieldsJson: String, // JSON serialized list of ExtractedField
    val isFullyVerified: Boolean,
    val processingTimeMs: Long
)
