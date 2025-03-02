package com.timor.kidsstory.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.presentation.book.BookScreen
import com.timor.kidsstory.presentation.bookshelf.BookshelfScreen
import com.timor.kidsstory.presentation.bookshelf.BookshelfViewModel
import com.timor.kidsstory.presentation.bookshelf.components.LanguageDialog
import com.timor.kidsstory.presentation.chatbot.ChatbotScreen
import com.timor.kidsstory.presentation.chatbot.ChatbotScreenViewModel
import com.timor.kidsstory.presentation.book.BookViewModel

// 네비게이션 그래프 정의
sealed class Screen(val route: String) {
    data object Bookshelf : Screen("bookshelf")
    data object Reader : Screen("reader/{storyId}") {
        fun createRoute(storyId: String) = "reader/$storyId"
    }

    data object ChatBot : Screen(route = "chatbot")
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
            val viewModel: BookshelfViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            BookshelfScreen(
                state = state,
                onBookSelected = { index ->
                    viewModel.onBookSelected(index)?.let { book ->
                        Log.d("NavGraph", "Navigating to book: ${book.storyId}")
                        navController.navigate(Screen.Reader.createRoute(book.storyId)) {
                            launchSingleTop = true
                        }
                    }
                },
                onMakerClick = { viewModel.onMakerClick() },
                onLanguageClick = { viewModel.showLanguageSelector() },
                onChatbotClick = { navController.navigate(Screen.ChatBot.route) }
            )
            // 언어 선택 다이얼로그 표시
            if (state.showLanguageDialog) {
                LanguageDialog(
                    languages = LanguageConstants.SUPPORTED_LANGUAGES,
                    selectedLanguage = state.currentLanguage,
                    onLanguageSelected = { viewModel.changeLanguage(it) },
                    onDismiss = { viewModel.hideLanguageSelector() }
                )
            }
        }

        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
            Log.d("NavGraph", "Reading storyId from NavArgs: $storyId")

            val viewModel: BookViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            BookScreen(
                state = state,
                onBackToBookshelf = {
                    navController.popBackStack()
                },
                onPageChanged = viewModel::onPageChanged,
                onTextToSpeech = viewModel::ttsSpeak
            )
        }

        // 챗봇 화면
        composable(
            route = Screen.ChatBot.route,
        ) { backStackEntry ->
            val viewModel: ChatbotScreenViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            ChatbotScreen(
                viewModel = viewModel,
                state = state
            )
        }
    }
}