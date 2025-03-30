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

/**
 * 앱의 화면 간 네비게이션을 정의하는 클래스
 * - 책장 화면(Bookshelf)
 * - 책 읽기 화면(Reader)
 * - 챗봇 화면(ChatBot)
 * - 설정 화면(Setting)
 */
sealed class Screen(val route: String) {
     // 책장 화면 - 앱의 시작 화면
    data object Bookshelf : Screen("bookshelf")

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
 * - 각 화면 간의 이동 경로와 데이터 전달 정의
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bookshelf.route  // 책장 화면이 앱의 시작점
    ) {
        // 책장 화면
        composable(Screen.Bookshelf.route) {
            BookShelfScreenRoot(
                // 책 선택 시 해당 책의 상세화면으로 이동
                onBookSelect = { index ->
                    navController.navigate(Screen.Reader.createRoute(index)) {
                        launchSingleTop = true  // 중복 화면 생성 방지
                    }
                },
                // 설정 버튼 클릭 시 설정 화면으로 이동
                onSettingClick = {
                    navController.navigate(Screen.Setting.route) {
                        launchSingleTop = true
                    }
                },
                // 챗봇 버튼 클릭 시 챗봇 화면으로 이동
                onChatbotClick = { navController.navigate(Screen.ChatBot.route) },
            )
        }

        // 책 읽기 화면
        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) { backStackEntry ->
            BookScreenRoot(
                onBack = { navController.popBackStack() }  // 뒤로가기 시 이전 화면으로 복귀
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