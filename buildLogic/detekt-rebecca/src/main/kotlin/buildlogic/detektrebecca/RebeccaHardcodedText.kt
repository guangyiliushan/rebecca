package buildlogic.detektrebecca

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * F6（frontend-design-system §9.1）：用户可见字符串零硬编码。
 * 检测 `Text("...")` 字面量调用（Compose 的 Text 首参）。
 * 豁免：*Test.kt、design/showcase 目录（陈列屏）、含 @Preview 的文件。
 * ⚠ 休眠状态说明见 RebeccaRuleSetProvider。
 */
class RebeccaHardcodedText(config: Config) :
    Rule(config, description = "User-visible strings must come from the catalog (Res.string) or core data.") {

    override fun visitCallExpression(expression: KtCallExpression) {
        val callee = expression.calleeExpression?.text ?: return
        // Text("...") 的 calleeExpression.text == "Text"（不含括号）
        if (callee != "Text") return
        val first = expression.valueArguments.firstOrNull()?.getArgumentExpression() ?: return
        if (first !is KtStringTemplateExpression) return
        if (isExempt(expression)) return
        report(Finding(Entity.from(expression), "F6: Text 硬编码字符串，文案须走 composeResources catalog"))
    }

    private fun isExempt(expression: org.jetbrains.kotlin.psi.KtExpression): Boolean {
        val file = expression.containingKtFile
        val path = file.virtualFilePath
        if (path.endsWith("Test.kt")) return true
        if (path.contains("design/showcase/") || path.contains("feature/showcase/")) return true
        // 含 @Preview 注解的文件整体豁免（preview 文案是示例数据）
        val hasPreview = file.text.contains("@Preview")
        if (hasPreview && path.contains("/design/")) return true
        return false
    }
}
