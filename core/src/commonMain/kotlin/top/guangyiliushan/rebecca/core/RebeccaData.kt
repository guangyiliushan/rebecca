package top.guangyiliushan.rebecca.core

import top.guangyiliushan.rebecca.core.demo.DemoDictionaryRepository
import top.guangyiliushan.rebecca.core.demo.DemoMasteryRepository
import top.guangyiliushan.rebecca.core.demo.DemoStudyRepository
import top.guangyiliushan.rebecca.core.demo.DemoWordListRepository
import top.guangyiliushan.rebecca.core.repository.DictionaryRepository
import top.guangyiliushan.rebecca.core.repository.MasteryRepository
import top.guangyiliushan.rebecca.core.repository.StudyRepository
import top.guangyiliushan.rebecca.core.repository.WordListRepository

/** 阶段一服务定位器：唯一切换数据实现的地方。UI 只依赖接口，禁止 import demo。 */
object RebeccaData {
    val dictionary: DictionaryRepository = DemoDictionaryRepository
    val wordLists: WordListRepository = DemoWordListRepository
    val mastery: MasteryRepository = DemoMasteryRepository
    val study: StudyRepository = DemoStudyRepository
}
