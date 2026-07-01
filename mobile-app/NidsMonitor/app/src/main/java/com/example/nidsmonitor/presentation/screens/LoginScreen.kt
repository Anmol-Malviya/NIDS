package com.example.nidsmonitor.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nidsmonitor.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Prevents layout cuts when keyboard opens
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- SECTION 1: NIDS BRANDING LOGO ---
// Look up the resource ID safely using standard Android context.
// If the file doesn't exist, it returns 0 instead of throwing an exception.
        val context = androidx.compose.ui.platform.LocalContext.current
        val logoResourceId = remember {
            context.resources.getIdentifier("app_logo", "drawable", context.packageName)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (logoResourceId != 0) {
            // The logo file exists safely in your drawable folder!
            Image(
                painter = painterResource(id = logoResourceId),
                contentDescription = "NIDS App Logo",
                modifier = Modifier.size(100.dp)
            )
        } else {
            // Fallback vector icon used cleanly without a try-catch block
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Security Shield",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NIDS MONITOR SECURE GATEWAY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "System Access",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECTION 2: INPUT FIELDS ---
        // Username
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (isError) isError = false
            },
            label = { Text("Operator Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password with Toggle Show/Hide Visibility
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (isError) isError = false
            },
            label = { Text("Security Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Error message placeholder
        if (isError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Access Denied: Invalid Operator Credentials",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECTION 3: ACTIONS ---
        Button(
            onClick = {
                if (username == "admin" && password == "admin") {
                    onLoginSuccess()
                } else {
                    isError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Authenticate Console",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme {
        LoginScreen {}
    }
}