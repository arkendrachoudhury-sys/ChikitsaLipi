package org.chikitsalipi.export

import android.content.Context
import android.util.Log
import org.chikitsalipi.data.GsonProvider
import org.chikitsalipi.model.HealthRecord
import java.io.File

object JsonExporter {
    fun exportRecordToJson(context: Context, record: HealthRecord): File? {
        return try {
            val jsonString = GsonProvider.gson.toJson(record)
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }
            val jsonFile = File(exportDir, "${record.recordId}.json")
            jsonFile.writeText(jsonString)
            jsonFile
        } catch (e: Exception) {
            Log.e("JsonExporter", "Error exporting JSON: ${e.message}", e)
            null
        }
    }
}
