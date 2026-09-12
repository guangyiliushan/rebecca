package top.guangyiliushan.rebecca.core.logic

import kotlin.random.Random
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.core.model.Sense

/** 一题：题干来自 target（definition），四选项为 lemma 形态（UI 层把 lemmaId 换 form）。 */
data class QuizQuestion(
    val target: Sense,
    val optionLemmaIds: List<String>,
    val correctIndex: Int,
)

/**
 * 构建一题：target + (optionCount-1) 个干扰项。
 * 干扰项排除与 target 同 lemmaId 的 Sense（同词形多义项会造成重复选项）；
 * pool 中可用干扰不足时返回 null（调用方结束会话）。
 */
fun buildQuestion(
    target: Sense,
    pool: List<Sense>,
    optionCount: Int = 4,
    rng: Random = Random.Default,
): QuizQuestion? {
    val distractors = pool.filter { it.lemmaId != target.lemmaId }.toMutableList()
    if (distractors.size < optionCount - 1) return null
    distractors.shuffle(rng)
    val chosen = distractors.take(optionCount - 1)
    val optionIds = (listOf(target) + chosen).map { it.lemmaId }.shuffled(rng)
    return QuizQuestion(target, optionIds, optionIds.indexOf(target.lemmaId))
}

fun isCorrect(question: QuizQuestion, chosenIndex: Int): Boolean = chosenIndex == question.correctIndex

/** 三模式候选筛选（Q6 裁定语义）。MIXED = 新词:到期 1:1 交错，rng 只决定组内顺序。 */
fun selectCandidates(
    mode: QuizMode,
    new: List<Sense>,
    due: List<Sense>,
    limit: Int,
    rng: Random,
): List<Sense> = when (mode) {
    QuizMode.LEARN -> new.shuffled(rng).take(limit)
    QuizMode.REVIEW -> due.shuffled(rng).take(limit)
    QuizMode.MIXED -> {
        val n = new.shuffled(rng)
        val d = due.shuffled(rng)
        val per = minOf(n.size, d.size, (limit + 1) / 2)
        buildList {
            repeat(per) { i -> add(n[i]); add(d[i]) }
        }.take(limit)
    }
}
