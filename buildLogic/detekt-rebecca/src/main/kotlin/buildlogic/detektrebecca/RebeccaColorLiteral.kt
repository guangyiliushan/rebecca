package buildlogic.detektrebecca

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * F1（frontend-design-system §9.1）：tokens 文件以外禁止 `Color(0x…)` / `Color.White` 等字面量。
 * 白名单：design/tokens 与 design/theme/palettes 目录（palettes 是全仓库唯一允许 Color 字面量的地方）。
 */
class RebeccaColorLiteral(config: Config) :
    Rule(config, description = "Color literals are only allowed inside design/tokens and design/theme/palettes.") {

    override fun visitCallExpression(expression: KtCallExpression) {
        val callee = expression.calleeExpression?.text ?: return
        if (!callee.startsWith("Color(") && callee != "Color") return
        if (isWhitelisted(expression)) return
        report(Finding(Entity.from(expression), "F1: Color 字面量只允许在 design/tokens 与 design/theme/palettes"))
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        // Color.White / Color.Black 等常量访问：receiver=Color，selector=常量名
        if (expression.receiverExpression.text != "Color") return
        val selector = expression.selectorExpression?.text ?: return
        // Transparent / Unspecified 无色彩语义，全库通用，豁免
        if (selector == "Transparent" || selector == "Unspecified") return
        if (isWhitelisted(expression)) return
        report(Finding(Entity.from(expression), "F1: Color.$selector 常量只允许在 design/tokens 与 design/theme/palettes"))
    }

    private fun isWhitelisted(expression: org.jetbrains.kotlin.psi.KtExpression): Boolean {
        val path = expression.containingKtFile.virtualFilePath
        return path.contains("design/tokens/") || path.contains("design/theme/palettes/")
    }
}
