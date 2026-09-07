package org.chikitsalipi.extraction

import org.chikitsalipi.model.ExtractedField

interface MedicalExtractor {
    fun extractFields(rawText: String): List<ExtractedField>
}
