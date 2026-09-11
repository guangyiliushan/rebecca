package top.guangyiliushan.rebecca.design.showcase

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * showcase 门禁（frontend-design-system §9.2-2 / §6.7）：双向比对注册表 ↔ components/overlays/scaffold 文件。
 * 漏注册 / 僵尸条目 / 缺官方多预览注解即失败。COMPONENTS.md 由注册表生成（单一真源）。
 * 放在 jvmTest（需要文件系统访问）。
 * 排除：AppFieldState.kt（CompositionLocal 定义，非组件）；AppScaffold.kt（骨架，Phase 4 改造后不进陈列注册表）。
 */
class ShowcaseRegistryTest {
    /** 仓库根（jvmTest 的 user.dir 是模块目录，向上找含 settings.gradle.kts 的目录）。 */
    private val repoRoot: File = generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
        .firstOrNull { File(it, "settings.gradle.kts").exists() }
        ?: error("找不到仓库根（向上未发现 settings.gradle.kts），user.dir=${System.getProperty("user.dir")}")

    private val designRoot = File(
        repoRoot,
        "app/shared/src/commonMain/kotlin/top/guangyiliushan/rebecca/design",
    )

    /** 受门禁的三个组件目录（v0.3 §6.1 目录三分）。 */
    private val componentDirs = listOf("components", "overlays", "scaffold")

    /** 每个目录的排除文件名（非陈列组件）。 */
    private val excluded = mapOf(
        "components" to setOf("AppFieldState.kt"),
        "overlays" to emptySet(),
        "scaffold" to setOf("AppScaffold.kt"),
    )

    private val sourceFiles: List<Pair<String, File>> = componentDirs.flatMap { dir ->
        val f = File(designRoot, dir)
        (f.listFiles { file -> file.isFile && file.name.endsWith(".kt") }?.toList() ?: emptyList())
            .filterNot { it.name in excluded.getValue(dir) }
            .map { dir to it }
    }

    private val registeredNames = showcaseEntries.map { it.component }.toSet()

    @Test
    fun everyComponentFileIsRegistered() {
        val unregistered = sourceFiles.map { (_, f) -> f.name.removeSuffix(".kt") }
            .filter { it !in registeredNames }
        assertEquals(emptyList(), unregistered, "未注册组件（漏注册）")
    }

    @Test
    fun noZombieRegistryEntries() {
        val allNames = sourceFiles.map { (_, f) -> f.name.removeSuffix(".kt") }.toSet()
        val zombies = registeredNames.filter { it !in allNames }
        assertEquals(emptyList(), zombies, "僵尸条目（注册了但无文件）")
    }

    @Test
    fun everyComponentHasOfficialMultiPreviewAnnotations() {
        // D8：@PreviewLightDark + @PreviewFontScale（两注解 = light/dark/fontScale2.0 三效果）
        val lacking = sourceFiles.filter { (_, file) ->
            val text = file.readText()
            !(text.contains("@PreviewLightDark") && text.contains("@PreviewFontScale"))
        }.map { (_, f) -> f.name }
        assertEquals(emptyList(), lacking, "缺 @PreviewLightDark 或 @PreviewFontScale（D8）")
    }

    @Test
    fun registryNamesMatchFileNamesExactly() {
        assertTrue(sourceFiles.isNotEmpty(), "components/overlays/scaffold 目录应存在组件文件")
        sourceFiles.forEach { (_, file) ->
            val name = file.name.removeSuffix(".kt")
            assertTrue(name in registeredNames, "$name 未在注册表（精确文件名比对）")
        }
    }
}
