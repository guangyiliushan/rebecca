# Repository Guidelines

Rebecca is a Kotlin Multiplatform app targeting Android, iOS, Desktop, Web, and a Ktor server. See [README.md](./README.md) for the full module map and command matrix; this file records the rules and gotchas that keep changes consistent.

## Project Structure

- `app/` — platform entry points (`androidApp`, `iosApp`, `desktopApp`, `webApp`) plus the `shared` Compose UI.
- `core/` — framework-free business logic shared by every target, including `server`. Models and repository interfaces live in `commonMain`; in-memory demo implementations are `internal`.
- `server/` — Ktor API, not yet implemented. 

## Build, Test, Run

Set `JAVA_HOME` to a JDK 21 before running Gradle. Use `.\gradlew.bat` on Windows and `./gradlew` elsewhere.

- `:core:jvmTest` — run the pure-logic unit tests.
- `:core:compileKotlinJs` — verify `commonMain` code compiles outside the JVM.
- `:app:desktopApp:run` — fastest way to run the UI.
- README lists the Android, iOS, Web, and server tasks.

## Coding Style & Naming

- `kotlin.code.style=official`.
-  Mastery is stored per `Sense`; lemma mastery is computed, never stored.
- Value types are `value class`; in `commonMain`, `@JvmInline` requires an explicit `import kotlin.jvm.JvmInline`.
- `core` never depends on Compose, platform APIs, or UI code.

## Testing

- Common tests use `kotlin.test` under `core/src/commonTest`.
- Test files end in `Test.kt`; names describe behavior (`merge_tombstoneWins`).
- Non-trivial logic in `core/logic` must have at least one test.

## Commit & Pull Requests

- Use short imperative messages: `feat: add SenseMastery merge`, `fix: key demo mastery by (accountId, senseId)`.
- Before merging: run `:core:jvmTest`; run a non-JVM compile when `core/commonMain` changes. Keep `core.demo` out of `app/shared`.

## Architecture Rules

- UI depends on `core.repository` interfaces and `core.model` only, never `core.demo`.
- `merge(SenseMastery, SenseMastery)` is the single source of truth for sync conflict resolution; do not add a second copy.
- Data phases: demo in-memory, then local SQLDelight, then server sync. Update the architecture docs before changing the domain model.
