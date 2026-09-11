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
}
