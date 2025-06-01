package com.example.chrono_fit_app.ui.screens.principal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chrono_fit_app.common.Constants.FORMATO_TIEMPO
import com.example.chrono_fit_app.common.Constants.MAS
import com.example.chrono_fit_app.common.Constants.MENOS
import com.example.chrono_fit_app.common.Constants.NUMERO_DE_SERIES
import com.example.chrono_fit_app.common.Constants.PAUSE
import com.example.chrono_fit_app.common.Constants.PLAY
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_DE_DESCANSO
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_POR_SET
import com.example.chrono_fit_app.common.Constants.STOP
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
        onTiempoActividadChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnTiempoActividadChanged(
                    it
                )
            )
        },
        onTiempoDescansoChanged = {
            viewModel.handleEvent(
                PrincipalContract.PrincipalEvent.OnTiempoDescansoChanged(
                    it
                )
            )
        },
        onNumeroSeriesChanged = {
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
    onTiempoActividadChanged: (Int) -> Unit,
    onTiempoDescansoChanged: (Int) -> Unit,
    onNumeroSeriesChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val controlsEnabled = !empezado
    val minSegundosSerie = 1
    val minSegundosDescanso = 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = TITULO,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

        Text(TIEMPO_TOTAL_TRANSCURRIDO, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = formatSecondsToTime(tiempoActividadTotal),
            style = MaterialTheme.typography.displayMedium,
        )
        Spacer(modifier = Modifier.padding(6.dp))
        Text(SEGUNDOS_POR_SET, style = MaterialTheme.typography.headlineMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (segundosSerie > minSegundosSerie) onTiempoActividadChanged(
                        segundosSerie - 1
                    )
                },
                enabled = controlsEnabled && segundosSerie > minSegundosSerie
            ) {
                Text(MENOS)
            }
            Text(
                text = if (empezado && !enDescanso) {
                    formatSecondsToTime(segundosSerieRestantes)
                } else {
                    formatSecondsToTime(segundosSerie)
                },
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(200.dp)
                    .padding(horizontal = 6.dp)
            )
            Button(
                onClick = { onTiempoActividadChanged(segundosSerie + 1) },
                enabled = controlsEnabled
            ) {
                Text(MAS)
            }
        }
        Spacer(modifier = Modifier.padding(6.dp))

        Text(SEGUNDOS_DE_DESCANSO, style = MaterialTheme.typography.headlineMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (segundosDescanso > minSegundosDescanso) onTiempoDescansoChanged(
                        segundosDescanso - 1
                    )
                },
                enabled = controlsEnabled && segundosDescanso > minSegundosDescanso
            ) {
                Text(MENOS)
            }
            Text(
                text = if (empezado && enDescanso) {
                    formatSecondsToTime(segundosDescansoRestantes)
                } else {
                    formatSecondsToTime(segundosDescanso)
                },
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(200.dp)
                    .padding(horizontal = 6.dp)
            )
            Button(
                onClick = { onTiempoDescansoChanged(segundosDescanso + 1) },
                enabled = controlsEnabled
            ) {
                Text(MAS)
            }
        }

        Spacer(modifier = Modifier.padding(6.dp))

        NumberSelector(
            NUMERO_DE_SERIES,
            numeroSeries,
            onNumeroSeriesChanged,
            enabled = controlsEnabled,
            min = 1
        )

        BotonesEntrenamiento(
            empezado = empezado,
            pausado = pausado,
            terminado = terminado,
            onStartClick = onStartClick,
            onPauseClick = onPauseClick,
            onResumeClick = onResumeClick,
            onStopClick = onStopClick
        )

        Text(
            "Series restantes: $numeroSeriesRestantes / $numeroSeries",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 6.dp)
        )
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
        Text(label, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.padding(6.dp))
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
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
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
    return FORMATO_TIEMPO.format(minutes, secs)
}

@Composable
fun BotonesEntrenamiento(
    empezado: Boolean,
    pausado: Boolean,
    terminado: Boolean,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 24.dp)
    ) {
        OutlinedButton(
            onClick = onStopClick,
            enabled = empezado,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Stop, contentDescription = STOP)
        }

        when {
            !empezado || terminado -> {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = PLAY)
                }
            }
            empezado && !pausado && !terminado -> {
                Button(
                    onClick = onPauseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Pause, contentDescription = PAUSE)
                }
            }
            empezado && pausado && !terminado -> {
                Button(
                    onClick = onResumeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = PLAY)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PantallaPrincipalPreview() {
    PantallaPrincipal(
        tiempoActividadTotal = 0,
        segundosSerie = 60,
        segundosSerieRestantes = 60,
        segundosDescanso = 30,
        segundosDescansoRestantes = 30,
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
        onTiempoActividadChanged = {},
        onTiempoDescansoChanged = {},
        onNumeroSeriesChanged = {}
    )
}