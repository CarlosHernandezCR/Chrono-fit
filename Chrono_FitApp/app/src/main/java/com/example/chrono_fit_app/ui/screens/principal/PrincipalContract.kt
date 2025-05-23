package com.example.chrono_fit_app.ui.screens.principal

interface PrincipalContract {

    data class PrincipalState(
        val tiempoActividad: Int = 0,
        val tiempoSerie: Int = 0,
        val tiempoDescanso: Int = 0,
        val numeroSeries: Int = 0,
        val tiempoRestante: Int = 0,
        val mensaje: String? = null,
        val pausado: Boolean = false,
        val empezado: Boolean = false,
        val terminado: Boolean = false
    )

    sealed interface PrincipalEvent {
        data class OnTiempoActividadChanged(val nuevoValor: Int) : PrincipalEvent
        data class OnTiempoDescansoChanged(val nuevoValor: Int) : PrincipalEvent
        data class OnNumeroSeriesChanged(val nuevoValor: Int) : PrincipalEvent
        data object Start : PrincipalEvent
        data object Stop : PrincipalEvent
        data object MensajeMostrado : PrincipalEvent
    }
}
