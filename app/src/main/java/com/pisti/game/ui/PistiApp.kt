package com.pisti.game.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pisti.game.ui.menu.MainMenuScreen
import com.pisti.game.ui.game.GameScreen

@Composable
fun PistiApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main_menu"
    ) {
        composable("main_menu") {
            MainMenuScreen(
                onStartGame = {
                    navController.navigate("game")
                }
            )
        }
        
        composable("game") {
            GameScreen(
                onGameEnd = {
                    navController.popBackStack()
                }
            )
        }
    }
}