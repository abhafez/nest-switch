package dev.hafez.nestswitch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NestFileRolesTest {

    @Test
    fun `role wins over folder`() {
        assertEquals("DTOs", NestFileRoles.groupFor("dto", "entities"))
    }

    @Test
    fun `specs all land in tests`() {
        assertEquals("Tests", NestFileRoles.groupFor("spec", ""))
        assertEquals("Tests", NestFileRoles.groupFor("controller.spec", ""))
        assertEquals("Tests", NestFileRoles.groupFor("e2e-spec", ""))
    }

    @Test
    fun `plural folder name is singularized to a known role`() {
        assertEquals("Entities", NestFileRoles.groupFor(null, "entities"))
        assertEquals("DTOs", NestFileRoles.groupFor(null, "dto"))
        assertEquals("Repositories", NestFileRoles.groupFor(null, "repositories"))
    }

    @Test
    fun `unknown folder becomes its own capitalized group`() {
        assertEquals("Graphql", NestFileRoles.groupFor(null, "graphql"))
    }

    @Test
    fun `only the first path segment names the group`() {
        assertEquals("DTOs", NestFileRoles.groupFor(null, "dto/nested"))
    }

    @Test
    fun `roleless file at module root is other`() {
        assertEquals("Other", NestFileRoles.groupFor(null, ""))
    }

    @Test
    fun `module sorts before controllers and other sorts last`() {
        assertTrue(NestFileRoles.groupRank("Module") < NestFileRoles.groupRank("Controllers"))
        assertTrue(NestFileRoles.groupRank("Controllers") < NestFileRoles.groupRank("DTOs"))
        assertTrue(NestFileRoles.groupRank("Tests") < NestFileRoles.groupRank("Other"))
        assertTrue(NestFileRoles.groupRank("Graphql") >= NestFileRoles.groupRank("Other"))
    }
}
