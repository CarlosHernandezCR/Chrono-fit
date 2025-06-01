package com.example.chrono_fit_app.ui.screens.principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chrono_fit_app.common.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrincipalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrincipalContract.PrincipalState())
    val uiState: StateFlow<PrincipalContract.PrincipalState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun handleEvent(event: PrincipalContract.PrincipalEvent) {
        when (event) {
            is PrincipalContract.PrincipalEvent.OnTiempoActividadChanged -> {
                _uiState.update {
                    it.copy(
                        segundosSerie = event.nuevoValor,
                        segundosSerieRestantes = if (!it.empezado) event.nuevoValor else it.segundosSerieRestantes
                    )
                }
            }
            is PrincipalContract.PrincipalEvent.OnTiempoDescansoChanged -> {
                _uiState.update {
                    it.copy(
                        segundosDescanso = event.nuevoValor,
                        segundosDescansoRestantes = if (!it.empezado) event.nuevoValor else it.segundosDescansoRestantes
                    )
                }
            }
            is PrincipalContract.PrincipalEvent.OnNumeroSeriesChanged -> {
                _uiState.update {
                    it.copy(
                        numeroSeries = event.nuevoValor,
                        numeroSeriesRestantes = if (!it.empezado) event.nuevoValor else it.numeroSeriesRestantes
                    )
                }
            }
            PrincipalContract.PrincipalEvent.Start -> start()
            PrincipalContract.PrincipalEvent.Stop -> stop()
            PrincipalContract.PrincipalEvent.Pause -> pause()
            PrincipalContract.PrincipalEvent.Resume -> resume()
            PrincipalContract.PrincipalEvent.MensajeMostrado -> { _uiState.update { it.copy(mensaje = null) } }
        }
    }

    private fun start() {
        if (_uiState.value.pausado || (timerJob?.isActive == true)) return

        val estado = _uiState.value
        val seriesACompletar = estado.numeroSeries
        val duracionSerie = estado.segundosSerie
        val duracionDescanso = estado.segundosDescanso

        if (duracionSerie <= 0 || seriesACompletar <= 0) {
            _uiState.update { it.copy(mensaje = Constants.CONFIGURACION_INCORRECTA) }
            return
        }

        _uiState.update {
            it.copy(
                empezado = true,
                terminado = false,
                pausado = false,
                enDescanso = false,
                numeroSeriesRestantes = seriesACompletar,
                segundosSerieRestantes = duracionSerie,
                segundosDescansoRestantes = duracionDescanso,
                tiempoActividadTotal = 0
            )
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seriesRestantes = seriesACompletar

            while (seriesRestantes > 0 && _uiState.value.empezado) {
                _uiState.update { it.copy(enDescanso = false) }

                val duracionSerieActual = _uiState.value.segundosSerieRestantes.takeIf {
                    it in 1 .. _uiState.value.segundosSerie && !_uiState.value.enDescanso
                } ?: _uiState.value.segundosSerie

                _uiState.update { it.copy(segundosSerieRestantes = duracionSerieActual) }

                val continua = contarTiempo(duracionSerieActual) {
                    _uiState.update { state -> state.copy(segundosSerieRestantes = it) }
                }
                if (!continua) break

                _uiState.update { it.copy(segundosSerieRestantes = 0) }

                seriesRestantes--
                _uiState.update { it.copy(numeroSeriesRestantes = seriesRestantes) }

                if (seriesRestantes > 0 && _uiState.value.segundosDescanso > 0) {
                    _uiState.update { it.copy(enDescanso = true) }

                    val duracionDescansoActual = _uiState.value.segundosDescansoRestantes.takeIf {
                        it in 1 .. _uiState.value.segundosDescanso && _uiState.value.enDescanso
                    } ?: _uiState.value.segundosDescanso

                    _uiState.update { it.copy(segundosDescansoRestantes = duracionDescansoActual) }

                    val continuaDescanso = contarTiempo(duracionDescansoActual) {
                        _uiState.update { state -> state.copy(segundosDescansoRestantes = it) }
                    }
                    if (!continuaDescanso) break

                    _uiState.update { it.copy(segundosDescansoRestantes = 0, enDescanso = false) }
                }
            }

            if (_uiState.value.empezado) {
                _uiState.update {
                    it.copy(
                        empezado = false,
                        terminado = true,
                        enDescanso = false,
                        segundosSerieRestantes = 0,
                        segundosDescansoRestantes = 0,
                        mensaje = Constants.ENTRENAMIENTO_COMPLETADO
                    )
                }
            }
        }
    }

    private fun pause() {
        if (_uiState.value.empezado && !_uiState.value.pausado) {
            _uiState.update { it.copy(pausado = true) }
        }
    }

    private fun resume() {
        if (_uiState.value.empezado && _uiState.value.pausado) {
            _uiState.update { it.copy(pausado = false) }
        }
    }

    private fun stop() {
        timerJob?.cancel()
        timerJob = null
        val currentState = _uiState.value
        _uiState.update {
            PrincipalContract.PrincipalState(
                segundosSerie = currentState.segundosSerie,
                segundosDescanso = currentState.segundosDescanso,
                numeroSeries = currentState.numeroSeries,
                segundosSerieRestantes = currentState.segundosSerie,
                segundosDescansoRestantes = currentState.segundosDescanso,
                numeroSeriesRestantes = currentState.numeroSeries,
                mensaje = if (currentState.empezado && !currentState.terminado) Constants.ENTRENAMIENTO_DETENIDO else null,
                empezado = false,
                pausado = false,
                terminado = true,
                tiempoActividadTotal = 0
            )
        }
    }
    private suspend fun contarTiempo(
        duracion: Int,
        actualizarTiempoRestante: (Int) -> Unit
    ): Boolean {
        for (segundo in duracion downTo 1) {
            while (_uiState.value.pausado && _uiState.value.empezado) {
                delay(100L)
            }
            if (!_uiState.value.empezado) return false

            actualizarTiempoRestante(segundo)
            delay(1000L)

            _uiState.update {
                it.copy(tiempoActividadTotal = it.tiempoActividadTotal + 1)
            }
        }
        return true
    }

}