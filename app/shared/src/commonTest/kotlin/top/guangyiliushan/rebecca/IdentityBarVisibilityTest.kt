package top.guangyiliushan.rebecca

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.navigation.Route

/** AppIdentityBar 路由显隐表（ui-design-survey 05 §Route-level visibility）。 */
class IdentityBarVisibilityTest {
    @Test
    fun topLevelLearningRoutes_showIdentityBar() {
        assertTrue(shouldShowIdentityBar(Route.Study))
        assertTrue(shouldShowIdentityBar(Route.Lists))
        assertTrue(shouldShowIdentityBar(Route.Dictionary))
        assertTrue(shouldShowIdentityBar(Route.Explore))
    }

    @Test
    fun settingsQuizShowcase_hideIdentityBar() {
        assertFalse(shouldShowIdentityBar(Route.Settings))
        assertFalse(shouldShowIdentityBar(Route.Quiz(QuizMode.MIXED)))
        assertFalse(shouldShowIdentityBar(Route.Showcase))
        assertFalse(shouldShowIdentityBar(null)) // 空栈安全
    }
}
