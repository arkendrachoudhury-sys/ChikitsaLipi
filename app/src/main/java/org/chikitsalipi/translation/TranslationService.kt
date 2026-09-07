package org.chikitsalipi.translation

import org.chikitsalipi.model.TranslationPair

interface TranslationService {
    suspend fun translateText(text: String, targetLanguage: String): TranslationPair
}
