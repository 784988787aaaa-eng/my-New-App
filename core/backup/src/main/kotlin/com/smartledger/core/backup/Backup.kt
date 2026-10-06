package com.smartledger.core.backup

import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

data class BackupManifest(val formatVersion: Int, val createdAt: String, val appVersion: String, val checksum: String)

object BackupNaming {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    fun fileName(now: LocalDateTime = LocalDateTime.now()): String = "SmartLedger_" + now.format(formatter) + ".zip"
}

object BackupIntegrity {
    fun sha256(file: File): String {
        require(file.exists()) { "File not found" }
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(16 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun validateArchive(archive: File): Boolean = runCatching {
        require(archive.exists())
        ZipFile(archive).use { zip ->
            val manifest = zip.getEntry("manifest.json") ?: return false
            val database = zip.getEntry("database.db") ?: return false
            manifest.size > 0 && database.size > 0
        }
    }.getOrDefault(false)
}

object BackupWriter {
    fun write(output: File, database: File, manifest: String) {
        require(database.exists()) { "Database file not found" }
        output.parentFile?.mkdirs()
        ZipOutputStream(output.outputStream().buffered()).use { zip ->
            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(manifest.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            zip.putNextEntry(ZipEntry("database.db"))
            database.inputStream().buffered().use { it.copyTo(zip) }
            zip.closeEntry()
        }
        require(BackupIntegrity.validateArchive(output)) { "Backup archive validation failed" }
    }
}

object BackupRestore {
    fun extractDatabase(archive: File, target: File): File {
        require(BackupIntegrity.validateArchive(archive)) { "Invalid backup archive" }
        val temp = File(target.parentFile, target.name + ".restore.tmp")
        temp.delete()
        ZipFile(archive).use { zip ->
            val entry = zip.getEntry("database.db") ?: error("Database entry missing")
            zip.getInputStream(entry).use { input -> temp.outputStream().buffered().use { input.copyTo(it) } }
        }
        require(temp.length() > 0L) { "Restored database is empty" }
        return temp
    }

    fun replaceDatabase(restored: File, target: File) {
        require(restored.exists() && restored.length() > 0L) { "Restored database is invalid" }
        val parent = target.parentFile ?: error("Database directory missing")
        File(target.absolutePath + "-wal").delete()
        File(target.absolutePath + "-shm").delete()
        val staged = File(parent, target.name + ".staged")
        restored.copyTo(staged, overwrite = true)
        require(staged.renameTo(target) || run {
            staged.copyTo(target, overwrite = true)
            staged.delete()
            true
        })
        restored.delete()
    }
}
