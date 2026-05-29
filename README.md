# Calculadora de IMC — Guía Explicativa del Proyecto

**Tecnología:** Android · Kotlin · Jetpack Compose · Navigation Compose  
**Propósito:** Demostración de estados, validación de entradas y navegación con múltiples parámetros.

---

## Tabla de contenidos

1. [¿Qué hace la app?](#1-qué-hace-la-app)
2. [Arquitectura y estructura de archivos](#2-arquitectura-y-estructura-de-archivos)
3. [Requisito 1 — Composables y Layout](#3-requisito-1--composables-y-layout)
4. [Requisito 2 — Estados y Validación (Pantalla 1)](#4-requisito-2--estados-y-validación-pantalla-1)
5. [Requisito 3 — Navegación y Pantalla de Resultados](#5-requisito-3--navegación-y-pantalla-de-resultados)
6. [Reto 1 — Validación que evita el crash](#6-reto-1--validación-que-evita-el-crash)
7. [Reto 2 — Cómo se pasan los dos parámetros en la ruta](#7-reto-2--cómo-se-pasan-los-dos-parámetros-en-la-ruta)
8. [Reto 3 — Interfaz dinámica con colores por categoría](#8-reto-3--interfaz-dinámica-con-colores-por-categoría)
9. [Cómo se evita que la app crashee](#9-cómo-se-evita-que-la-app-crashee)
10. [Flujo completo paso a paso](#10-flujo-completo-paso-a-paso)

---

## 1. ¿Qué hace la app?

La aplicación calcula el **Índice de Masa Corporal (IMC)** de un usuario.

| Paso | Acción |
|------|--------|
| 1 | El usuario ingresa su nombre, peso (kg) y altura (m) |
| 2 | Presiona **"Calcular"** |
| 3 | La app valida los datos y calcula el IMC |
| 4 | Navega a una segunda pantalla mostrando el resultado, la categoría y un color indicador |
| 5 | El usuario puede volver y los campos quedan en blanco listos para un nuevo cálculo |

**Fórmula:** `IMC = Peso / (Altura × Altura)`

---

## 2. Arquitectura y estructura de archivos

```
BMIComposeApp/
└── app/src/main/java/com/example/bmicomposeapp/
    ├── MainActivity.kt      ← Punto de entrada: aplica tema y carga el grafo de navegación
    └── ui/
        ├── NavGraph.kt      ← Define todas las rutas (NavHost)
        ├── MainScreen.kt    ← Pantalla 1: formulario de ingreso + validación
        └── ResultScreen.kt  ← Pantalla 2: resultado del IMC con color dinámico
```

**Patrón arquitectónico:** Single Activity + State Hoisting local (sin ViewModel).  
El estado de los campos vive en `remember { mutableStateOf("") }` dentro de `MainScreen`.

---

## 3. Requisito 1 — Composables y Layout

Todas las pantallas usan **`Column`** como contenedor principal para apilar elementos verticalmente.

```kotlin
// MainScreen.kt — layout principal
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),               // padding uniforme en todos los bordes
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text("Calculadora de IMC", ...)

    Spacer(modifier = Modifier.height(32.dp))  // margen entre título y primer campo

    OutlinedTextField(...)   // campo Nombre
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(...)   // campo Peso
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(...)   // campo Altura
    Spacer(modifier = Modifier.height(8.dp))

    // (mensaje de error condicional)

    Spacer(modifier = Modifier.height(24.dp))
    Button(...)              // botón Calcular
}
```

**Cumplimiento de requisitos de layout:**

| Requisito | Cómo se cumple |
|-----------|----------------|
| `Column` y `Row` para organizar la UI | `Column` en ambas pantallas; alineación centrada |
| `Modifier` para padding, tamaño y centrado | `padding(24.dp)`, `fillMaxSize()`, `fillMaxWidth()`, `height(50.dp)` |
| `Spacer` para márgenes entre elementos | `Spacer(Modifier.height(...))` entre cada sección |

---

## 4. Requisito 2 — Estados y Validación (Pantalla 1)

### 4.1 Los tres `TextField` con estado

Cada campo tiene su propio estado con `mutableStateOf` y delegación `by`:

```kotlin
// MainScreen.kt:42-44
var nombre by remember { mutableStateOf("") }
var peso   by remember { mutableStateOf("") }
var altura by remember { mutableStateOf("") }
```

- `remember` hace que el valor **persista a través de recomposiciones**.
- La delegación `by` permite leer/escribir el valor directamente (`nombre` en vez de `nombre.value`).
- Cada `OutlinedTextField` conecta su `value` y `onValueChange` al estado correspondiente:

```kotlin
OutlinedTextField(
    value         = peso,
    onValueChange = { peso = it },   // 'it' es el nuevo texto ingresado
    label         = { Text("Peso (kg)") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
)
```

Los campos de peso y altura usan `KeyboardType.Decimal` para mostrar teclado numérico en el dispositivo.

### 4.2 Estado adicional para el error

```kotlin
// MainScreen.kt:47
var mostrarError by remember { mutableStateOf(false) }
```

Este booleano controla si el mensaje de error rojo es visible o no.

---

## 5. Requisito 3 — Navegación y Pantalla de Resultados

### 5.1 NavHost con `rememberNavController()`

```kotlin
// NavGraph.kt:26
val navController = rememberNavController()

NavHost(
    navController    = navController,
    startDestination = "main"
) {
    composable("main") { MainScreen(navController) }

    composable(
        route     = "resultado/{nombre}/{imc}",
        arguments = listOf(
            navArgument("nombre") { type = NavType.StringType },
            navArgument("imc")    { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
        val imc    = backStackEntry.arguments?.getFloat("imc")    ?: 0f
        ResultScreen(nombre, imc.toDouble(), navController)
    }
}
```

### 5.2 Saludo personalizado

```kotlin
// ResultScreen.kt:65-68
Text(text = "Hola $nombre, tu resultado es:")
```

### 5.3 IMC con un decimal

```kotlin
// ResultScreen.kt:74-78
Text(text = "IMC: ${"%.1f".format(imc)}")
// Ejemplo: "IMC: 22.4"
```

### 5.4 Clasificación con `when`

```kotlin
// ResultScreen.kt:40-45
val (categoria, colorCategoria) = when {
    imc < 18.5 -> "Bajo peso"   to Color.Red
    imc < 25.0 -> "Peso normal" to Color(0xFF388E3C)  // verde oscuro
    imc < 30.0 -> "Sobrepeso"   to Color(0xFFE65100)  // naranja oscuro
    else       -> "Obesidad"    to Color.Red
}
```

La expresión `when` retorna un `Pair<String, Color>` que se destructura en dos variables en una sola línea.

---

## 6. Reto 1 — Validación que evita el crash

### El problema sin validación

Si el usuario deja los campos vacíos y presiona "Calcular", una conversión directa como `peso.toDouble()` lanzaría una `NumberFormatException` y la app **crashearía**.

### La solución: `toDoubleOrNull()`

```kotlin
// MainScreen.kt:116-120
val pesoDouble   = peso.trim().toDoubleOrNull()
val alturaDouble = altura.trim().toDoubleOrNull()

val datosValidos = pesoDouble   != null && pesoDouble   > 0.0 &&
                   alturaDouble != null && alturaDouble > 0.0
```

- `toDoubleOrNull()` retorna `null` si la cadena es vacía, contiene letras o no es un número válido. **Nunca lanza excepción.**
- `.trim()` elimina espacios al inicio y al final antes de intentar la conversión.
- La condición verifica que el valor sea **mayor que cero** (rechaza `-5` o `0`).

### Resultado del botón según la validación

```kotlin
if (datosValidos) {
    mostrarError = false
    // calcular IMC y navegar
} else {
    mostrarError = true   // activa el mensaje rojo
}
```

```kotlin
// El mensaje de error solo aparece cuando mostrarError == true
if (mostrarError) {
    Text(
        text  = "Por favor, ingresa valores válidos",
        color = Color.Red
    )
}
```

---

## 7. Reto 2 — Cómo se pasan los dos parámetros en la ruta

### Paso 1: definir la ruta con placeholders en `NavGraph.kt`

```kotlin
// NavGraph.kt:43-47
composable(
    route = "resultado/{nombre}/{imc}",   // dos placeholders: {nombre} e {imc}
    arguments = listOf(
        navArgument("nombre") { type = NavType.StringType },
        navArgument("imc")    { type = NavType.FloatType }
    )
)
```

Los `{}` en la ruta son **argumentos de ruta** (path arguments). Navigation Compose los parsea automáticamente según el `NavType` declarado.

### Paso 2: construir la URL de navegación en `MainScreen.kt`

```kotlin
// MainScreen.kt:125-131
val imc = pesoDouble!! / (alturaDouble!! * alturaDouble)

val encodedNombre = Uri.encode(nombre.ifBlank { "Usuario" })
//  ↑ convierte "Juan Pérez" → "Juan%20P%C3%A9rez"
//    para que espacios y tildes no rompan la ruta de navegación

navController.navigate("resultado/$encodedNombre/${imc.toFloat()}")
//  Ejemplo real: "resultado/Juan%20P%C3%A9rez/22.350124"
```

**¿Por qué `toFloat()`?**  
El `NavType.FloatType` espera un `Float`. El IMC se calcula como `Double` para mayor precisión, pero se convierte a `Float` solo al armar la URL. En `ResultScreen` se convierte de vuelta a `Double` con `.toDouble()`.

### Paso 3: leer los parámetros en `NavGraph.kt`

```kotlin
// NavGraph.kt:49-54
} { backStackEntry ->
    val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
    val imc    = backStackEntry.arguments?.getFloat("imc")    ?: 0f
    ResultScreen(
        nombre        = nombre,
        imc           = imc.toDouble(),
        navController = navController
    )
}
```

Navigation Compose decodifica el URI automáticamente, por lo que `nombre` llega a `ResultScreen` ya como texto legible (`"Juan Pérez"`).

### Resumen visual del flujo de parámetros

```
MainScreen                    NavGraph                   ResultScreen
──────────────────────────────────────────────────────────────────────
imc (Double) ──toFloat()──► URL: "resultado/{nombre}/{imc}"
nombre ──Uri.encode()──►        │
                                │ Navigation Compose parsea
                                ▼
                        backStackEntry.arguments
                                │
                    getString("nombre") → nombre: String
                    getFloat("imc") → imc: Float → .toDouble() → Double
                                │
                                ▼
                        ResultScreen(nombre, imc)
```

---

## 8. Reto 3 — Interfaz dinámica con colores por categoría

La categoría y su color se calculan **juntos** en una sola expresión `when`:

```kotlin
val (categoria, colorCategoria) = when {
    imc < 18.5 -> "Bajo peso"   to Color.Red
    imc < 25.0 -> "Peso normal" to Color(0xFF388E3C)   // verde oscuro
    imc < 30.0 -> "Sobrepeso"   to Color(0xFFE65100)   // naranja oscuro
    else       -> "Obesidad"    to Color.Red
}
```

El operador `to` crea un `Pair`. El destructuring `val (a, b)` extrae los dos valores en una sola línea.

El color se aplica directamente al `Text`:

```kotlin
Text(
    text  = categoria,        // "Peso normal", "Sobrepeso", etc.
    color = colorCategoria    // verde, naranja o rojo según el caso
)
```

| Categoría | Rango IMC | Color |
|-----------|-----------|-------|
| Bajo peso | < 18.5 | Rojo |
| Peso normal | 18.5 – 24.9 | Verde oscuro |
| Sobrepeso | 25.0 – 29.9 | Naranja oscuro |
| Obesidad | ≥ 30.0 | Rojo |

---

## 9. Cómo se evita que la app crashee

Hay tres capas de protección:

### Capa 1 — `toDoubleOrNull()` en vez de `toDouble()`

| Entrada del usuario | `toDouble()` | `toDoubleOrNull()` |
|---------------------|-------------|-------------------|
| `"70"` | `70.0` | `70.0` |
| `""` (vacío) | **CRASH** `NumberFormatException` | `null` (seguro) |
| `"abc"` | **CRASH** | `null` (seguro) |
| `"7 0"` (con espacio) | **CRASH** | `null` → `.trim()` lo resuelve |

### Capa 2 — Validación explícita antes de calcular

```kotlin
val datosValidos = pesoDouble != null && pesoDouble > 0.0 &&
                   alturaDouble != null && alturaDouble > 0.0
```

La navegación y el cálculo **solo ocurren si `datosValidos == true`**. El operador `!!` (non-null assertion) en la línea siguiente es seguro porque ya verificamos que no son `null`:

```kotlin
val imc = pesoDouble!! / (alturaDouble!! * alturaDouble)
```

### Capa 3 — `Uri.encode()` para el nombre

Sin codificación, un nombre como `"Ana García"` produciría la URL `"resultado/Ana García/22.4"`, que el sistema de navegación no puede parsear y causaría un crash silencioso o pantalla en blanco.

```kotlin
val encodedNombre = Uri.encode(nombre.ifBlank { "Usuario" })
// "Ana García" → "Ana%20Garc%C3%ADa"  ✓ URL válida
```

`ifBlank { "Usuario" }` también cubre el caso donde el nombre está vacío.

### Capa 4 — Operador Elvis `?:` al leer argumentos

```kotlin
val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
val imc    = backStackEntry.arguments?.getFloat("imc")    ?: 0f
```

Si por algún motivo los argumentos llegaran nulos, los valores por defecto (`""` y `0f`) evitan un NullPointerException.

---

## 10. Flujo completo paso a paso

```
┌─────────────────────────────────────────────────┐
│                  PANTALLA 1                     │
│           MainScreen.kt                         │
│                                                 │
│  [Nombre    ]  ← mutableStateOf("")             │
│  [Peso (kg) ]  ← mutableStateOf("")             │
│  [Altura (m)]  ← mutableStateOf("")             │
│                                                 │
│  [    Calcular    ]                             │
│                                                 │
│  ① onClick → toDoubleOrNull() en peso y altura  │
│  ② Si inválido → mostrar texto rojo             │
│  ③ Si válido   → calcular IMC                   │
│                  Uri.encode(nombre)             │
│                  navigate("resultado/...")      │
└───────────────────┬─────────────────────────────┘
                    │ navController.navigate(...)
                    ▼
┌─────────────────────────────────────────────────┐
│                  PANTALLA 2                     │
│           ResultScreen.kt                       │
│                                                 │
│  "Hola [Nombre], tu resultado es:"              │
│                                                 │
│         IMC: 22.4                               │
│                                                 │
│       Peso normal   ← color verde               │
│                                                 │
│  [      Volver      ]                           │
│                                                 │
│  onClick → navigate("main") con                 │
│            popUpTo("main") { inclusive = true } │
│            → recrea MainScreen con campos vacíos│
└─────────────────────────────────────────────────┘
```

### ¿Por qué el botón "Volver" no usa `popBackStack()`?

`popBackStack()` regresa a la instancia anterior de `MainScreen`, la cual **conserva el estado** de los campos en memoria. Para que los campos queden vacíos en cada nuevo cálculo, se usa:

```kotlin
navController.navigate("main") {
    popUpTo("main") { inclusive = true }
}
```

Esto **destruye** la instancia anterior de `MainScreen` y crea una nueva desde cero. Como el estado vive en `remember { mutableStateOf("") }`, la nueva instancia inicia con campos vacíos.

---

## Tecnologías utilizadas

| Herramienta | Versión | Rol |
|-------------|---------|-----|
| Kotlin | 2.2.10 | Lenguaje principal |
| Jetpack Compose BOM | 2026.02.01 | UI declarativa |
| Navigation Compose | 2.8.4 | Navegación entre pantallas |
| Android Gradle Plugin | 9.2.1 | Sistema de build |
| Compile SDK | 36 | Target de compilación |
| Min SDK | 24 (Android 7.0) | Compatibilidad mínima |

---

*Proyecto desarrollado como ejercicio de integración de estados, validación y navegación con Jetpack Compose.*
