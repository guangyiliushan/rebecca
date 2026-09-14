package top.guangyiliushan.rebecca

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.navigation.Route

/**
 * AppIdentityBar 路由显隐表。
 * 产品决策 2026-09-14（用户裁定）：身份栏**只在首页 Study** 装——其余界面（Lists/Dictionary/
 * Explore/Quiz/Showcase/Settings/词单详情）对身份栏没有配合交互，保留即噪音。
 * 本表取代 ui-design-survey 05 §Route-level visibility 原表（四条顶层学习路由全装）。
 */
class IdentityBarVisibilityTest {
    @Test
    fun onlyStudy_showsIdentityBar() {
        assertTrue(shouldShowIdentityBar(Route.Study))
    }

    @Test
    fun otherRoutes_hideIdentityBar() {
        assertFalse(shouldShowIdentityBar(Route.Lists))
        assertFalse(shouldShowIdentityBar(Route.Dictionary))
        assertFalse(shouldShowIdentityBar(Route.Explore))
        assertFalse(shouldShowIdentityBar(Route.Settings))
        assertFalse(shouldShowIdentityBar(Route.Quiz(QuizMode.MIXED)))
        assertFalse(shouldShowIdentityBar(Route.Showcase))
        assertFalse(shouldShowIdentityBar(null)) // 空栈安全
    }

    @Test
    fun wordListDetailAndCardSession_neverShowIdentityBar() {
        assertFalse(shouldShowIdentityBar(Route.WordListDetail("x")))
        assertFalse(shouldShowIdentityBar(Route.CardSession("x")))
    }
}
