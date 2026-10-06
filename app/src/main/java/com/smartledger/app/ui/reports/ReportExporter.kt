package com.smartledger.app.ui.reports

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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
                if (y <= 800f) {
                    page.canvas.drawText(line, 555f, y, paint)
                    y += 26f
                }
            }
            document.finishPage(page)
            file.outputStream().use { document.writeTo(it) }
        } finally {
            document.close()
        }
        share(context, file, "application/pdf")
    }

    fun shareExcel(context: Context, rows: List<List<String>>) {
        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(dir, "SmartLedger_" + System.currentTimeMillis() + ".xlsx")
        ZipOutputStream(file.outputStream().buffered()).use { zip ->
            fun entry(name: String, content: String) {
                zip.putNextEntry(ZipEntry(name))
                zip.write(content.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            entry("[Content_Types].xml", """<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/><Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/></Types>""")
            entry("_rels/.rels", """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/></Relationships>""")
            entry("xl/workbook.xml", """<?xml version="1.0" encoding="UTF-8"?><workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><sheets><sheet name="التقرير" sheetId="1" r:id="rId1"/></sheets></workbook>""")
            entry("xl/_rels/workbook.xml.rels", """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/></Relationships>""")
            val sheet = buildString {
                append("""<?xml version="1.0" encoding="UTF-8"?><worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
                rows.forEachIndexed { ri, row ->
                    append("<row r='").append(ri + 1).append("'>")
                    row.forEachIndexed { ci, value ->
                        val col = ('A'.code + ci).toChar()
                        append("<c r='").append(col).append(ri + 1).append("' t='inlineStr'><is><t>")
                        append(xml(value))
                        append("</t></is></c>")
                    }
                    append("</row>")
                }
                append("</sheetData></worksheet>")
            }
            entry("xl/worksheets/sheet1.xml", sheet)
        }
        share(context, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    }

    private fun xml(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

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
