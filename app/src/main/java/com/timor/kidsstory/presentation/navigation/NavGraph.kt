package com.timor.kidsstory.presentation.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
    application: Application
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bookshelf.route
    ) {
        composable(Screen.Bookshelf.route) {
            val viewModel: BookshelfViewModel = viewModel(
                factory = BookshelfViewModel.factory(application)
            )

            // 초기 데이터 로드
            /*LaunchedEffect는 Composable의 생명주기에 따라 코루틴을 실행하는 함수
            Unit을 키로 사용하면 컴포지션이 처음 시작될 때 한 번만 실행됨
            화면이 처음 생성될 때만 stories를 로드하도록*/
            LaunchedEffect(Unit) {
                viewModel.loadStories()
            }

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
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId")
            val viewModel: ReaderViewModel = viewModel(
                factory = ReaderViewModel.factory(application)
            )

            // storyId를 사용하여 해당 스토리 로드
            LaunchedEffect(storyId) {
                storyId?.let { id ->
                    viewModel.loadStory(id)
                }
            }

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