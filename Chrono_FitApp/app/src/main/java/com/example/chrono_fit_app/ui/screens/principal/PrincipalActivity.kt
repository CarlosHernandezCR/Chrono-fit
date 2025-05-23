package com.example.chrono_fit_app.ui.screens.principal

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch


@Composable
fun PrincipalActivity (
    viewModel: PrincipalViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
){
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message,
                duration = SnackbarDuration.Short
            )
        }
    }
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            showSnackbar(it)
            viewModel.handleEvent(PrincipalContract.PrincipalEvent.MensajeMostrado)
        }
    }
}