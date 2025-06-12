package com.example.chrono_fit_app.ui.screens.principal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chrono_fit_app.common.Constants.CONFIRMAR
import com.example.chrono_fit_app.common.Constants.DOSPUNTOS
import com.example.chrono_fit_app.common.Constants.FORMATO_TIEMPO
import com.example.chrono_fit_app.common.Constants.MAS
import com.example.chrono_fit_app.common.Constants.MENOS
import com.example.chrono_fit_app.common.Constants.NUMERO_DE_SERIES
import com.example.chrono_fit_app.common.Constants.PAUSE
import com.example.chrono_fit_app.common.Constants.PLAY
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_DE_DESCANSO
import com.example.chrono_fit_app.common.Constants.SEGUNDOS_POR_SET
import com.example.chrono_fit_app.common.Constants.SERIES_RESTANTES
import com.example.chrono_fit_app.common.Constants.STOP
import com.example.chrono_fit_app.common.Constants.TIEMPO_TOTAL
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
        tiempoRestanteTotal = uiState.tiempoTotalRestante,
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
    tiempoRestanteTotal: Int,
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

        Text(TIEMPO_TOTAL, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = formatSecondsToTime(tiempoActividadTotal),
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.padding(6.dp))

        ContadorDeTiempoConfigurable(
            titulo = SEGUNDOS_POR_SET,
            valorInicial = segundosSerie,
            valorRestante = segundosSerieRestantes,
            iniciado = empezado,
            enEsteEstado = !enDescanso,
            onChange = onTiempoActividadChanged,
            min = 1
        )

        Spacer(modifier = Modifier.padding(6.dp))

        ContadorDeTiempoConfigurable(
            titulo = SEGUNDOS_DE_DESCANSO,
            valorInicial = segundosDescanso,
            valorRestante = segundosDescansoRestantes,
            iniciado = empezado,
            enEsteEstado = enDescanso,
            onChange = onTiempoDescansoChanged,
            min = 0
        )


        if (!empezado) {
            NumeroSeries(
                NUMERO_DE_SERIES,
                numeroSeries,
                onNumeroSeriesChanged,
                enabled = true,
                min = 1
            )
        }

        BotonesEntrenamiento(
            empezado = empezado,
            pausado = pausado,
            terminado = terminado,
            onStartClick = onStartClick,
            onPauseClick = onPauseClick,
            onResumeClick = onResumeClick,
            onStopClick = onStopClick
        )

        if (empezado) {
            Text(
                "$SERIES_RESTANTES $numeroSeriesRestantes / $numeroSeries",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 6.dp)
            )
            BarraProgresoTotal(segundosSerie, segundosDescanso, numeroSeries, tiempoRestanteTotal)
        }
    }
}

@Composable
fun BarraProgresoTotal(
    segundosSerie: Int,
    segundosDescanso: Int,
    numeroSeries: Int,
    tiempoRestanteTotal: Int
) {
    val tiempoTotal = ((segundosSerie + segundosDescanso) * numeroSeries) - segundosDescanso
    val progresoTotal = tiempoRestanteTotal.toFloat() / tiempoTotal.toFloat()
    LinearProgressIndicator(
        progress = { progresoTotal },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun ContadorDeTiempoConfigurable(
    titulo: String,
    valorInicial: Int,
    valorRestante: Int,
    iniciado: Boolean,
    enEsteEstado: Boolean,
    onChange: (Int) -> Unit,
    min: Int
) {
    val progreso = if (iniciado && enEsteEstado && valorInicial > 0) {
        valorRestante.toFloat() / valorInicial
    } else 1f

    val animatedProgress by animateFloatAsState(targetValue = progreso)

    var editando by remember { mutableStateOf(false) }
    var minutos by remember { mutableStateOf((valorInicial / 60).toString()) }
    var segundos by remember { mutableStateOf((valorInicial % 60).toString().padStart(2, '0')) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (!iniciado) {
                Button(
                    onClick = { if (valorInicial > min) onChange(valorInicial - 1) },
                    enabled = valorInicial > min,
                ) {
                    Icon(imageVector = Icons.Filled.Remove, contentDescription = MENOS)
                }
            }

            if (iniciado && enEsteEstado) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 150.dp, height = 120.dp)
                        .padding(horizontal = 6.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 5.dp
                    )
                    Text(
                        text = formatSecondsToTime(valorRestante),
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = 8.dp)
                    )
                }
            } else {
                if (editando) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 30.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BasicTextField(
                                value = minutos,
                                onValueChange = {
                                    if (it.all { c -> c.isDigit() } && it.length <= 2) {
                                        minutos = it
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.displaySmall.copy(
                                    fontSize = 30.sp,
                                    color = Color.White
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .width(35.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )

                            Text(
                                text = DOSPUNTOS,
                                style = MaterialTheme.typography.displaySmall,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            BasicTextField(
                                value = segundos,
                                onValueChange = {
                                    if (it.all { c -> c.isDigit() } && it.length <= 2) {
                                        val seg = it.toIntOrNull() ?: 0
                                        if (it.isEmpty() || seg in 0..59) {
                                            segundos = it
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.displaySmall.copy(
                                    fontSize = 30.sp,
                                    color = Color.White
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .width(35.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }

                        IconButton(
                            onClick = {
                                editando = false
                                val m = minutos.ifEmpty { "0" }.padStart(2, '0').toInt()
                                val s = segundos.ifEmpty { "0" }.padStart(2, '0').toInt()
                                minutos = m.toString().padStart(2, '0')
                                segundos = s.toString().padStart(2, '0')
                                onChange(m * 60 + s)
                            },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(40.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = CONFIRMAR)
                        }

                    }
                } else {
                    Text(
                        text = minutos.padStart(2, '0') + ":" + segundos.padStart(2, '0'),
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(8.dp)
                            .clickable {
                                minutos = ""
                                segundos = ""
                                editando = true
                            }
                    )
                }
            }

            if (!iniciado) {
                Button(
                    onClick = { onChange(valorInicial + 1) },
                    enabled = true
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = MAS)
                }
            }
        }
    }
}


@Composable
fun NumeroSeries(
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
                Icon(imageVector = Icons.Filled.Remove, contentDescription = MENOS)
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
                Icon(imageVector = Icons.Filled.Add, contentDescription = MAS)
            }
        }
    }
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


private fun formatSecondsToTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return FORMATO_TIEMPO.format(minutes, secs)
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
        tiempoRestanteTotal = 180,
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