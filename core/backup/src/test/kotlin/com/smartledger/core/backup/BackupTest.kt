package com.smartledger.core.backup

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.nio.file.Files

class BackupTest {
    @Test fun fileNameIsStableAndCommerciallyNamed() {
        assertEquals("SmartLedger_2026-10-06_14-05-09.zip", BackupNaming.fileName(LocalDateTime.of(2026,10,6,14,5,9)))
    }
}
