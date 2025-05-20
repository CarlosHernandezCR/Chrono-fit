package com.example.chrono_fit_app.ui.screens.principal

interface PrincipalContract {
    sealed class PrincipalEvent{

    }

    data class PrincipalState(
        val isLoading: Boolean = false,
        val error: String? = null,
    )
}