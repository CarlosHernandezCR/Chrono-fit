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
            PrincipalContract.PrincipalEvent.MensajeMostrado -> {
                _uiState.update { it.copy(mensaje = null) }
            }
        }
    }

    private fun start() {
        if (timerJob?.isActive == true && !_uiState.value.pausado) {
            return
        }

        if (_uiState.value.pausado) {
            resume()
            return
        }

        timerJob?.cancel()

        val seriesACompletar = _uiState.value.numeroSeries
        val duracionSerie = _uiState.value.segundosSerie
        val duracionDescanso = _uiState.value.segundosDescanso

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

        timerJob = viewModelScope.launch {
            var seriesIteracionActual = _uiState.value.numeroSeriesRestantes

            while (seriesIteracionActual > 0 && _uiState.value.empezado) {
                _uiState.update { it.copy(enDescanso = false) }

                var tiempoSerieActual =
                    if (_uiState.value.segundosSerieRestantes in 1..duracionSerie && !_uiState.value.enDescanso) {
                        _uiState.value.segundosSerieRestantes
                    } else {
                        duracionSerie
                    }
                _uiState.update { it.copy(segundosSerieRestantes = tiempoSerieActual) }


                for (segundo in tiempoSerieActual downTo 1) {
                    while (_uiState.value.pausado && _uiState.value.empezado) {
                        delay(100L)
                    }
                    if (!_uiState.value.empezado) break

                    _uiState.update {
                        it.copy(
                            segundosSerieRestantes = segundo,
                            tiempoActividadTotal = it.tiempoActividadTotal + 1
                        )
                    }
                    delay(1000L)
                }

                if (!_uiState.value.empezado) break

                seriesIteracionActual--
                _uiState.update { it.copy(numeroSeriesRestantes = seriesIteracionActual) }

                if (seriesIteracionActual > 0 && _uiState.value.empezado) {
                    if (duracionDescanso > 0) {
                        _uiState.update { it.copy(enDescanso = true) }

                        var tiempoDescansoActual =
                            if (_uiState.value.segundosDescansoRestantes in 1..duracionDescanso && _uiState.value.enDescanso) {
                                _uiState.value.segundosDescansoRestantes
                            } else {
                                duracionDescanso
                            }
                        _uiState.update { it.copy(segundosDescansoRestantes = tiempoDescansoActual) }

                        for (segundo in tiempoDescansoActual downTo 1) {
                            while (_uiState.value.pausado && _uiState.value.empezado) {
                                delay(100L)
                            }
                            if (!_uiState.value.empezado) break

                            _uiState.update { it.copy(segundosDescansoRestantes = segundo) }
                            delay(1000L)
                        }
                        if (!_uiState.value.empezado) break
                        _uiState.update { it.copy(segundosDescansoRestantes = duracionDescanso) }
                    }
                    _uiState.update {
                        it.copy(
                            enDescanso = false,
                            segundosSerieRestantes = duracionSerie
                        )
                    }
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
}