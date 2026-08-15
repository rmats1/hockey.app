package com.example.hockey_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.ui.screens.club.CompareClubsScreen
import com.example.hockey_app.ui.screens.club.FavoriteClubsScreen
import com.example.hockey_app.ui.screens.coach.CallUpManagementScreen
import com.example.hockey_app.ui.screens.coach.PhysicalPlanningScreen
import com.example.hockey_app.ui.screens.fixture.CalendarScreen
import com.example.hockey_app.ui.screens.fixture.CommentsScreen
import com.example.hockey_app.ui.screens.fixture.MatchDetailScreen
import com.example.hockey_app.ui.screens.fixture.PredictionsScreen
import com.example.hockey_app.ui.screens.home.HomeScreen
import com.example.hockey_app.ui.screens.login.LoginScreen
import com.example.hockey_app.ui.screens.news.NewsDetailScreen
import com.example.hockey_app.ui.screens.onboarding.OnboardingScreen
import com.example.hockey_app.ui.screens.profile.AyudaScreen
import com.example.hockey_app.ui.screens.profile.ProfileScreen
import com.example.hockey_app.ui.screens.profile.SettingsScreen
import com.example.hockey_app.ui.screens.profile.ShareAppScreen
import com.example.hockey_app.ui.screens.register.RegisterScreen
import com.example.hockey_app.ui.screens.splash.SplashScreen
import com.example.hockey_app.ui.screens.tactical.TacticalBoardScreen
import com.example.hockey_app.ui.screens.team.SearchPlayersScreen
import com.example.hockey_app.ui.screens.torneos.ChartsScreen
import com.example.hockey_app.ui.screens.torneos.EstadisticasScreen
import com.example.hockey_app.ui.screens.torneos.TorneoDetalleMode
import com.example.hockey_app.ui.screens.torneos.TorneoDetalleScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable data object Splash : AppRoute
    @Serializable data object Login : AppRoute
    @Serializable data object Home : AppRoute
    @Serializable data object Register : AppRoute
    @Serializable data object Onboarding : AppRoute
    @Serializable data object Settings : AppRoute
    @Serializable data object CompareClubs : AppRoute
    @Serializable data object FavoriteClubs : AppRoute
    @Serializable data object SearchPlayers : AppRoute
    @Serializable data object PhysicalPlanning : AppRoute
    @Serializable data object CallUpManagement : AppRoute
    @Serializable data object Ayuda : AppRoute
    @Serializable data object ShareApp : AppRoute
    @Serializable data object Estadisticas : AppRoute
    @Serializable data object Charts : AppRoute
    @Serializable data object Predictions : AppRoute
    @Serializable data object Calendar : AppRoute

    @Serializable
    data class TacticalBoard(val clubEscudo: String? = null) : AppRoute

    @Serializable
    data class MatchDetail(val matchId: String) : AppRoute

    @Serializable
    data class Comments(val partidoId: String, val titulo: String) : AppRoute

    @Serializable
    data class TorneoDetalle(val torneo: TorneoResumen, val mode: TorneoDetalleMode) : AppRoute

    @Serializable
    data class NewsDetail(val news: NewsModel) : AppRoute
}

private fun MutableList<AppRoute>.replaceWith(route: AppRoute) {
    clear()
    add(route)
}

private fun MutableList<AppRoute>.pop() {
    if (size > 1) removeAt(lastIndex)
}

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Splash) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.pop() },
        entryProvider = { route ->
            NavEntry(route) { RouteContent(route, backStack) }
        }
    )
}

@Composable
private fun RouteContent(route: AppRoute, backStack: MutableList<AppRoute>) {
        when (route) {
            AppRoute.Splash -> SplashScreen(
                onNavigateToLogin = { backStack.replaceWith(AppRoute.Login) },
                onNavigateToHome = { backStack.replaceWith(AppRoute.Home) }
            )

            AppRoute.Login -> LoginScreen(
                onNavigateToHome = { backStack.replaceWith(AppRoute.Home) },
                onNavigateToRegister = { backStack.add(AppRoute.Register) }
            )

            AppRoute.Register -> RegisterScreen(
                onNavigateBack = { backStack.pop() },
                onNavigateToHome = { backStack.replaceWith(AppRoute.Onboarding) }
            )

            AppRoute.Onboarding -> OnboardingScreen(
                onSkip = { backStack.replaceWith(AppRoute.Home) },
                onFinish = { backStack.replaceWith(AppRoute.Home) }
            )

            AppRoute.Home -> HomeScreen(
                onNavigateToLogin = { backStack.replaceWith(AppRoute.Login) },
                onNavigateToTorneoDetalle = { torneo, mode ->
                    backStack.add(AppRoute.TorneoDetalle(torneo, mode))
                },
                onNavigateToTacticalBoard = { escudo -> backStack.add(AppRoute.TacticalBoard(escudo)) },
                onNavigateToCompareClubs = { backStack.add(AppRoute.CompareClubs) },
                onNavigateToFavoriteClubs = { backStack.add(AppRoute.FavoriteClubs) },
                onNavigateToSearchPlayers = { backStack.add(AppRoute.SearchPlayers) },
                onNavigateToCallUpManagement = { backStack.add(AppRoute.CallUpManagement) },
                onNavigateToPhysicalPlanning = { backStack.add(AppRoute.PhysicalPlanning) },
                onNavigateToSettings = { backStack.add(AppRoute.Settings) },
                onNavigateToNewsDetail = { news -> backStack.add(AppRoute.NewsDetail(news)) }
            )

            is AppRoute.TorneoDetalle -> TorneoDetalleScreen(
                torneo = route.torneo,
                initialMode = route.mode,
                onBack = { backStack.pop() },
                onMatchClick = { matchId -> backStack.add(AppRoute.MatchDetail(matchId)) }
            )

            AppRoute.Settings -> SettingsScreen(onBack = { backStack.pop() })
            AppRoute.FavoriteClubs -> FavoriteClubsScreen(onBack = { backStack.pop() })
            AppRoute.SearchPlayers -> SearchPlayersScreen(onBack = { backStack.pop() })
            is AppRoute.TacticalBoard -> TacticalBoardScreen(
                onBack = { backStack.pop() },
                clubEscudo = route.clubEscudo
            )
            AppRoute.CompareClubs -> CompareClubsScreen(onBack = { backStack.pop() })
            is AppRoute.NewsDetail -> NewsDetailScreen(news = route.news, onBack = { backStack.pop() })
            is AppRoute.MatchDetail -> MatchDetailScreen(matchId = route.matchId, onBack = { backStack.pop() })
            AppRoute.CallUpManagement -> CallUpManagementScreen(onBack = { backStack.pop() })
            AppRoute.PhysicalPlanning -> PhysicalPlanningScreen(onBack = { backStack.pop() })
            AppRoute.Ayuda -> AyudaScreen(onBack = { backStack.pop() })
            AppRoute.ShareApp -> ShareAppScreen(onBack = { backStack.pop() })
            AppRoute.Estadisticas -> EstadisticasScreen(onBack = { backStack.pop() })
            AppRoute.Charts -> ChartsScreen(onBack = { backStack.pop() })
            AppRoute.Predictions -> PredictionsScreen(onBack = { backStack.pop() })
            AppRoute.Calendar -> CalendarScreen(onBack = { backStack.pop() })
            is AppRoute.Comments -> CommentsScreen(
                partidoId = route.partidoId,
                titulo = route.titulo,
                onBack = { backStack.pop() }
            )
        }
}
