package dev.hafez.nestswitch

import com.intellij.openapi.vfs.VirtualFile

/**
 * Groups NestJS files by convention: `<base>.<role>.ts`, e.g. `users.controller.ts`,
 * `users.service.ts`, `users.module.ts`, `users.controller.spec.ts`. Two files in the
 * same directory are "related" when they share `<base>` but differ in `<role>`.
 */
object NestRelatedFilesFinder {

    private val BASE_ROLES = listOf(
        "controller", "service", "module", "resolver", "gateway", "guard",
        "pipe", "interceptor", "middleware", "provider", "repository",
        "entity", "schema", "dto", "interface", "decorator", "filter",
        "strategy", "type", "enum", "constant", "config", "util", "helper",
        "mapper", "factory", "listener", "subscriber", "processor", "task",
        "cron", "command", "query", "event", "seed", "mock", "stub"
    )

    // Longer (dotted) roles must be tried before their shorter suffix, e.g.
    // "controller.spec" before "spec", otherwise "spec" would win and leave
    // "controller" stuck in the base name.
    private val ROLE_SUFFIXES = (
        BASE_ROLES.map { "$it.spec" } + BASE_ROLES + listOf("spec", "e2e-spec")
    ).sortedByDescending { it.count { c -> c == '.' } }

    private val EXTENSION_REGEX = Regex("\\.(ts|tsx|js|jsx)$")

    /** Returns (base, role) for a file name matching the NestJS convention, or null. */
    fun splitBaseAndRole(fileName: String): Pair<String, String>? {
        if (!EXTENSION_REGEX.containsMatchIn(fileName)) return null
        val withoutExt = fileName.replace(EXTENSION_REGEX, "")

        for (role in ROLE_SUFFIXES) {
            val suffix = ".$role"
            if (withoutExt.endsWith(suffix) && withoutExt.length > suffix.length) {
                return withoutExt.removeSuffix(suffix) to role
            }
        }
        return null
    }

    fun findRelatedFiles(file: VirtualFile): List<VirtualFile> {
        val (base, role) = splitBaseAndRole(file.name) ?: return emptyList()
        val dir = file.parent ?: return emptyList()

        return dir.children
            .filter { it.isValid && !it.isDirectory && it != file }
            .filter { candidate ->
                val (candBase, candRole) = splitBaseAndRole(candidate.name) ?: return@filter false
                candBase == base && candRole != role
            }
            .sortedBy { it.name }
    }
}
