package com.example.aplicacion

import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aplicacion.ui.theme.AplicacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionTheme {
                MainContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainContent() {
    AplicacionTheme {
        MainContent()
    }
}

// Lista local de usuarios registrados
val usuariosRegistrados = mutableStateListOf<Usuario>()

@Composable
fun MainContent() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var showRegisterScreen by remember { mutableStateOf(false) }
    var showForgotPasswordScreen by remember { mutableStateOf(false) }

    when {
        isLoggedIn -> MinutaScreen(onLogout = { isLoggedIn = false })
        showRegisterScreen -> RegisterScreen(
            onBackToLoginClick = { showRegisterScreen = false },
            onRegisterSuccess = { showRegisterScreen = false }
        )
        showForgotPasswordScreen -> ForgotPasswordScreen(onBackToLoginClick = { showForgotPasswordScreen = false })
        else -> LoginScreen(
            onLoginSuccess = { isLoggedIn = true },
            onCreateAccountClick = { showRegisterScreen = true },
            onForgotPasswordClick = { showForgotPasswordScreen = true }
        )
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Estados para controlar el foco en los TextFields
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lottie animation
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logoanimacion))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )

        // Email input field
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = "Correo Electrónico") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isEmailFocused) Color(0xFFE3F2FD) else Color.White,
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .onFocusChanged { focusState ->
                    isEmailFocused = focusState.isFocused
                }
        )


        // Password input field
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isPasswordFocused) Color(0xFFE0EFFA) else Color.White,
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .onFocusChanged { focusState ->
                    isPasswordFocused = focusState.isFocused
                }
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login button
        Button(
            onClick = {
                val user = usuariosRegistrados.find { it.email == email && it.password == password }
                if (user != null) {
                    onLoginSuccess()
                } else {
                    errorMessage = "Correo o contraseña incorrectos"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // Create account button
        TextButton(onClick = onCreateAccountClick) {
            Text(text = "Crear Cuenta", color = Color(0xFF0D47A1))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot password button
        TextButton(onClick = onForgotPasswordClick) {
            Text(text = "¿Olvidaste tu contraseña?", color = Color(0xFF0D47A1))
        }
    }
}



@Composable
fun RegisterScreen(onBackToLoginClick: () -> Unit, onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = "Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text(text = "Repetir Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                } else if (usuariosRegistrados.any { it.email == email }) {
                    errorMessage = "El correo ya está registrado"
                } else {
                    usuariosRegistrados.add(Usuario(email = email, password = password))
                    onRegisterSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Registrar", color = Color.White, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = onBackToLoginClick) {
            Text(text = "Volver a Iniciar sesión", color = Color(0xFF0D47A1))
        }
    }
}

@Composable
fun ForgotPasswordScreen(onBackToLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = "Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        }

        if (successMessage.isNotEmpty()) {
            Text(text = successMessage, color = Color.Green, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = {
                val user = usuariosRegistrados.find { it.email == email }
                if (user != null) {
                    successMessage = "Se ha enviado un correo de recuperación a $email"
                    errorMessage = ""
                } else {
                    errorMessage = "El correo no está registrado"
                    successMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Recuperar Contraseña", color = Color.White, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = onBackToLoginClick) {
            Text(text = "Volver a Iniciar sesión", color = Color(0xFF0D47A1))
        }
    }
}

@Composable
fun MinutaScreen(onLogout: () -> Unit) {
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val recetas = remember { mutableStateOf(generateMinuta()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Minuta Nutricional Semanal",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(diasSemana.zip(recetas.value)) { (dia, receta) ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = dia,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = receta,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Cerrar sesión")
        }
    }
}

fun generateMinuta(): List<String> {
    return listOf(
        "Desayuno saludable, Huevo, Almuerzo balanceado, Cena ligera",
        "Desayuno energético, Almuerzo proteico, Cena baja en carbohidratos",
        "Batido de frutas, Ensalada de pollo, Sopa de verduras",
        "Huevos revueltos, Pasta integral, Pescado al vapor",
        "Avena con frutas, Carne a la parrilla, Ensalada fresca",
        "Tostadas integrales, Pollo al horno, Verduras al vapor",
        "Frutas frescas, Arroz integral, Pechuga de pavo"
    )
}

data class Usuario(val email: String, val password: String)
