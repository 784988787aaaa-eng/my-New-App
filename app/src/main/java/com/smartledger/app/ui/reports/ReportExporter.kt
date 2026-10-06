package com.smartledger.app.ui.reports

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File

object ReportExporter {
    fun sharePdf(context: Context, title: String, lines: List<String>) {
        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(dir, "SmartLedger_" + System.currentTimeMillis() + ".pdf")
        val document = PdfDocument()
        try {
            val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 15f }
            var y = 48f
            paint.textAlign = Paint.Align.RIGHT
            page.canvas.drawText(title, 555f, y, paint)
            y += 36f
            lines.forEach { line ->
                if (y <= 800f) { page.canvas.drawText(line, 555f, y, paint); y += 26f }
            }
            document.finishPage(page)
            file.outputStream().use { document.writeTo(it) }
        } finally { document.close() }
        share(context, file, "application/pdf")
    }

    fun shareExcelCsv(context: Context, rows: List<List<String>>) {
        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(dir, "SmartLedger_" + System.currentTimeMillis() + ".csv")
        file.outputStream().bufferedWriter(Charsets.UTF_8).use { out ->
            out.write("\uFEFF")
            rows.forEach { row ->
                out.write(row.joinToString(",") { csv(it) })
                out.newLine()
            }
        }
        share(context, file, "text/csv")
    }

    private fun csv(value: String): String = "\"" + value.replace("\"", "\"\"") + "\""

    private fun share(context: Context, file: File, type: String) {
        val uri = FileProvider.getUriForFile(context, context.packageName + ".files", file)
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    this.type = type
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                },
                "مشاركة التقرير"
            )
        )
    }
}
