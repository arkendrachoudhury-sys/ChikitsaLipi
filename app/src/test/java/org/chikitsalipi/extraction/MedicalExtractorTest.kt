package org.chikitsalipi.extraction

import org.junit.Assert.*
import org.junit.Test

class MedicalExtractorTest {

    private val extractor = DefaultMedicalExtractor()

    @Test
    fun testExtractBloodPressureAndHb() {
        val sampleOcrText = """
            Patient Record
            BP: 120/80 mmHg
            Hb: 13.5 g/dL
            Pulse: 72 bpm
        """.trimIndent()

        val fields = extractor.extractFields(sampleOcrText)

        assertEquals(3, fields.size)

        val bpField = fields.find { it.fieldName == "Blood Pressure" }
        assertNotNull(bpField)
        assertEquals("120/80", bpField?.extractedValue)
        assertEquals("mmHg", bpField?.unit)

        val hbField = fields.find { it.fieldName == "Hemoglobin" }
        assertNotNull(hbField)
        assertEquals("13.5", hbField?.extractedValue)
        assertEquals("g/dL", hbField?.unit)

        val pulseField = fields.find { it.fieldName == "Pulse" }
        assertNotNull(pulseField)
        assertEquals("72", pulseField?.extractedValue)
        assertEquals("bpm", pulseField?.unit)
    }

    @Test
    fun testEmptyOcrTextReturnsEmptyList() {
        val fields = extractor.extractFields("")
        assertTrue(fields.isEmpty())
    }
}
