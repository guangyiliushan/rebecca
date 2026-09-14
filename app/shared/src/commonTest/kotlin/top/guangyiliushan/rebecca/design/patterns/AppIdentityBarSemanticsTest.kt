package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * AppIdentityBar 语义（review 🟡5 补缺口：身份条语言对语义短语此前无测试）：
 * 视觉短标签 + 语义完整短语；名字语义全名可覆盖；语言对仅在提供 handler 时可点。
 */
@OptIn(ExperimentalTestApi::class)
class AppIdentityBarSemanticsTest {

    @Test
    fun exposesNameAndLanguagePairSemantics() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppIdentityBar(
                    name = "Local learner",
                    languagePairLabel = "EN · 中文",
                    languagePairSemantics = "Learning English; native language Chinese",
                )
            }
        }
        onNodeWithText("Local learner").assertExists()
        onNodeWithContentDescription("Learning English; native language Chinese").assertExists()
    }

    @Test
    fun nameSemantics_overridesVisualName() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppIdentityBar(
                    name = "Local learner",
                    nameSemantics = "Learner profile, signed out",
                    languagePairLabel = "EN · 中文",
                    languagePairSemantics = "Learning English; native language Chinese",
                )
            }
        }
        onNodeWithContentDescription("Learner profile, signed out").assertExists()
    }

    @Test
    fun languagePair_clickableOnlyWhenHandlerProvided() = runComposeUiTest {
        var clicked = false
        setContent {
            AppTheme(ThemeSettings()) {
                AppIdentityBar(
                    name = "Local learner",
                    languagePairLabel = "EN · 中文",
                    languagePairSemantics = "Learning English; native language Chinese",
                    onLanguageClick = { clicked = true },
                )
            }
        }
        onNodeWithContentDescription("Learning English; native language Chinese").performClick()
        assertTrue(clicked, "提供 handler 时语言对可点")
    }
}
