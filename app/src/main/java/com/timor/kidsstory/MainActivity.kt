package com.timor.kidsstory.presentation.common

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timor.kidsstory.domain.usecase.ExtractEpubUseCase
import com.timor.kidsstory.presentation.bookshelf.BookshelfViewModel
import com.timor.kidsstory.presentation.reader.ReaderViewModel
import com.timor.kidsstory.ui.theme.KidsStoryTheme

class MainActivity : ComponentActivity() {
    private val bookshelfViewModel: BookshelfViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val extractEpubUseCase = ExtractEpubUseCase(applicationContext)
                return BookshelfViewModel(extractEpubUseCase, application) as T
            }
        }
    }

    private val readerViewModel: ReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 가로 모드 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()

        // 스토리 로드 시작
        bookshelfViewModel.loadStories()

        setContent {
            KidsStoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        bookshelfViewModel = bookshelfViewModel,
                        readerViewModel = readerViewModel
                    )
                }
            }
        }
    }
}