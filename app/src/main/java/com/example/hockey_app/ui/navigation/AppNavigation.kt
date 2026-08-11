package com.example.hockey_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hockey_app.ui.screens.home.HomeScreen
import com.example.hockey_app.ui.screens.login.LoginScreen
import com.example.hockey_app.ui.screens.onboarding.OnboardingScreen
import com.example.hockey_app.ui.screens.register.RegisterScreen
import com.example.hockey_app.ui.screens.splash.SplashScreen
import com.example.hockey_app.ui.screens.torneos.TorneoDetalleMode
import com.example.hockey_app.ui.screens.torneos.TorneoDetalleScreen
import com.example.hockey_app.data.models.TorneoResumen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.hockey_app.ui.screens.club.CompareClubsScreen
import com.example.hockey_app.ui.screens.club.FavoriteClubsScreen
import com.example.hockey_app.ui.screens.tactical.TacticalBoardScreen
import com.example.hockey_app.ui.screens.news.NewsDetailScreen
import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.ui.screens.fixture.MatchDetailScreen
import com.example.hockey_app.ui.screens.coach.PhysicalPlanningScreen
import com.example.hockey_app.ui.screens.coach.CallUpManagementScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    object TacticalBoard : Screen("tactical_board?clubEscudo={clubEscudo}") {
        fun createRoute(clubEscudo: String?) = "tactical_board?clubEscudo=${if (clubEscudo.isNullOrEmpty()) "none" else clubEscudo}"
    }
    object Settings : Screen("settings")
    object CompareClubs : Screen("compare_clubs")
    object FavoriteClubs : Screen("favorite_clubs")
    object SearchPlayers : Screen("search_players")
    object NewsDetail : Screen("news_detail")
    object PhysicalPlanning : Screen("physical_planning")
    object MatchDetail : Screen("match_detail/{matchId}") {
        fun createRoute(matchId: String) = "match_detail/$matchId"
    }
    object CallUpManagement : Screen("call_up_management")
    object TorneoDetalle : Screen("torneo_detalle/{id}/{nombre}/{rama}/{categoria}/{division}/{temporada}/{mode}") {
        fun createRoute(t: TorneoResumen, mode: TorneoDetalleMode) = 
            "torneo_detalle/${t.id}/${t.nombre}/${t.rama}/${t.categoria}/${if(t.division.isEmpty()) "none" else t.division}/${t.temporada}/${mode.name}"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToTorneoDetalle = { torneo, mode ->
                    navController.navigate(Screen.TorneoDetalle.createRoute(torneo, mode))
                },
                onNavigateToTacticalBoard = { escudo ->
                    navController.navigate(Screen.TacticalBoard.createRoute(escudo))
                },
                onNavigateToCompareClubs = {
                    navController.navigate(Screen.CompareClubs.route)
                },
                onNavigateToFavoriteClubs = {
                    navController.navigate(Screen.FavoriteClubs.route)
                },
                onNavigateToSearchPlayers = {
                    navController.navigate(Screen.SearchPlayers.route)
                },
                onNavigateToCallUpManagement = {
                    navController.navigate(Screen.CallUpManagement.route)
                },
                onNavigateToPhysicalPlanning = {
                    navController.navigate(Screen.PhysicalPlanning.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToNewsDetail = { news ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("news", news)
                    navController.navigate(Screen.NewsDetail.route)
                }
            )
        }
        composable(
            route = Screen.TorneoDetalle.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rama") { type = NavType.StringType },
                navArgument("categoria") { type = NavType.StringType },
                navArgument("division") { type = NavType.StringType },
                navArgument("temporada") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val rama = backStackEntry.arguments?.getString("rama") ?: ""
            val categoria = backStackEntry.arguments?.getString("categoria") ?: ""
            val division = backStackEntry.arguments?.getString("division").let { if (it == "none") "" else it } ?: ""
            val temporada = backStackEntry.arguments?.getString("temporada") ?: ""
            val mode = TorneoDetalleMode.valueOf(backStackEntry.arguments?.getString("mode") ?: "FIXTURE")

            TorneoDetalleScreen(
                torneo = TorneoResumen(id, nombre, rama, categoria, division, temporada),
                initialMode = mode,
                onBack = { navController.popBackStack() },
                onMatchClick = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            com.example.hockey_app.ui.screens.profile.SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FavoriteClubs.route) {
            FavoriteClubsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SearchPlayers.route) {
            com.example.hockey_app.ui.screens.team.SearchPlayersScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.TacticalBoard.route,
            arguments = listOf(navArgument("clubEscudo") { defaultValue = "none" })
        ) { backStackEntry ->
            val escudo = backStackEntry.arguments?.getString("clubEscudo").let { if (it == "none") null else it }
            TacticalBoardScreen(
                onBack = { navController.popBackStack() },
                clubEscudo = escudo
            )
        }
        composable(Screen.CompareClubs.route) {
            CompareClubsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.NewsDetail.route) {
            val news = navController.previousBackStackEntry?.savedStateHandle?.get<NewsModel>("news")
            if (news != null) {
                NewsDetailScreen(
                    news = news,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.MatchDetail.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                matchId = matchId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CallUpManagement.route) {
            CallUpManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PhysicalPlanning.route) {
            PhysicalPlanningScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
