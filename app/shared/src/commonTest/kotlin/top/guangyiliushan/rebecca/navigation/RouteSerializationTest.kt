package top.guangyiliushan.rebecca.navigation

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RouteSerializationTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun topLevelRoutesRoundTrip() {
        listOf<Route>(Route.Study, Route.Lists, Route.Dictionary, Route.Explore, Route.Settings)
            .forEach { route ->
                val encoded = json.encodeToString(Route.serializer(), route)
                val decoded = json.decodeFromString(Route.serializer(), encoded)
                assertEquals(route, decoded)
            }
    }

    @Test
    fun serialNamesAreReadableUrls() {
        assertTrue(json.encodeToString(Route.serializer(), Route.Study).contains("\"study\""))
    }

    @Test
    fun exactlyFiveTopLevelRoutes() {
        assertEquals(5, topLevelNavItems.size)
    }

    @Test
    fun fragmentNameRoundTrips() {
        listOf(Route.Study, Route.Lists, Route.Dictionary, Route.Explore, Route.Settings, Route.Showcase)
            .forEach { route -> assertEquals(route, routeFromFragmentName(route.fragmentName)) }
    }

    @Test
    fun unknownFragmentFallsBackToStudy() {
        assertEquals(Route.Study, routeFromFragmentName("nonexistent"))
    }
}
