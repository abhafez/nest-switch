package dev.hafez.nestswitch

import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.ConcurrentHashMap

/**
 * Only decorate files in projects that actually depend on NestJS — checked by walking
 * up from the file to the nearest `package.json` and looking for an "@nestjs/" dependency.
 */
object NestProjectDetector {

    private data class CacheEntry(val stamp: Long, val isNestProject: Boolean)

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    fun isNestProject(file: VirtualFile): Boolean {
        val packageJson = findNearestPackageJson(file.parent) ?: return false

        val cached = cache[packageJson.path]
        if (cached != null && cached.stamp == packageJson.modificationStamp) {
            return cached.isNestProject
        }

        val isNestProject = try {
            VfsUtilCore.loadText(packageJson).contains("\"@nestjs/")
        } catch (e: Exception) {
            false
        }

        cache[packageJson.path] = CacheEntry(packageJson.modificationStamp, isNestProject)
        return isNestProject
    }

    private fun findNearestPackageJson(startDir: VirtualFile?): VirtualFile? {
        var dir = startDir
        var depth = 0
        while (dir != null && depth < 20) {
            dir.findChild("package.json")?.let { return it }
            dir = dir.parent
            depth++
        }
        return null
    }
}
