package dev.hafez.nestswitch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NestRelatedFilesFinderTest {

    @Test
    fun `splits simple controller`() {
        assertEquals("users" to "controller", NestRelatedFilesFinder.splitBaseAndRole("users.controller.ts"))
    }

    @Test
    fun `splits service`() {
        assertEquals("users" to "service", NestRelatedFilesFinder.splitBaseAndRole("users.service.ts"))
    }

    @Test
    fun `splits module`() {
        assertEquals("app" to "module", NestRelatedFilesFinder.splitBaseAndRole("app.module.ts"))
    }

    @Test
    fun `prefers dotted role over shorter suffix - controller spec`() {
        assertEquals(
            "users" to "controller.spec",
            NestRelatedFilesFinder.splitBaseAndRole("users.controller.spec.ts")
        )
    }

    @Test
    fun `prefers dotted role over shorter suffix - service spec`() {
        assertEquals(
            "users" to "service.spec",
            NestRelatedFilesFinder.splitBaseAndRole("users.service.spec.ts")
        )
    }

    @Test
    fun `handles hyphenated base names`() {
        assertEquals("create-user" to "dto", NestRelatedFilesFinder.splitBaseAndRole("create-user.dto.ts"))
    }

    @Test
    fun `handles tsx extension`() {
        assertEquals("app" to "module", NestRelatedFilesFinder.splitBaseAndRole("app.module.tsx"))
    }

    @Test
    fun `plain spec without a role still matches`() {
        assertEquals("users" to "spec", NestRelatedFilesFinder.splitBaseAndRole("users.spec.ts"))
    }

    @Test
    fun `no known role returns null`() {
        assertNull(NestRelatedFilesFinder.splitBaseAndRole("index.ts"))
    }

    @Test
    fun `non ts js file returns null`() {
        assertNull(NestRelatedFilesFinder.splitBaseAndRole("users.controller.java"))
    }

    @Test
    fun `role only with no base returns null`() {
        // ".controller.ts" alone shouldn't be treated as an empty-base match.
        assertNull(NestRelatedFilesFinder.splitBaseAndRole("controller.ts"))
    }

    /** The real my-nest-app/src/app folder: all four files share base "app". */
    @Test
    fun `real app folder files all share one base`() {
        val names = listOf(
            "app.controller.spec.ts", "app.controller.ts", "app.module.ts", "app.service.ts"
        )
        val roles = names.map { name ->
            val (base, role) = NestRelatedFilesFinder.splitBaseAndRole(name)!!
            assertEquals("app", base)
            role
        }
        assertEquals(listOf("controller.spec", "controller", "module", "service"), roles)
    }

    @Test
    fun `splits the extra roles added for module-wide navigation`() {
        assertEquals("app" to "config", NestRelatedFilesFinder.splitBaseAndRole("app.config.ts"))
        assertEquals("user" to "mapper", NestRelatedFilesFinder.splitBaseAndRole("user.mapper.ts"))
        assertEquals("order" to "event", NestRelatedFilesFinder.splitBaseAndRole("order.event.ts"))
    }

    @Test
    fun `spec variants of the extra roles keep the dotted role`() {
        assertEquals("user" to "mapper.spec", NestRelatedFilesFinder.splitBaseAndRole("user.mapper.spec.ts"))
    }

    @Test
    fun `e2e spec is not mistaken for a plain spec`() {
        assertEquals("users" to "e2e-spec", NestRelatedFilesFinder.splitBaseAndRole("users.e2e-spec.ts"))
    }
}
