package com.example.chrono_fit_app.ui.screens.principal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chrono_fit_app.common.Constants.MAS
import com.example.chrono_fit_app.common.Constants.MENOS
import com.example.chrono_fit_app.common.Constants.NUMERO_DE_SERIES
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_DE_DESCANSO
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_POR_SET
import com.example.chrono_fit_app.common.Constants.TIEMPO_TOTAL_TRANSCURRIDO
import com.example.chrono_fit_app.common.Constants.TITULO
import kotlinx.coroutines.launch

@Composable
fun PrincipalActivity(
    modifier: Modifier = Modifier
) {
    val viewModel: PrincipalViewModel = viewModel()
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

    PantallaPrincipal(
        tiempoActividadTotal = uiState.tiempoActividadTotal,
        segundosSerie = uiState.segundosSerie,
        segundosSerieRestantes = uiState.segundosSerieRestantes,
        segundosDescanso = uiState.segundosDescanso,
        segundosDescansoRestantes = uiState.segundosDescansoRestantes,
        numeroSeries = uiState.numeroSeries,
        numeroSeriesRestantes = uiState.numeroSeriesRestantes,
        empezado = uiState.empezado,
        pausado = uiState.pausado,
        enDescanso = uiState.enDescanso,
        terminado = uiState.terminado,
        onStartClick = { viewModel.handleEvent(PrincipalContract.PrincipalEvent.Start) },
        onStopClick = { viewModel.handleEvent(PrincipalContract.PrincipalEvent.Stop) },
        onPauseClick = { viewModel.handleEvent(PrincipalContract.PrincipalEvent.Pause) },
        onResumeClick = { viewModel.handleEvent(PrincipalContract.PrincipalEvent.Resume) },
        OnTiempoActividadChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnTiempoActividadChanged(
                    it
                )
            )
        },
        OnTiempoDescansoChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnTiempoDescansoChanged(
                    it
                )
            )
        },
        OnNumeroSeriesChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnNumeroSeriesChanged(
                    it
                )
            )
        },
        modifier = modifier
    )
}

@Composable
fun PantallaPrincipal(
    tiempoActividadTotal: Int,
    segundosSerie: Int,
    segundosSerieRestantes: Int,
    segundosDescanso: Int,
    segundosDescansoRestantes: Int,
    numeroSeries: Int,
    numeroSeriesRestantes: Int,
    empezado: Boolean,
    pausado: Boolean,
    enDescanso: Boolean,
    terminado: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    OnTiempoActividadChanged: (Int) -> Unit,
    OnTiempoDescansoChanged: (Int) -> Unit,
    OnNumeroSeriesChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = TITULO,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 30.dp)
        )

        Text(TIEMPO_TOTAL_TRANSCURRIDO, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = formatSecondsToTime(tiempoActividadTotal),
            style = MaterialTheme.typography.displayMedium
        )

        Text(SEGUNDOS_POR_SET, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = if (empezado && !enDescanso) {
                formatSecondsToTime(segundosSerieRestantes)
            } else {
                formatSecondsToTime(segundosSerie)
            },
            style = MaterialTheme.typography.displayLarge
        )

        Text(SEGUNDOS_DE_DESCANSO, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = if (empezado && enDescanso) {
                formatSecondsToTime(segundosDescansoRestantes)
            } else {
                formatSecondsToTime(segundosDescanso)
            },
            style = MaterialTheme.typography.displayMedium
        )

        NumberSelector(
            SEGUNDOS_POR_SET,
            segundosSerie,
            OnTiempoActividadChanged,
            enabled = !empezado,
            min = 1
        )
        NumberSelector(
            SEGUNDOS_DE_DESCANSO,
            segundosDescanso,
            OnTiempoDescansoChanged,
            enabled = !empezado,
            min = 0
        )
        NumberSelector(
            NUMERO_DE_SERIES,
            numeroSeries,
            OnNumeroSeriesChanged,
            enabled = !empezado,
            min = 1
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onStopClick, enabled = empezado) {
                Text("■")
            }
            if (!empezado || terminado) {
                Button(onClick = onStartClick) {
                    Text("▶")
                }
            } else if (empezado && !pausado && !terminado) {
                Button(onClick = onPauseClick) {
                    Text("Ⅱ")
                }
            } else if (empezado && pausado && !terminado) {
                Button(onClick = onResumeClick) {
                    Text("►")
                }
            }
        }
        Text("Series restantes: $numeroSeriesRestantes / $numeroSeries")
    }
}

@Composable
fun NumberSelector(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    step: Int = 1,
    min: Int,
    max: Int = Int.MAX_VALUE
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { if (value > min) onValueChange(value - step) },
                enabled = enabled && value > min
            ) {
                Text(MENOS)
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = { if (value < max) onValueChange(value + step) },
                enabled = enabled && value < max 
            ) {
                Text(MAS)
            }
        }
    }
}

private fun formatSecondsToTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}

@Composable
@Preview(showBackground = true)
fun PantallaPrincipalPreview() {
    PantallaPrincipal(
        tiempoActividadTotal = 0,
        segundosSerie = 3,
        segundosSerieRestantes = 3,
        segundosDescanso = 3,
        segundosDescansoRestantes = 3,
        numeroSeries = 3,
        numeroSeriesRestantes = 3,
        empezado = false,
        pausado = false,
        enDescanso = false,
        terminado = false,
        onStartClick = {},
        onStopClick = {},
        onPauseClick = {},
        onResumeClick = {},
        OnTiempoActividadChanged = {},
        OnTiempoDescansoChanged = {},
        OnNumeroSeriesChanged = {}
    )
}