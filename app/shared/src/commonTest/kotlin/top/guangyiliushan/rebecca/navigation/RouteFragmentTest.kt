package top.guangyiliushan.rebecca.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import top.guangyiliushan.rebecca.core.model.QuizMode

/** 0.1.2 新增路由的 URL 片段往返（R-D4：fragmentName ↔ routeFromFragmentName）。 */
class RouteFragmentTest {
    @Test
    fun wordListDetail_fragmentRoundTrip() {
        val route = Route.WordListDetail("list-basic-verbs")
        assertEquals("lists/list-basic-verbs", route.fragmentName)
        assertEquals(route, routeFromFragmentName("lists/list-basic-verbs"))
    }

    @Test
    fun cardSession_fragmentRoundTrip() {
        val route = Route.CardSession("list-basic-verbs")
        assertEquals("cards/list-basic-verbs", route.fragmentName)
        assertEquals(route, routeFromFragmentName("cards/list-basic-verbs"))
    }

    @Test
    fun listsWithoutId_fallsBackToLibrary() {
        assertEquals(Route.Lists, routeFromFragmentName("lists"))
        assertEquals(Route.Lists, routeFromFragmentName("lists/"))
    }

    @Test
    fun existingRoutes_unchanged() {
        assertEquals(Route.Study, routeFromFragmentName("study"))
        assertEquals(Route.Quiz(QuizMode.MIXED), routeFromFragmentName("quiz/mixed"))
        assertEquals("quiz/learn", Route.Quiz(QuizMode.LEARN).fragmentName)
    }
}
