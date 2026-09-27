package org.chikitsalipi.ui

import org.chikitsalipi.model.DocumentCategory
import org.chikitsalipi.model.HealthRecord
import org.junit.Assert.assertEquals
import org.junit.Test

class DirectorySearchTest {

    @Test
    fun testSearchFilteringByRecordIdAndOcrText() {
        val records = listOf(
            HealthRecord(
                recordId = "REC_1001",
                timestamp = System.currentTimeMillis(),
                category = DocumentCategory.PRESCRIPTION,
                imagePath = "/path/1.jpg",
                rawOcrText = "Paracetamol 500mg",
                ocrConfidence = 0.9f,
                isHandwritten = false,
                fieldsJson = "",
                isFullyVerified = false,
                processingTimeMs = 200
            ),
            HealthRecord(
                recordId = "REC_1002",
                timestamp = System.currentTimeMillis(),
                category = DocumentCategory.LABORATORY_REPORT,
                imagePath = "/path/2.jpg",
                rawOcrText = "Hemoglobin 12.5 g/dL",
                ocrConfidence = 0.85f,
                isHandwritten = false,
                fieldsJson = "",
                isFullyVerified = false,
                processingTimeMs = 250
            )
        )

        fun filterRecords(records: List<HealthRecord>, query: String): List<HealthRecord> {
            if (query.isBlank()) return records
            return records.filter { rec ->
                rec.recordId.contains(query, ignoreCase = true) ||
                        rec.category.name.contains(query, ignoreCase = true) ||
                        rec.rawOcrText.contains(query, ignoreCase = true)
            }
        }

        // Search by OCR text
        val paraResult = filterRecords(records, "Paracetamol")
        assertEquals(1, paraResult.size)
        assertEquals("REC_1001", paraResult[0].recordId)

        // Search by record ID
        val idResult = filterRecords(records, "1002")
        assertEquals(1, idResult.size)
        assertEquals("REC_1002", idResult[0].recordId)

        // Search empty
        val emptyResult = filterRecords(records, "")
        assertEquals(2, emptyResult.size)
    }
}
