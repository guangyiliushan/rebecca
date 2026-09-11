package top.guangyiliushan.rebecca.core.logic

/** 句子分析结果；Unavailable = 能力未就绪（0.1.3 前 / 无分析器的语言包）。 */
sealed interface SentenceAnalysis {
    data object Unavailable : SentenceAnalysis
    // 0.1.3 增加 data class Parsed(...)，0.7.x 接真 NLP
}

interface SentenceAnalyzer {
    fun analyze(text: String): SentenceAnalysis
}

/** Special Case 实现：返回 Unavailable 而非 null/抛错。 */
object DisabledSentenceAnalyzer : SentenceAnalyzer {
    override fun analyze(text: String): SentenceAnalysis = SentenceAnalysis.Unavailable
}
