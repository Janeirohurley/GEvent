package com.janeirohurley.gevent.ui.navigation

/**
 * Configuration centralisée pour les transitions de navigation
 * Changez simplement la valeur de CURRENT_TRANSITION_TYPE pour tester différentes animations
 */

enum class TransitionType {
    INSTANT,        // 0ms - Navigation instantanée (la plus rapide)
    FAST_FADE,      // 120ms - Fade rapide (bon compromis)
    MINIMAL_SLIDE,  // 150ms - Slide subtil
    SCALE           // 140ms - Effet moderne
}

object NavigationConfig {
    /**
     * ⚡ MODIFIER ICI POUR CHANGER LE TYPE DE TRANSITION ⚡
     *
     * Options disponibles:
     * - TransitionType.INSTANT       -> Navigation instantanée (RECOMMANDÉ)
     * - TransitionType.FAST_FADE     -> Fade rapide (bon compromis)
     * - TransitionType.MINIMAL_SLIDE -> Slide minimal
     * - TransitionType.SCALE         -> Effet scale moderne
     */
    val CURRENT_TRANSITION_TYPE = TransitionType.SCALE

    /**
     * Si vous voulez tester, changez simplement la ligne ci-dessus:
     * const val CURRENT_TRANSITION_TYPE = TransitionType.FAST_FADE
     */
}
