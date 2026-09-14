package top.guangyiliushan.rebecca.core.language

/** 0.3.0 前回退返回 key 本身。 */
private object EmptyStringCatalog : StringCatalog {
    override fun get(key: String, uiLanguage: String): String = key
}

/** 英语包桩：注册全引擎 + 空 catalog；0.7.1 换真内容。 */
object EnglishProfile : LanguageProfile {
    override val languageCode = "en"
    override val engines = EngineFeature.entries.toSet()
    override val stringCatalog: StringCatalog = EmptyStringCatalog

    /** 英语包桩：CEFR 级别理论词汇量口径（0.7.1 换真词表数据）。 */
    override val vocabularyLevels = listOf(
        VocabularyLevel("A1", 500),
        VocabularyLevel("A2", 1500),
        VocabularyLevel("B1", 3000),
        VocabularyLevel("B2", 5000),
    )
}
