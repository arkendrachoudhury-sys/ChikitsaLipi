package org.chikitsalipi.extraction

import org.chikitsalipi.model.BoundingBoxInfo
import org.chikitsalipi.model.ExtractedField
import org.chikitsalipi.model.TranslationPair
import org.chikitsalipi.model.VerificationStatus
import java.util.UUID

class DefaultMedicalExtractor : MedicalExtractor {

    override fun extractFields(rawText: String): List<ExtractedField> {
        val extracted = mutableListOf<ExtractedField>()

        if (rawText.isBlank()) return extracted

        val lines = rawText.lines()

        // Regex pattern for Blood Pressure (e.g. 120/80 mmHg or BP: 130/85)
        val bpRegex = Regex("""(?i)\b(?:BP|Blood\s*Pressure):?\s*(\d{2,3}\s*/\s*\d{2,3})\s*(mmHg)?\b|\b(\d{2,3}\s*/\s*\d{2,3})\s*(mmHg)\b""")
        // Regex pattern for Hemoglobin (e.g. Hb: 12.5 g/dL or Hemoglobin 14 g/dl)
        val hbRegex = Regex("""(?i)\b(?:Hb|Hemoglobin):?\s*(\d{1,2}(?:\.\d)?)\s*(g/dL|g/dl)?\b""")
        // Regex pattern for Pulse / Heart Rate (e.g. Pulse: 72 bpm or HR: 80)
        val pulseRegex = Regex("""(?i)\b(?:Pulse|HR|Heart\s*Rate):?\s*(\d{2,3})\s*(bpm)?\b|\b(\d{2,3})\s*(bpm)\b""")
        // Regex pattern for Temperature (e.g. Temp: 98.6 F or 37 C)
        val tempRegex = Regex("""(?i)\b(?:Temp|Temperature):?\s*(\d{2,3}(?:\.\d)?)\s*(°?[FC])?\b|\b(\d{2,3}(?:\.\d)?)\s*(°[FC])\b""")
        // Regex pattern for Blood Glucose (e.g. Glucose: 110 mg/dL or RBS: 140)
        val glucoseRegex = Regex("""(?i)\b(?:Glucose|RBS|FBS):?\s*(\d{2,3})\s*(mg/dL|mg/dl)?\b""")

        for (line in lines) {
            val bpMatch = bpRegex.find(line)
            if (bpMatch != null) {
                val valStr = bpMatch.groupValues[1].ifEmpty { bpMatch.groupValues[3] }
                val unitStr = bpMatch.groupValues[2].ifEmpty { bpMatch.groupValues[4] }.ifBlank { "mmHg" }
                extracted.add(
                    ExtractedField(
                        fieldId = UUID.randomUUID().toString(),
                        categoryName = "Vital Signs",
                        fieldName = "Blood Pressure",
                        extractedValue = valStr,
                        unit = unitStr,
                        confidenceScore = 0.90f,
                        sourceText = line.trim(),
                        sourceBoundingBox = BoundingBoxInfo(0, 0, 0, 0),
                        status = VerificationStatus.HIGH_CONFIDENCE,
                        translations = listOf(
                            TranslationPair("bn", "রক্তচাপ: $valStr $unitStr"),
                            TranslationPair("hi", "रक्तचाप: $valStr $unitStr")
                        )
                    )
                )
            }

            val hbMatch = hbRegex.find(line)
            if (hbMatch != null) {
                val valStr = hbMatch.groupValues[1]
                val unitStr = hbMatch.groupValues[2].ifBlank { "g/dL" }
                extracted.add(
                    ExtractedField(
                        fieldId = UUID.randomUUID().toString(),
                        categoryName = "Lab Result",
                        fieldName = "Hemoglobin",
                        extractedValue = valStr,
                        unit = unitStr,
                        confidenceScore = 0.88f,
                        sourceText = line.trim(),
                        sourceBoundingBox = BoundingBoxInfo(0, 0, 0, 0),
                        status = VerificationStatus.HIGH_CONFIDENCE,
                        translations = listOf(
                            TranslationPair("bn", "হিমোগ্লোবিন: $valStr $unitStr"),
                            TranslationPair("hi", "हीमोग्लोबिन: $valStr $unitStr")
                        )
                    )
                )
            }

            val pulseMatch = pulseRegex.find(line)
            if (pulseMatch != null) {
                val valStr = pulseMatch.groupValues[1].ifEmpty { pulseMatch.groupValues[3] }
                val unitStr = pulseMatch.groupValues[2].ifEmpty { pulseMatch.groupValues[4] }.ifBlank { "bpm" }
                extracted.add(
                    ExtractedField(
                        fieldId = UUID.randomUUID().toString(),
                        categoryName = "Vital Signs",
                        fieldName = "Pulse",
                        extractedValue = valStr,
                        unit = unitStr,
                        confidenceScore = 0.85f,
                        sourceText = line.trim(),
                        sourceBoundingBox = BoundingBoxInfo(0, 0, 0, 0),
                        status = VerificationStatus.HIGH_CONFIDENCE,
                        translations = listOf(
                            TranslationPair("bn", "পালস: $valStr $unitStr"),
                            TranslationPair("hi", "पल्स: $valStr $unitStr")
                        )
                    )
                )
            }

            val tempMatch = tempRegex.find(line)
            if (tempMatch != null) {
                val valStr = tempMatch.groupValues[1].ifEmpty { tempMatch.groupValues[3] }
                val unitStr = tempMatch.groupValues[2].ifEmpty { tempMatch.groupValues[4] }.ifBlank { "°F" }
                extracted.add(
                    ExtractedField(
                        fieldId = UUID.randomUUID().toString(),
                        categoryName = "Vital Signs",
                        fieldName = "Temperature",
                        extractedValue = valStr,
                        unit = unitStr,
                        confidenceScore = 0.85f,
                        sourceText = line.trim(),
                        sourceBoundingBox = BoundingBoxInfo(0, 0, 0, 0),
                        status = VerificationStatus.HIGH_CONFIDENCE,
                        translations = listOf(
                            TranslationPair("bn", "তাপমাত্রা: $valStr $unitStr"),
                            TranslationPair("hi", "तापमान: $valStr $unitStr")
                        )
                    )
                )
            }

            val glucoseMatch = glucoseRegex.find(line)
            if (glucoseMatch != null) {
                val valStr = glucoseMatch.groupValues[1]
                val unitStr = glucoseMatch.groupValues[2].ifBlank { "mg/dL" }
                extracted.add(
                    ExtractedField(
                        fieldId = UUID.randomUUID().toString(),
                        categoryName = "Lab Result",
                        fieldName = "Blood Glucose",
                        extractedValue = valStr,
                        unit = unitStr,
                        confidenceScore = 0.85f,
                        sourceText = line.trim(),
                        sourceBoundingBox = BoundingBoxInfo(0, 0, 0, 0),
                        status = VerificationStatus.HIGH_CONFIDENCE,
                        translations = listOf(
                            TranslationPair("bn", "গ্লুকোজ: $valStr $unitStr"),
                            TranslationPair("hi", "ग्लूकोज: $valStr $unitStr")
                        )
                    )
                )
            }
        }

        return extracted
    }
}
