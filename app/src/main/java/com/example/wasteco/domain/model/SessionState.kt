package com.example.wasteco.domain.model

sealed interface SessionState {
    /** Session is still being validated against the server. */
    data object Checking : SessionState

    /** A token exists and was not rejected by the server. */
    data object Authenticated : SessionState

    /** No token, or the token was rejected (expired / invalid). */
    data object Unauthenticated : SessionState
}
