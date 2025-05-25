package com.example.chrono_fit_app.ui.screens.principal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.example.chrono_fit_app.common.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PrincipalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrincipalContract.PrincipalState())
    val uiState: StateFlow<PrincipalContract.PrincipalState> = _uiState.asStateFlow()
    private var timerJob: Job? = null

    fun handleEvent(event: PrincipalContract.PrincipalEvent) {
        when (event) {
            is PrincipalContract.PrincipalEvent.OnTiempoActividadChanged -> {
                _uiState.value = _uiState.value.copy(tiempoActividad = event.nuevoValor)
            }

            is PrincipalContract.PrincipalEvent.OnTiempoDescansoChanged -> {
                _uiState.value = _uiState.value.copy(segundosDescanso = event.nuevoValor)
            }

            is PrincipalContract.PrincipalEvent.OnNumeroSeriesChanged -> {
                _uiState.value = _uiState.value.copy(numeroSeries = event.nuevoValor)
            }

            is PrincipalContract.PrincipalEvent.Start -> {start()}

            is PrincipalContract.PrincipalEvent.Stop -> {stop()}
            is PrincipalContract.PrincipalEvent.MensajeMostrado -> _uiState.update { it.copy(mensaje = null) }
            PrincipalContract.PrincipalEvent.Pause -> {pause()}
        }
    }

    private fun pause() {
        timerJob?.cancel()
        _uiState.update { it.copy(pausado = true) }
    }
    private fun start() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            val actividad = _uiState.value.tiempoActividad
            val descanso = _uiState.value.segundosDescanso
            val series = _uiState.value.numeroSeries

            if (actividad <= 0 || descanso < 0 || series <= 0) {
                _uiState.update { it.copy(mensaje = Constants.CONFIGURACION_INCORRECTA) }
                return@launch
            }

            _uiState.update { it.copy(empezado = true, terminado = false) }
            repeat(series) { serie ->
                for (segundo in actividad downTo 1) {
                    _uiState.update { it.copy(tiempoRestante = segundo) }
                    delay(1000L)
                }
                if (serie < series - 1) {
                    for (segundo in descanso downTo 1) {
                        _uiState.update { it.copy(tiempoRestante = segundo) }
                        delay(1000L)
                    }
                }
            }
            _uiState.update {
                it.copy(
                    tiempoRestante = 0,
                    terminado = true,
                    empezado = false,
                    mensaje = Constants.ENTRENAMIENTO_COMPLETADO
                )
            }
        }
    }

    private fun stop() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                tiempoRestante = it.segundosSerie,
                tiempoActividad = 0,
                terminado = true,
                empezado = false,
                pausado = false,
                mensaje = Constants.ENTRENAMIENTO_DETENIDO
            )
        }
    }
}