package top.guangyiliushan.rebecca.core.logic

import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.SenseMastery

/**
 * 掌握度档（0.1.2）。
 * 阈值是临时政策：真 SRS 0.2.2 落地时随调度一并校准（收敛在 MASTERED_STABILITY_THRESHOLD 单点）。
 */
enum class MasteryGrade { New, Learning, Mastered }

/** Mastered 阈值：stability ≥ 2.0（约 2 天记忆稳定度）。 */
const val MASTERED_STABILITY_THRESHOLD = 2.0

fun masteryGrade(mastery: SenseMastery?): MasteryGrade = when {
    mastery == null || mastery.deletedAt != null -> MasteryGrade.New
    mastery.stability >= MASTERED_STABILITY_THRESHOLD -> MasteryGrade.Mastered
    else -> MasteryGrade.Learning
}

/** 到期判定（dueAt ≤ now 且未被删除）。 */
fun isDue(mastery: SenseMastery?, now: Instant): Boolean =
    mastery != null && mastery.deletedAt == null && mastery.dueAt <= now
