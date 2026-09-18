package dev.hafez.nestswitch

import com.intellij.openapi.vfs.VirtualFile

/**
 * Finds every file belonging to the NestJS *module* a file lives in — not just its
 * same-base siblings. The module root is the nearest ancestor directory holding a
 * `*.module.ts`; everything under it is module content, except sub-directories that
 * declare a module of their own (those are separate modules).
 */
object NestModuleScanner {

    private const val MAX_WALK_UP = 12
    private const val MAX_FILES = 300

    private val MODULE_FILE_REGEX = Regex("\\.module\\.(ts|tsx|js|jsx)$")
    private val SOURCE_EXTENSION_REGEX = Regex("\\.(ts|tsx|js|jsx)$")

    private val IGNORED_DIRS = setOf(
        "node_modules", "dist", "build", "coverage", ".git", ".idea", "out"
    )

    fun isModuleFileName(name: String): Boolean = MODULE_FILE_REGEX.containsMatchIn(name)

    fun isSourceFileName(name: String): Boolean = SOURCE_EXTENSION_REGEX.containsMatchIn(name)

    /** Nearest ancestor directory (including the file's own) that declares a `*.module.ts`. */
    fun findModuleRoot(file: VirtualFile): VirtualFile? {
        var dir = file.parent
        var depth = 0
        while (dir != null && depth < MAX_WALK_UP) {
            if (declaresModule(dir)) return dir
            if (dir.findChild("package.json") != null) return null
            dir = dir.parent
            depth++
        }
        return null
    }

    /** All source files inside [root], recursing into sub-folders but not into nested modules. */
    fun collectModuleFiles(root: VirtualFile): List<VirtualFile> {
        val result = mutableListOf<VirtualFile>()
        collect(root, result, isRoot = true)
        return result
    }

    private fun collect(dir: VirtualFile, sink: MutableList<VirtualFile>, isRoot: Boolean) {
        if (sink.size >= MAX_FILES) return
        if (!isRoot && (dir.name in IGNORED_DIRS || declaresModule(dir))) return

        for (child in dir.children) {
            if (sink.size >= MAX_FILES) return
            if (!child.isValid) continue
            if (child.isDirectory) {
                collect(child, sink, isRoot = false)
            } else if (isSourceFileName(child.name)) {
                sink += child
            }
        }
    }

    private fun declaresModule(dir: VirtualFile): Boolean =
        dir.children.any { !it.isDirectory && isModuleFileName(it.name) }
}
