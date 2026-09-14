package top.guangyiliushan.rebecca.core.language

/**
 * 词汇等级档 + 该等级的**理论词汇量**（语言包数据，非用户进度数据）。
 *
 * 口径（0.1.2 登记，0.7.1 语言包填真值）：理论词汇量取 CEFR 各级常用词表规模估计，
 * 用于向学习者说明"这一阶段应掌握多少词"，**不是**用户已学数量的度量。
 */
data class VocabularyLevel(
    val code: String,
    val targetVocabularySize: Int,
)

/**
 * 按已掌握词数匹配当前词汇等级（0.1.2 临时口径）：取首个 target ≥ mastered 的档；超出最高档取最高档。
 *
 * TODO(0.2.x)：等级来源将由"推算"改为"用户声明/分级测试校准"（当前为临时校准，登记于计划与 09 spec）。
 */
fun matchVocabularyLevel(masteredCount: Int, levels: List<VocabularyLevel>): VocabularyLevel? {
    val sorted = levels.sortedBy { it.targetVocabularySize }
    if (sorted.isEmpty()) return null
    return sorted.firstOrNull { masteredCount <= it.targetVocabularySize } ?: sorted.last()
}
