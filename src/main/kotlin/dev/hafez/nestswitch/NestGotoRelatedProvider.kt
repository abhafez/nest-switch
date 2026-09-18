package dev.hafez.nestswitch

import com.intellij.navigation.GotoRelatedItem
import com.intellij.navigation.GotoRelatedProvider
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

/**
 * Powers Navigate > Related Symbol (Ctrl+Alt+Home) for NestJS files — no gutter icon,
 * file-type-agnostic (works even without the bundled JavaScript/TypeScript plugin).
 *
 * Lists every file of the module the current file belongs to — controllers, services,
 * DTOs, entities, providers, guards, specs, whatever sits under the module folder —
 * grouped by role. Falls back to same-directory, same-base siblings when the file
 * isn't inside a module folder.
 */
class NestGotoRelatedProvider : GotoRelatedProvider() {

    override fun getItems(psiElement: PsiElement): MutableList<out GotoRelatedItem> {
        val file = psiElement.containingFile ?: return mutableListOf()
        val vFile = file.virtualFile ?: return mutableListOf()
        if (!NestProjectDetector.isNestProject(vFile)) return mutableListOf()
        if (!NestLicenseGate.isLicensed(file.project)) return mutableListOf()

        val psiManager = PsiManager.getInstance(file.project)
        val moduleRoot = NestModuleScanner.findModuleRoot(vFile)
            ?: return fallbackSiblings(vFile, psiManager)

        val candidates = NestModuleScanner.collectModuleFiles(moduleRoot)
            .filter { it != vFile }
        if (candidates.isEmpty()) return mutableListOf()

        return candidates
            .map { candidate -> Entry(candidate, subFolderOf(candidate, moduleRoot)) }
            .sortedWith(compareBy({ NestFileRoles.groupRank(it.group) }, { it.sortKey }))
            .mapNotNull { entry ->
                psiManager.findFile(entry.file)?.let { psi -> item(psi, entry.label, entry.group) }
            }
            .toMutableList()
    }

    /** Flat-folder mode: the pre-module behaviour, same directory + same base name. */
    private fun fallbackSiblings(vFile: VirtualFile, psiManager: PsiManager): MutableList<out GotoRelatedItem> =
        NestRelatedFilesFinder.findRelatedFiles(vFile)
            .mapNotNull { psiManager.findFile(it) }
            .map { GotoRelatedItem(it, "NestJS") }
            .toMutableList()

    private fun item(psi: PsiFile, label: String, group: String): GotoRelatedItem =
        object : GotoRelatedItem(psi, group) {
            override fun getCustomName(): String = label
        }

    private fun subFolderOf(file: VirtualFile, root: VirtualFile): String {
        val parent = file.parent ?: return ""
        if (parent == root) return ""
        return VfsUtilCore.getRelativePath(parent, root) ?: ""
    }

    private class Entry(val file: VirtualFile, subFolder: String) {
        val group: String = NestFileRoles.groupFor(
            NestRelatedFilesFinder.splitBaseAndRole(file.name)?.second,
            subFolder
        )
        val label: String = if (subFolder.isEmpty()) file.name else "$subFolder/${file.name}"
        val sortKey: String = label
    }
}
