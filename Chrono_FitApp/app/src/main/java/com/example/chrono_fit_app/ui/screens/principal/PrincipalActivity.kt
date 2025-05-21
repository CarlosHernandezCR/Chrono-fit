package com.example.chrono_fit_app.ui.screens.principal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PrincipalActivity (
    viewModel: PrincipalViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
){
    val uiState by viewModel.uiState.collectAsState()

}