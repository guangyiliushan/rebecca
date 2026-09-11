package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.Lemma
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.repository.ContentDictionaryRepository

internal object DemoContentDictionaryRepository : ContentDictionaryRepository {
    override fun sense(id: SenseId): Sense? = DEMO_SENSES.firstOrNull { it.id == id }

    override fun sensesForLemma(lemmaId: String): List<Sense> =
        DEMO_SENSES.filter { it.lemmaId == lemmaId }

    override fun lemma(lemmaId: String): Lemma? = DEMO_LEMMAS.firstOrNull { it.id == lemmaId }

    override fun search(query: String): List<Sense> {
        val q = query.trim()
        if (q.isEmpty()) return DEMO_SENSES
        val formHits = DEMO_LEMMAS.filter { it.form.contains(q, ignoreCase = true) }.map { it.id }.toSet()
        return DEMO_SENSES.filter {
            it.lemmaId in formHits || it.definition.contains(q, ignoreCase = true)
        }
    }
}
