package top.guangyiliushan.rebecca.core.language

/** 注册表容器（与 LanguageProfile 契约分离，对齐插件系统 extension point/registry 两层形态）。 */
object LanguageProfileRegistry {
    private val profiles = mutableMapOf<String, LanguageProfile>()

    init {
        register(EnglishProfile)
    }

    fun register(profile: LanguageProfile) {
        profiles[profile.languageCode] = profile
    }

    fun forLanguage(languageCode: String): LanguageProfile? = profiles[languageCode]

    fun default(): LanguageProfile = profiles["en"] ?: EnglishProfile
}
