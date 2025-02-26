package com.timor.kidsstory.presentation.navigation

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
import com.timor.kidsstory.presentation.bookshelf.BookshelfScreen
import com.timor.kidsstory.presentation.bookshelf.BookshelfViewModel
import com.timor.kidsstory.presentation.reader.ReaderViewModel
import com.timor.kidsstory.presentation.reader.StoryDetailScreen

// 네비게이션 그래프 정의

sealed class Screen(val route: String) {
    data object Bookshelf : Screen("bookshelf")
    data object Reader : Screen("reader/{storyId}") {
        fun createRoute(storyId: String) = "reader/$storyId"
    }
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
                    viewModel.onBookSelected(index)?.let { story ->
                        navController.navigate(Screen.Reader.createRoute(story.storyId)) {
                            launchSingleTop = true // 동일한 화면이 백스택에 중복으로 쌓이는 것을 방지
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) {
            val viewModel: ReaderViewModel = hiltViewModel()

            val state by viewModel.state.collectAsState()

            StoryDetailScreen(
                state = state,
                onBackToBookshelf = {
                    navController.popBackStack()
                },
                onPageChanged = viewModel::onPageChanged
            )
        }
    }
}