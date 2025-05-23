package com.example.chrono_fit_app.ui.screens.principal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

@HiltViewModel
class PrincipalViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrincipalContract.PrincipalState())
    val uiState: StateFlow<PrincipalContract.PrincipalState> = _uiState.asStateFlow()

    fun handleEvent(event: PrincipalContract.PrincipalEvent) {
        when (event) {
            is PrincipalContract.PrincipalEvent.OnTiempoActividadChanged -> {
                _uiState.value = _uiState.value.copy(tiempoActividad = event.nuevoValor)
            }

            is PrincipalContract.PrincipalEvent.OnTiempoDescansoChanged -> {
                _uiState.value = _uiState.value.copy(tiempoDescanso = event.nuevoValor)
            }

            is PrincipalContract.PrincipalEvent.OnNumeroSeriesChanged -> {
                _uiState.value = _uiState.value.copy(numeroSeries = event.nuevoValor)
            }

            is PrincipalContract.PrincipalEvent.Start -> {start()}

            is PrincipalContract.PrincipalEvent.Stop -> {stop()}
            is PrincipalContract.PrincipalEvent.MensajeMostrado -> _uiState.update { it.copy(mensaje = null) }
        }
    }

    private fun start(){

    }

    private fun stop(){

    }
}