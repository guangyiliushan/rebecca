package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.Lemma
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId

/** 内容平面·词典（只读）。0.0.4 demo 内联，0.7.1 换 Pack。 */
interface ContentDictionaryRepository {
    fun sense(id: SenseId): Sense?
    fun sensesForLemma(lemmaId: String): List<Sense>
    fun lemma(lemmaId: String): Lemma?
    fun search(query: String): List<Sense>
}
