package com.example.bmicomposeapp.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Pantalla de ingreso de datos del usuario.
 *
 * Muestra tres campos (nombre, peso, altura) y un botón "Calcular".
 * Antes de navegar valida que peso y altura sean números positivos;
 * si no lo son, muestra un mensaje de error en rojo.
 *
 * @param navController Controlador usado para navegar hacia [ResultScreen].
 */
@Composable
fun MainScreen(navController: NavController) {

    // ── Estado de los campos del formulario ──────────────────────────────────
    var nombre by remember { mutableStateOf("") }
    var peso   by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

    // Controla la visibilidad del texto de error de validación
    var mostrarError by remember { mutableStateOf(false) }

    // ── Layout principal ─────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text       = "Calculadora de IMC",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo: nombre del usuario (texto libre)
        OutlinedTextField(
            value         = nombre,
            onValueChange = { nombre = it },
            label         = { Text("Nombre") },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: peso en kilogramos — teclado decimal para facilitar la entrada
        OutlinedTextField(
            value         = peso,
            onValueChange = { peso = it },
            label         = { Text("Peso (kg)") },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: altura en metros — teclado decimal
        OutlinedTextField(
            value         = altura,
            onValueChange = { altura = it },
            label         = { Text("Altura (m)") },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mensaje de error: solo visible cuando la validación ha fallado
        if (mostrarError) {
            Text(
                text     = "Por favor, ingresa valores válidos",
                color    = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón "Calcular": valida los datos y navega si son correctos
        Button(
            onClick = {
                // toDoubleOrNull previene NumberFormatException con cadenas vacías o no numéricas
                val pesoDouble   = peso.trim().toDoubleOrNull()
                val alturaDouble = altura.trim().toDoubleOrNull()

                val datosValidos = pesoDouble   != null && pesoDouble   > 0.0 &&
                                   alturaDouble != null && alturaDouble > 0.0

                if (datosValidos) {
                    mostrarError = false

                    // Fórmula IMC: peso / altura²
                    val imc = pesoDouble!! / (alturaDouble!! * alturaDouble)

                    // URI.encode garantiza que espacios y caracteres especiales no rompan la ruta
                    val encodedNombre = Uri.encode(nombre.ifBlank { "Usuario" })

                    navController.navigate("resultado/$encodedNombre/${imc.toFloat()}")
                } else {
                    mostrarError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Calcular", fontSize = 16.sp)
        }
    }
}
