package org.chikitsalipi.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
import org.chikitsalipi.model.HealthRecord
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
    fun exportRecordToPdf(context: Context, record: HealthRecord): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 14f

        val titlePaint = Paint()
        titlePaint.color = Color.rgb(15, 82, 87) // ForestTeal
        titlePaint.textSize = 20f
        titlePaint.isFakeBoldText = true

        canvas.drawText("ChikitsaLipi Health Record Report", 40f, 50f, titlePaint)

        paint.textSize = 12f
        canvas.drawText("Record ID: ${record.recordId}", 40f, 90f, paint)
        canvas.drawText("Category: ${record.category.name}", 40f, 110f, paint)
        canvas.drawText("Timestamp: ${record.timestamp}", 40f, 130f, paint)

        titlePaint.textSize = 14f
        canvas.drawText("Raw Recognized Text (OCR):", 40f, 170f, titlePaint)

        var yPos = 195f
        record.rawOcrText.lines().forEach { line ->
            if (yPos < 800f) {
                canvas.drawText(line, 40f, yPos, paint)
                yPos += 18f
            }
        }

        document.finishPage(page)

        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }
            val pdfFile = File(exportDir, "${record.recordId}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()
            pdfFile
        } catch (e: Exception) {
            Log.e("PdfExporter", "Error generating PDF: ${e.message}", e)
            document.close()
            null
        }
    }
}
