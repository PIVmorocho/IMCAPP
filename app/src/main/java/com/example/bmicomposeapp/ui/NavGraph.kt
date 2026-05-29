package com.example.bmicomposeapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * Punto central de navegación de la app.
 *
 * Define dos destinos:
 *  - "main"                   → Pantalla de ingreso de datos
 *  - "resultado/{nombre}/{imc}" → Pantalla de resultado con los datos calculados
 *
 * El Scaffold envuelve el NavHost para manejar el padding de las barras del sistema
 * (edge-to-edge activado en MainActivity).
 * NavGraph
 */
@Composable
fun BMINavGraph() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Destino 1: pantalla de ingreso de nombre, peso y altura
            composable("main") {
                MainScreen(navController = navController)
            }

            // Destino 2: pantalla de resultado
            // {nombre} = String con el nombre del usuario (URI-encoded desde MainScreen)
            // {imc}    = Float con el valor del IMC calculado
            composable(
                route = "resultado/{nombre}/{imc}",
                arguments = listOf(
                    navArgument("nombre") { type = NavType.StringType },
                    navArgument("imc")    { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
                val imc    = backStackEntry.arguments?.getFloat("imc")    ?: 0f
                ResultScreen(
                    nombre        = nombre,
                    imc           = imc.toDouble(),
                    navController = navController
                )
            }
        }
    }
}
