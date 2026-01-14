package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.CustomInput
import com.janeirohurley.gevent.viewmodel.AuthViewModel
import com.janeirohurley.gevent.utils.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val successMessage by authViewModel.successMessage.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Validation
    val isPasswordMatch = password == confirmPassword
    val isFormValid = username.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            isPasswordMatch &&
            password.length >= 6

    // Navigation automatique après inscription réussie
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo ou Image de l'app
            Image(
                painter = painterResource(id = R.drawable.logo1),
                contentDescription = "GEvent Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
            )

            // Titre
            Text(
                text = "Créer un compte",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rejoignez GEvent aujourd'hui",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Champ Nom d'utilisateur (obligatoire)
            CustomInput(
                value = username,
                onValueChange = { username = it },
                placeholder = "Nom d'utilisateur *",
                height = 50.dp
            )
            
            // Afficher le username nettoyé si différent
            val cleanedUsername = ValidationUtils.cleanUsername(username)
            
            if (username.isNotBlank() && cleanedUsername != username) {
                Text(
                    text = "Sera enregistré comme: $cleanedUsername",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Email (obligatoire)
            CustomInput(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email *",
                height = 50.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Prénom (optionnel)
            CustomInput(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = "Prénom",
                height = 50.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Nom (optionnel)
            CustomInput(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = "Nom",
                height = 50.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Téléphone (optionnel)
            CustomInput(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = "Téléphone",
                height = 50.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Mot de passe
            CustomInput(
                value = password,
                onValueChange = { password = it },
                placeholder = "Mot de passe *",
                height = 50.dp,
                isPassword = true,
                passwordVisible = passwordVisible,
                suffix = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible)
                                    R.drawable.fi_rr_eye
                                else
                                    R.drawable.fi_rr_eye_crossed
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Confirmation mot de passe
            CustomInput(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirmer le mot de passe *",
                height = 50.dp,
                isPassword = true,
                passwordVisible = confirmPasswordVisible,
                suffix = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (confirmPasswordVisible)
                                    R.drawable.fi_rr_eye
                                else
                                    R.drawable.fi_rr_eye_crossed
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                }
            )
            // Messages d'erreur
            if (password.isNotBlank() && password.length < 6) {
                Text(
                    text = "Le mot de passe doit contenir au moins 6 caractères",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            if (confirmPassword.isNotBlank() && password.isNotBlank() && !isPasswordMatch) {
                Text(
                    text = "Les mots de passe ne correspondent pas",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            // Message d'erreur de l'API
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton d'inscription
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isFormValid) {
                        authViewModel.register(
                            username = username,
                            email = email,
                            password = password,
                            firstName = firstName.ifBlank { null },
                            lastName = lastName.ifBlank { null },
                            phoneNumber = phoneNumber.ifBlank { null }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading && isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Créer mon compte",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lien vers la connexion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vous avez déjà un compte? ",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                TextButton(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Se connecter",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
