package buildlogic.detektrebecca

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test

/**
 * 规则单元测试（detekt 2.0：compileContentForTest + rule.lint(ktFile)）。
 * 注意：lint(content) 重载默认 filename="Test.kt" 会触发我们规则的 *Test.kt 豁免——
 * 必须用 compileContentForTest(code, filename) 自定义文件名再 lint(ktFile)。
 */
class RebeccaRulesTest {
    @Test
    fun colorLiteral_isReportedOutsideWhitelist() {
        val findings = RebeccaColorLiteral(Config.empty)
            .lint(compileContentForTest("val bad = Color(0xFF123456)", "Screen.kt"))
        assertThat(findings).hasSize(1)
    }

    @Test
    fun colorConstant_isReported() {
        val findings = RebeccaColorLiteral(Config.empty)
            .lint(compileContentForTest("val bad = Color.White", "Screen.kt"))
        assertThat(findings).hasSize(1)
    }

    @Test
    fun colorTransparent_isExempt() {
        val findings = RebeccaColorLiteral(Config.empty)
            .lint(compileContentForTest("val ok = Color.Transparent", "Screen.kt"))
        assertThat(findings).hasSize(0)
    }

    @Test
    fun rawDimension_isReported() {
        val findings = RebeccaRawDimension(Config.empty)
            .lint(compileContentForTest("val bad = 42.dp", "Screen.kt"))
        assertThat(findings).hasSize(1)
    }

    @Test
    fun rawDimension_whitelistedValuesAllowed() {
        val code = """
            val hairline = 1.dp
            val zero = 0.dp
        """.trimIndent()
        val findings = RebeccaRawDimension(Config.empty).lint(compileContentForTest(code, "Screen.kt"))
        assertThat(findings).hasSize(0)
    }

    @Test
    fun hardcodedText_isReported() {
        val findings = RebeccaHardcodedText(Config.empty)
            .lint(compileContentForTest("fun screen() { Text(\"hardcoded\") }", "Screen.kt"))
        assertThat(findings).hasSize(1)
    }

    @Test
    fun hardcodedText_testFileIsExempt() {
        val findings = RebeccaHardcodedText(Config.empty)
            .lint(compileContentForTest("fun test() { Text(\"hardcoded\") }", "ScreenTest.kt"))
        assertThat(findings).hasSize(0)
    }

    @Test
    fun hardcodedText_probeFileScenario() {
        val code = """
            package top.guangyiliushan.rebecca.design
            import androidx.compose.material3.Text
            @androidx.compose.runtime.Composable
            private fun Probe() { Text("hardcoded english") }
        """.trimIndent()
        val findings = RebeccaHardcodedText(Config.empty)
            .lint(compileContentForTest(code, "DetektProbe.kt"))
        assertThat(findings).hasSize(1)
    }
}
