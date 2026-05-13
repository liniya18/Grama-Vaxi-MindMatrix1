package com.example.gramavaximindmatrix

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfHelper {

    fun generateVaccinationReport(
        context: Context,
        animals: List<AnimalEntity>,
        vaccinations: List<VaccinationEntity>
    ): File? {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        // Page info: A4 size is roughly 595 x 842 points
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var y = 40f

        // Header
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 20f
        titlePaint.color = Color.BLACK
        canvas.drawText(context.getString(R.string.vaccination_report_title), 40f, y, titlePaint)
        
        y += 30f
        paint.textSize = 12f
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        canvas.drawText("Generated on: ${sdf.format(Date())}", 40f, y, paint)

        y += 40f
        
        // Table Header
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Animal", 40f, y, paint)
        canvas.drawText("Vaccine", 150f, y, paint)
        canvas.drawText("Date", 350f, y, paint)
        canvas.drawText("Next Due", 450f, y, paint)

        y += 10f
        canvas.drawLine(40f, y, 555f, y, paint)
        y += 20f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        vaccinations.forEach { vaccination ->
            if (y > 800) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }

            val animal = animals.find { it.id == vaccination.animalId }
            val animalName = animal?.name ?: "Unknown"

            canvas.drawText(animalName, 40f, y, paint)
            canvas.drawText(vaccination.vaccineName, 150f, y, paint)
            canvas.drawText(vaccination.date, 350f, y, paint)
            canvas.drawText(vaccination.nextDueDate, 450f, y, paint)

            y += 20f
        }

        pdfDocument.finishPage(page)

        val directory = File(context.cacheDir, "reports")
        if (!directory.exists()) directory.mkdirs()
        
        val file = File(directory, "Vaccination_Report_${System.currentTimeMillis()}.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
    }
}
