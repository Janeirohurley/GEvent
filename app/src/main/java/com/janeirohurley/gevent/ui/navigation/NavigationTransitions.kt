package com.janeirohurley.gevent.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

/**
 * Optimisation: Transitions ultra-rapides et fluides pour la navigation
 * Utilisation de specs d'animations optimisées pour les performances
 */

// Transition instantanée (la plus rapide - recommandée)
object InstantTransitions {
    fun enter(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        EnterTransition.None
    }

    fun exit(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        ExitTransition.None
    }
}

// Transition ultra-rapide avec fade (bon compromis vitesse/esthétique)
object FastFadeTransitions {
    private const val DURATION = 120 // Très rapide

    fun enter(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(
            animationSpec = tween(
                durationMillis = DURATION,
                easing = FastOutSlowInEasing
            )
        )
    }

    fun exit(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(
            animationSpec = tween(
                durationMillis = DURATION / 2, // Exit plus rapide
                easing = FastOutSlowInEasing
            )
        )
    }
}

// Transition avec slide minimal (pour un effet visuel subtil)
object MinimalSlideTransitions {
    private const val DURATION = 150
    private const val SLIDE_OFFSET = 50 // Pixels - très petit mouvement

    fun enter(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(
            animationSpec = tween(DURATION)
        ) + slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(DURATION),
            initialOffset = { SLIDE_OFFSET }
        )
    }

    fun exit(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(
            animationSpec = tween(DURATION / 2)
        )
    }
}

// Transition avec scale (effet moderne type Material Design)
object ScaleTransitions {
    private const val DURATION = 140

    fun enter(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(
            animationSpec = tween(DURATION)
        ) + scaleIn(
            initialScale = 0.95f, // Commence légèrement plus petit
            animationSpec = tween(DURATION)
        )
    }

    fun exit(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(
            animationSpec = tween(DURATION / 2)
        ) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(DURATION / 2)
        )
    }
}
