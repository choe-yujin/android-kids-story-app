package com.timor.kidsstory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timor.kidsstory.presentation.book.BookScreenRoot
import com.timor.kidsstory.presentation.bookshelf.BookShelfScreenRoot
import com.timor.kidsstory.presentation.chatbot.ChatbotScreenRoot
import com.timor.kidsstory.presentation.setting.SettingScreenRoot
import com.timor.kidsstory.presentation.languageselection.LanguageSelectionScreenRoot
import com.timor.kidsstory.presentation.leveltest.LevelTestScreenRoot
import com.timor.kidsstory.presentation.splash.SplashScreenRoot

/**
 * 앱의 화면 간 네비게이션을 정의하는 클래스
 */
sealed class Screen(val route: String) {
    // 스플래시 화면
    data object Splash : Screen("splash")
    
    // 언어 선택 화면 - 첫 실행시 표시
    data object LanguageSelection : Screen("language_selection")
    
    // 레벨 테스트 화면 - 언어 선택 후 진행
    data object LevelTest : Screen("level_test/{language}") {
        fun createRoute(language: String) = "level_test/$language"
    }
    
     // 책장 화면 - 메인 화면
    data object Bookshelf : Screen("bookshelf/{language}/{level}?wasSkipped={wasSkipped}&showLevelResultPopup={showLevelResultPopup}") {
        fun createRoute(language: String, level: Int, wasSkipped: Boolean = false, showLevelResultPopup: Boolean = false) =
            "bookshelf/$language/$level?wasSkipped=$wasSkipped&showLevelResultPopup=$showLevelResultPopup"
        // 기본 경로 (호환성 유지)
        val defaultRoute = "bookshelf"
    }

    // 책 읽기 화면 - 특정 책의 상세 페이지 표시
    data object Reader : Screen("reader/{storyId}") {
        // storyId 파라미터를 포함한 경로 생성
        fun createRoute(storyId: String) = "reader/$storyId"
    }

     // 챗봇 화면 - 대화형 도우미
    data object ChatBot : Screen(route = "chatbot")

    // 설정 화면 - 앱 설정 관리
    data object Setting : Screen("setting")
}

/**
 * 앱의 네비게이션 그래프를 구성하는 Composable 함수
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 스플래시 화면
        composable(Screen.Splash.route) {
            SplashScreenRoot(
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToBookshelf = { language, level, wasSkipped, showLevelResultPopup ->
                    navController.navigate(Screen.Bookshelf.createRoute(language, level, wasSkipped, showLevelResultPopup)) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
    
        // 언어 선택 화면
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreenRoot(
                onLevelTestNavigation = { language ->
                    navController.navigate(Screen.LevelTest.createRoute(language)) {
                        launchSingleTop = true
                    }
                },
                onBookshelfNavigation = { language, level ->
                    navController.navigate(Screen.Bookshelf.createRoute(language, level)) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 레벨 테스트 화면
        composable(
            route = Screen.LevelTest.route,
            arguments = listOf(navArgument("language") { type = NavType.StringType })
        ) { backStackEntry ->
            val language = backStackEntry.arguments?.getString("language") ?: "en"
            
            LevelTestScreenRoot(
                language = language,
                onNavigateToBookshelf = { lang, level, wasSkipped, showLevelResultPopup ->
                    navController.navigate(Screen.Bookshelf.createRoute(lang, level, wasSkipped, showLevelResultPopup)) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                }
            )
        }

        // 책장 화면 (기본 경로)
        composable(Screen.Bookshelf.defaultRoute) {
            BookShelfScreenRoot(
                onBookSelect = { index ->
                    navController.navigate(Screen.Reader.createRoute(index)) {
                        launchSingleTop = true
                    }
                },
                onSettingClick = {
                    navController.navigate(Screen.Setting.route) {
                        launchSingleTop = true
                    }
                },
                onChatbotClick = { navController.navigate(Screen.ChatBot.route) },
                onMyPageClick = { /* TODO: Implement MyPage navigation */ },
                initialLanguage = "en", // Default language
                initialLevel = 3,       // Default level
                wasSkipped = false,
                showLevelResultPopup = false
            )
        }
        
        // 책장 화면 (언어/레벨 파라미터 포함)
        composable(
            route = Screen.Bookshelf.route,
            arguments = listOf(
                navArgument("language") { type = NavType.StringType },
                navArgument("level") { type = NavType.IntType },
                navArgument("wasSkipped") { type = NavType.BoolType; defaultValue = false },
                navArgument("showLevelResultPopup") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val language = backStackEntry.arguments?.getString("language") ?: "en"
            val level = backStackEntry.arguments?.getInt("level") ?: 3
            val wasSkipped = backStackEntry.arguments?.getBoolean("wasSkipped") ?: false
            val showLevelResultPopup = backStackEntry.arguments?.getBoolean("showLevelResultPopup") ?: false

            BookShelfScreenRoot(
                onBookSelect = { index ->
                    navController.navigate(Screen.Reader.createRoute(index)) {
                        launchSingleTop = true
                    }
                },
                onSettingClick = {
                    navController.navigate(Screen.Setting.route) {
                        launchSingleTop = true
                    }
                },
                onChatbotClick = { navController.navigate(Screen.ChatBot.route) },
                initialLanguage = language,
                initialLevel = level,
                wasSkipped = wasSkipped,
                showLevelResultPopup = showLevelResultPopup
            )
        }

        // 책 읽기 화면
        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) {
            BookScreenRoot(
                onBack = { navController.popBackStack() }
            )
        }

        // 챗봇 화면
        composable(Screen.ChatBot.route) {
            ChatbotScreenRoot(onBack = { navController.popBackStack() })
        }

        // 설정 화면
        composable(Screen.Setting.route) {
            SettingScreenRoot(onBack = { navController.popBackStack() })
        }
    }
}