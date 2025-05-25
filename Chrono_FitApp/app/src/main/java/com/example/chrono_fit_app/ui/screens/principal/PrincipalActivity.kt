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
import com.example.chrono_fit_app.common.Constants.DESCANSO
import com.example.chrono_fit_app.common.Constants.MAS
import com.example.chrono_fit_app.common.Constants.MENOS
import com.example.chrono_fit_app.common.Constants.NUMERO_DE_SERIES
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_DE_DESCANSO
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_POR_SET
import com.example.chrono_fit_app.common.Constants.TIEMPO_RESTANTE_DE_LA_SERIE
import com.example.chrono_fit_app.common.Constants.TIEMPO_TOTAL_TRANSCURRIDO
import com.example.chrono_fit_app.common.Constants.TITULO
import kotlinx.coroutines.launch


@Composable
fun PrincipalActivity (
    modifier: Modifier = Modifier
){
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
        tiempoActividad = uiState.tiempoActividad,
        tiempoDescanso = uiState.segundosDescanso,
        numeroSeries = uiState.numeroSeries,
        tiempoRestante = uiState.tiempoRestante,
        empezado = uiState.empezado,
        pausado = uiState.pausado,
        terminado = uiState.terminado,
        onStartClick = { viewModel.handleEvent(PrincipalContract.PrincipalEvent.Start) },
        onStopClick = { viewModel.handleEvent(PrincipalContract.PrincipalEvent.Stop) },
        onPauseClick = {viewModel.handleEvent(PrincipalContract.PrincipalEvent.Stop)},
        OnTiempoActividadChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnTiempoActividadChanged(it)
            )
        },
        OnTiempoDescansoChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnTiempoDescansoChanged(it)
            )
        },
        OnNumeroSeriesChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnNumeroSeriesChanged(it)
            )
        },
        modifier = modifier
    )
}

@Composable
fun PantallaPrincipal(
    tiempoActividad: Int,
    tiempoDescanso: Int,
    numeroSeries: Int,
    tiempoRestante: Int,
    empezado: Boolean,
    pausado: Boolean,
    terminado: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onPauseClick: () -> Unit,
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
            text = formatSecondsToTime(180 - tiempoRestante),
            style = MaterialTheme.typography.displayMedium
        )

        Text(TIEMPO_RESTANTE_DE_LA_SERIE, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = formatSecondsToTime(tiempoRestante),
            style = MaterialTheme.typography.displayLarge
        )

        Text(DESCANSO, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = formatSecondsToTime(tiempoDescanso),
            style = MaterialTheme.typography.displayMedium
        )

        NumberSelector(SEGUNDOS_POR_SET, tiempoActividad, OnTiempoActividadChanged, min = 60)
        NumberSelector(SEGUNDOS_DE_DESCANSO, tiempoDescanso, OnTiempoDescansoChanged, min = 0)
        NumberSelector(NUMERO_DE_SERIES, numeroSeries, OnNumeroSeriesChanged, min = 1)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onStopClick, enabled = empezado && !terminado) {
                Text("■")
            }
            Button(onClick = onStartClick, enabled = !empezado || terminado) {
                Text("▶")
            }
            Button(onClick = onPauseClick, enabled = !pausado && !terminado) {
                Text("Ⅱ")
            }
        }
    }
}

@Composable
fun NumberSelector(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
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
                enabled = value > min
            ) {
                Text(MENOS)
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = { if (value < max) onValueChange(value + step) },
                enabled = value < max
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
        tiempoActividad = 30,
        tiempoDescanso = 20,
        numeroSeries = 2,
        tiempoRestante = 180,
        empezado = false,
        pausado = false,
        terminado = false,
        onStartClick = {},
        onStopClick = {},
        OnTiempoActividadChanged = {},
        OnTiempoDescansoChanged = {},
        OnNumeroSeriesChanged = {},
        onPauseClick = {}
    )
}