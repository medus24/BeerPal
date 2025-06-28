package com.example.beerpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.beerpal.ui.LibraryScreen
import com.example.beerpal.ui.MainScreen
import com.example.beerpal.viewmodel.BeerPalViewModel
import com.example.beerpal.ui.theme.BeerPalTheme
import com.example.beerpal.viewmodel.BeerPalViewModelFactory
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            BeerPalTheme {
                val context = LocalContext.current
                val viewModel: BeerPalViewModel = viewModel(
                    factory = BeerPalViewModelFactory(context.applicationContext as android.app.Application)
                )

                var showLibrary by remember { mutableStateOf(false) }

                if (showLibrary) {
                    LibraryScreen(
                        context = context,
                        viewModel = viewModel,
                        onResumeList = { showLibrary = false }
                    )
                } else {
                    MainScreen(
                        viewModel = viewModel,
                        onOpenLibrary = {
                            viewModel.saveCurrentList()
                            showLibrary = true
                        },
                        onCloseList = {
                            viewModel.saveCurrentList()
                        }
                    )
                }
            }
        }
    }
}
