package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.ConfusablePair
import top.guangyiliushan.rebecca.core.model.RootAffix
import top.guangyiliushan.rebecca.core.model.WordOfTheDay
import top.guangyiliushan.rebecca.core.repository.ContentExploreRepository

internal object DemoContentExploreRepository : ContentExploreRepository {
    override fun wordOfTheDay(): WordOfTheDay? = DEMO_WOTD

    override fun confusables(): List<ConfusablePair> = DEMO_CONFUSABLES

    override fun rootsAndAffixes(): List<RootAffix> = DEMO_ROOTS
}
