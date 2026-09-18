package dev.hafez.nestswitch

/**
 * Turns a file's role (or the sub-folder it sits in) into the section header shown in the
 * Related Symbol popup, plus the order those sections appear in.
 */
object NestFileRoles {

    private val ROLE_LABELS = linkedMapOf(
        "module" to "Module",
        "controller" to "Controllers",
        "service" to "Services",
        "resolver" to "Resolvers",
        "gateway" to "Gateways",
        "provider" to "Providers",
        "repository" to "Repositories",
        "entity" to "Entities",
        "schema" to "Schemas",
        "dto" to "DTOs",
        "interface" to "Interfaces",
        "type" to "Types",
        "enum" to "Enums",
        "constant" to "Constants",
        "guard" to "Guards",
        "strategy" to "Strategies",
        "interceptor" to "Interceptors",
        "pipe" to "Pipes",
        "middleware" to "Middleware",
        "filter" to "Filters",
        "decorator" to "Decorators",
        "config" to "Config",
        "util" to "Utils",
        "helper" to "Utils",
        "mapper" to "Mappers",
        "factory" to "Factories",
        "listener" to "Listeners",
        "subscriber" to "Subscribers",
        "processor" to "Processors",
        "task" to "Tasks",
        "cron" to "Tasks",
        "command" to "Commands",
        "query" to "Queries",
        "event" to "Events",
        "seed" to "Seeds",
        "mock" to "Mocks",
        "stub" to "Mocks"
    )

    private const val TESTS = "Tests"
    private const val OTHER = "Other"

    /** Section order in the popup; anything unlisted lands after these, alphabetically. */
    val GROUP_ORDER: List<String> =
        ROLE_LABELS.values.distinct() + listOf(TESTS, OTHER)

    /**
     * @param role the role parsed off the file name (`users.dto.ts` -> `dto`), or null
     * @param subFolder folder of the file relative to the module root (`dto`, `entities/..`), "" at root
     */
    fun groupFor(role: String?, subFolder: String): String {
        if (role != null) {
            if (role == "spec" || role == "e2e-spec" || role.endsWith(".spec")) return TESTS
            ROLE_LABELS[role]?.let { return it }
        }
        val folder = subFolder.substringBefore('/')
        if (folder.isNotEmpty()) {
            ROLE_LABELS[singularize(folder)]?.let { return it }
            return folder.replaceFirstChar { it.uppercase() }
        }
        return OTHER
    }

    fun groupRank(group: String): Int {
        val index = GROUP_ORDER.indexOf(group)
        return if (index >= 0) index else GROUP_ORDER.size
    }

    private fun singularize(name: String): String = when {
        name.endsWith("ies") && name.length > 3 -> name.dropLast(3) + "y"
        name.endsWith("ses") -> name.dropLast(2)
        name.endsWith("s") && !name.endsWith("ss") -> name.dropLast(1)
        else -> name
    }
}
