package top.guangyiliushan.rebecca.core.model

/** 词形入口（内容）；掌握度不挂在 Lemma 上。 */
data class Lemma(val id: String, val form: String)

/** 一条义项；Sense 级掌握度以此为聚合根。examples 挂 Sense 对齐 OntoLex/TEI/wiktextract；ipa/wordFamily 是 form/entry 级数据在 Sense 上的扁平化承载，0.7.1 Pack 导入时按此铺平、不改模型。 */
data class Sense(
    val id: SenseId,
    val lemmaId: String,
    val definition: String,
    val partOfSpeech: String? = null,
    val ipa: String? = null,
    val examples: List<String> = emptyList(),
    val wordFamily: List<String> = emptyList(),
)

/** 书（内容平面）；paragraphs 为分页最小单位，0.1.3 阅读器用 splitSentences 切句。 */
data class Book(val id: String, val title: String, val author: String? = null, val paragraphs: List<String>)

/** 每日一词（内容平面）；指向 Sense，点词进词详情。 */
data class WordOfTheDay(val senseId: SenseId, val note: String? = null)

/** 易混词对。 */
data class ConfusablePair(val left: SenseId, val right: SenseId, val note: String)

enum class AffixKind { ROOT, PREFIX, SUFFIX }

/** 词根/词缀（内容平面）。 */
data class RootAffix(val form: String, val meaning: String, val kind: AffixKind)
