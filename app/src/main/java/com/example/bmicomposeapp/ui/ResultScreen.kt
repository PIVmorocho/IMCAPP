package com.example.bmicomposeapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Pantalla de resultados del IMC.
 *
 * Recibe el nombre del usuario y el IMC calculado, los muestra con formato
 * y clasifica el resultado usando una expresión `when`. El color del texto
 * de la categoría cambia dinámicamente según el rango obtenido.
 *
 * @param nombre        Nombre del usuario (puede estar URI-decoded automáticamente por Navigation).
 * @param imc           Valor del IMC como Double (convertido desde Float al deserializar la ruta).
 * @param navController Controlador para volver a la pantalla de ingreso con estado limpio.
 * ResulScreen
 */
@Composable
fun ResultScreen(
    nombre:        String,
    imc:           Double,
    navController: NavController
) {
    // Clasificación del IMC: la inferencia de tipos deduce Pair<String, Color> desde el 'when'.
    // No se permite anotar el tipo sobre el patrón de destructuring completo en Kotlin.
    val (categoria, colorCategoria) = when {
        imc < 18.5 -> "Bajo peso"   to Color.Red
        imc < 25.0 -> "Peso normal" to Color(0xFF388E3C)  // verde oscuro
        imc < 30.0 -> "Sobrepeso"   to Color(0xFFE65100)  // naranja oscuro
        else       -> "Obesidad"    to Color.Red
    }

    // ── Layout de resultados ─────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text       = "Resultado",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Saludo personalizado con el nombre recibido por parámetro de navegación
        Text(
            text       = "Hola $nombre, tu resultado es:",
            fontSize   = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Valor del IMC formateado a exactamente un decimal ("%.1f")
        Text(
            text       = "IMC: ${"%.1f".format(imc)}",
            fontSize   = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categoría con color dinámico definido por la expresión 'when' anterior
        Text(
            text       = categoria,
            fontSize   = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color      = colorCategoria
        )

        
    }
}
