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

// 네비게이션 그래프 정의
sealed class Screen(val route: String) {
    data object Bookshelf : Screen("bookshelf")
    data object Reader : Screen("reader/{storyId}") {
        fun createRoute(storyId: String) = "reader/$storyId"
    }

    data object ChatBot : Screen(route = "chatbot")
    data object Setting : Screen("setting")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bookshelf.route
    ) {
        composable(Screen.Bookshelf.route) {
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
            )
        }

        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) { backStackEntry ->
            BookScreenRoot(
                onBack = { navController.popBackStack() }
            )
        }

        // 챗봇 화면
        composable(
            route = Screen.ChatBot.route,
        ) { backStackEntry ->
            ChatbotScreenRoot(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // 설정 화면
        composable(
            route = Screen.Setting.route
        ) { backStackEntry ->
            SettingScreenRoot(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}