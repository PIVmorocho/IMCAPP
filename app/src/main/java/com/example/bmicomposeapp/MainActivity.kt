package com.example.bmicomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bmicomposeapp.ui.BMINavGraph
import com.example.bmicomposeapp.ui.theme.BMIComposeAppTheme

/**
 * Actividad raíz de la aplicación.
 *
 * Su única responsabilidad es aplicar el tema Material3 y delegar
 * todo el árbol de composables al grafo de navegación [BMINavGraph].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMIComposeAppTheme {
                // Punto de entrada de la UI: carga las pantallas y la lógica de navegación
                BMINavGraph()
            }
        }
    }
}
