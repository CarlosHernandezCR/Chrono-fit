package com.example.chrono_fit_app.ui.screens.principal

interface PrincipalContract {

    data class PrincipalState(
        val tiempoActividadTotal: Int = 0,
        val segundosSerie: Int = 60,
        val segundosSerieRestantes: Int = segundosSerie,
        val segundosDescanso: Int = 30,
        val segundosDescansoRestantes: Int = segundosDescanso,
        val numeroSeries: Int = 1,
        val numeroSeriesRestantes: Int = numeroSeries,
        val mensaje: String? = null,
        val pausado: Boolean = false,
        val empezado: Boolean = false,
        val enDescanso: Boolean = false,
        val terminado: Boolean = false
    )

    sealed interface PrincipalEvent {
        data class OnTiempoActividadChanged(val nuevoValor: Int) : PrincipalEvent
        data class OnTiempoDescansoChanged(val nuevoValor: Int) : PrincipalEvent
        data class OnNumeroSeriesChanged(val nuevoValor: Int) : PrincipalEvent
        data object Start : PrincipalEvent
        data object Stop : PrincipalEvent
        data object Pause : PrincipalEvent
        data object Resume : PrincipalEvent
        data object MensajeMostrado : PrincipalEvent
    }
}
