package top.guangyiliushan.rebecca.core.language

/** 语言包私有 UI 串（文案三域之域 B）。0.0.4 空实现，0.3.0 填真 catalog。 */
interface StringCatalog {
    fun get(key: String, uiLanguage: String): String
}

/** 语言包向系统注册：功能入口（engines）+ 内容源/适配器（0.7.x 加 tokenizer/spellAdapter）。 */
interface LanguageProfile {
    val languageCode: String
    val engines: Set<EngineFeature>
    val stringCatalog: StringCatalog
}
