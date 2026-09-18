package dev.hafez.nestswitch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NestModuleScannerTest {

    @Test
    fun `recognizes module files`() {
        assertTrue(NestModuleScanner.isModuleFileName("users.module.ts"))
        assertTrue(NestModuleScanner.isModuleFileName("app.module.js"))
        assertFalse(NestModuleScanner.isModuleFileName("users.service.ts"))
        assertFalse(NestModuleScanner.isModuleFileName("users.module.spec.ts"))
    }

    @Test
    fun `recognizes source files`() {
        assertTrue(NestModuleScanner.isSourceFileName("create-user.dto.ts"))
        assertTrue(NestModuleScanner.isSourceFileName("index.ts"))
        assertFalse(NestModuleScanner.isSourceFileName("README.md"))
    }
}
