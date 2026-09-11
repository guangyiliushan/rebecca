package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.ConfusablePair
import top.guangyiliushan.rebecca.core.model.RootAffix
import top.guangyiliushan.rebecca.core.model.WordOfTheDay

/** 内容平面·探索（只读）：WOTD / 易混词 / 词根词缀，内容随语言包变化。 */
interface ContentExploreRepository {
    fun wordOfTheDay(): WordOfTheDay?
    fun confusables(): List<ConfusablePair>
    fun rootsAndAffixes(): List<RootAffix>
}
