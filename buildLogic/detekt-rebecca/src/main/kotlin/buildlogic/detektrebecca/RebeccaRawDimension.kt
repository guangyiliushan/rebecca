package buildlogic.detektrebecca

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

/**
 * F2（frontend-design-system §9.1）：tokens 文件以外禁止魔法 dp/sp。
 * 白名单：`0.dp`、`1.dp`（发丝线）、`2.dp`（spinner 描边，已在组件内注明）；design/tokens 目录。
 */
class RebeccaRawDimension(config: Config) :
    Rule(config, description = "Raw dp/sp literals are only allowed inside design/tokens.") {

    private val allowedValues = setOf("0.dp", "1.dp", "2.dp", "0.sp", "1.sp", "2.sp")

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        val full = expression.text
        val isDimension = full.matches(Regex("""\d+\.(dp|sp)"""))
        if (!isDimension) return
        if (full in allowedValues) return
        val path = expression.containingKtFile.virtualFilePath
        if (path.contains("design/tokens/")) return
        report(Finding(Entity.from(expression), "F2: 魔法 $full 只允许在 design/tokens（白名单 0/1/2 dp/sp）"))
    }
}
