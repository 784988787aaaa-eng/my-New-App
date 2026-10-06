package com.smartledger.core.backup

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.File

data class BackupManifest(val formatVersion: Int, val createdAt: String, val appVersion: String, val checksum: String)

object BackupNaming {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    fun fileName(now: LocalDateTime = LocalDateTime.now()): String =
        "SmartLedger_" + now.format(formatter) + ".zip"
}

object BackupWriter {
    fun write(output: File, database: File, manifest: String) {
        require(database.exists()) { "Database file not found" }
        ZipOutputStream(output.outputStream().buffered()).use { zip ->
            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(manifest.toByteArray())
            zip.closeEntry()
            zip.putNextEntry(ZipEntry("database.db"))
            database.inputStream().buffered().use { it.copyTo(zip) }
            zip.closeEntry()
        }
    }
}
