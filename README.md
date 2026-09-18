# Nest Switch

JetBrains plugin that wires **all the files of a NestJS module** — controller,
service, module, providers, DTOs, entities, gateways, resolvers, guards, specs,
everything — into **Navigate > Related Symbol** (`Ctrl+Alt+Home`), so you can jump
between them the way the bundled Angular support lets you switch between a
component's files.

Deliberately has **no icons and no editor decorations** — the shortcut is the
whole feature.

## How it groups files (v0.7)

Scope is the **module folder**, not the file name. From the current file the plugin
walks up to the nearest directory holding a `*.module.ts` — that's the module root —
and lists every `.ts`/`.tsx`/`.js`/`.jsx` file under it, sub-folders included:

```
users/
  users.module.ts          <- module root found here
  users.controller.ts
  users.service.ts
  users.controller.spec.ts
  dto/
    create-user.dto.ts     <- listed
    update-user.dto.ts     <- listed
  entities/
    user.entity.ts         <- listed
  providers/
    user-cache.provider.ts <- listed
  admin/
    admin.module.ts        <- own module: this folder is skipped
```

Items are grouped by role into popup sections, ordered: Module, Controllers,
Services, Resolvers, Gateways, Providers, Repositories, Entities, Schemas, DTOs,
Interfaces, … Guards, Strategies, Interceptors, Pipes, Middleware, Filters,
Decorators, … Tests, Other. The role comes from the file name
(`create-user.dto.ts` → `dto`); when the name carries no role, the sub-folder names
the group (`entities/` → Entities, an unknown `graphql/` → Graphql). Files in
sub-folders show as `dto/create-user.dto.ts` so the popup stays unambiguous.

Guards: `node_modules`, `dist`, `build`, `coverage`, `out` are skipped, nested
modules are skipped, and the listing is capped at 300 files.

If the file isn't inside any module folder, the plugin falls back to the old
behaviour — same directory, same base name (`users.controller.ts` ↔
`users.service.ts`).

## Requirements

None beyond the IntelliJ Platform itself. It uses only core platform APIs
(`GotoRelatedProvider`, VFS), so it runs on any JetBrains IDE from 2023.3 on —
Community editions included — with no dependency on the JavaScript/TypeScript
plugin.

## Build & try it

```bash
./gradlew test          # pure-logic unit tests, no IDE fixture needed
./gradlew buildPlugin   # -> build/distributions/nest-switch-<version>.zip
./gradlew runIde        # launches a sandbox IDE with the plugin installed
```

Install the zip manually via *Settings → Plugins → ⚙ → Install Plugin from Disk*
in any JetBrains IDE if you'd rather not use `runIde`.

## Layout

- `src/main/kotlin/dev/hafez/nestswitch/NestModuleScanner.kt` — module-root lookup
  and recursive module-file collection (nested modules excluded).
- `src/main/kotlin/dev/hafez/nestswitch/NestFileRoles.kt` — role/folder → popup
  section label and section ordering.
- `src/main/kotlin/dev/hafez/nestswitch/NestRelatedFilesFinder.kt` — filename
  convention parsing (`<base>.<role>.ts`) and the flat-folder fallback lookup.
- `src/main/kotlin/dev/hafez/nestswitch/NestGotoRelatedProvider.kt` —
  `GotoRelatedProvider` backing `Ctrl+Alt+Home`.
- `src/main/kotlin/dev/hafez/nestswitch/NestProjectDetector.kt` — keeps the plugin
  inert outside projects that actually depend on `@nestjs/*`.
- `src/main/resources/META-INF/plugin.xml` — plugin descriptor.
