package top.guangyiliushan.rebecca.core

import top.guangyiliushan.rebecca.core.demo.DemoContentDictionaryRepository
import top.guangyiliushan.rebecca.core.demo.DemoContentExploreRepository
import top.guangyiliushan.rebecca.core.demo.DemoContentReadingRepository
import top.guangyiliushan.rebecca.core.demo.DemoExcerptRepository
import top.guangyiliushan.rebecca.core.demo.DemoMasteryRepository
import top.guangyiliushan.rebecca.core.demo.DemoPreferenceRepository
import top.guangyiliushan.rebecca.core.demo.DemoReadingProgressRepository
import top.guangyiliushan.rebecca.core.demo.DemoStudyRepository
import top.guangyiliushan.rebecca.core.demo.DemoWordListRepository
import top.guangyiliushan.rebecca.core.language.LanguageProfileRegistry
import top.guangyiliushan.rebecca.core.repository.ContentDictionaryRepository
import top.guangyiliushan.rebecca.core.repository.ContentExploreRepository
import top.guangyiliushan.rebecca.core.repository.ContentReadingRepository
import top.guangyiliushan.rebecca.core.repository.ExcerptRepository
import top.guangyiliushan.rebecca.core.repository.MasteryRepository
import top.guangyiliushan.rebecca.core.repository.PreferenceRepository
import top.guangyiliushan.rebecca.core.repository.ReadingProgressRepository
import top.guangyiliushan.rebecca.core.repository.StudyRepository
import top.guangyiliushan.rebecca.core.repository.WordListRepository

/** 阶段一服务定位器：唯一切换数据实现的地方。UI 只依赖接口，禁止 import demo。 */
object RebeccaData {
    // 内容平面（只读）
    val contentDictionary: ContentDictionaryRepository = DemoContentDictionaryRepository
    val contentReading: ContentReadingRepository = DemoContentReadingRepository
    val contentExplore: ContentExploreRepository = DemoContentExploreRepository

    // 用户平面（可变）
    val mastery: MasteryRepository = DemoMasteryRepository
    val wordLists: WordListRepository = DemoWordListRepository
    val study: StudyRepository = DemoStudyRepository
    val readingProgress: ReadingProgressRepository = DemoReadingProgressRepository
    val excerpts: ExcerptRepository = DemoExcerptRepository
    val preferences: PreferenceRepository = DemoPreferenceRepository

    // 语言包
    val languages: LanguageProfileRegistry = LanguageProfileRegistry
}
