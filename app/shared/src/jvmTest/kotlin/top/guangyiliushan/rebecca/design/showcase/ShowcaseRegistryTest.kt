package top.guangyiliushan.rebecca.design.showcase

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * showcase 门禁（frontend-design-system §9.2-2 / §6.7）：双向比对注册表 ↔ components 文件。
 * 漏注册 / 僵尸条目 / @Preview 计数 <3 即失败。COMPONENTS.md 由注册表生成（单一真源）。
 * 放在 jvmTest（需要文件系统访问）。
 */
class ShowcaseRegistryTest {
    /** 仓库根（jvmTest 的 user.dir 是模块目录，向上找含 settings.gradle.kts 的目录）。 */
    private val repoRoot: File = generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
        .first { File(it, "settings.gradle.kts").exists() }

    private val componentsDir = File(
        repoRoot,
        "app/shared/src/commonMain/kotlin/top/guangyiliushan/rebecca/design/components",
    )

    private val sourceFiles: List<File> =
        componentsDir.listFiles { f -> f.isFile && f.name.endsWith(".kt") && f.name != "AppFieldState.kt" }
            ?.toList() ?: emptyList()

    private val registeredNames = showcaseEntries.map { it.component }.toSet()

    @Test
    fun everyComponentFileIsRegistered() {
        val unregistered = sourceFiles.map { it.name.removeSuffix(".kt") }
            .filter { it !in registeredNames }
        assertEquals(emptyList(), unregistered, "未注册组件（漏注册）")
    }

    @Test
    fun noZombieRegistryEntries() {
        val zombies = registeredNames.filter { name -> sourceFiles.none { it.name == "$name.kt" } }
        assertEquals(emptyList(), zombies, "僵尸条目（注册了但无文件）")
    }

    @Test
    fun everyComponentHasOfficialMultiPreviewAnnotations() {
        // D8：@PreviewLightDark + @PreviewFontScale（两注解 = light/dark/fontScale2.0 三效果）
        val lacking = sourceFiles.filter { file ->
            val text = file.readText()
            !(text.contains("@PreviewLightDark") && text.contains("@PreviewFontScale"))
        }.map { it.name }
        assertEquals(emptyList(), lacking, "缺 @PreviewLightDark 或 @PreviewFontScale（D8）")
    }

    @Test
    fun registryNamesMatchFileNamesExactly() {
        assertTrue(sourceFiles.isNotEmpty(), "components 目录应存在组件文件")
        sourceFiles.forEach { file ->
            val name = file.name.removeSuffix(".kt")
            assertTrue(name in registeredNames, "$name 未在注册表（精确文件名比对）")
        }
    }
}
