package tel.jeelpa.falsecaller.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle

class SlideTransitions(
    private val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
): NavHostAnimatedDestinationStyle() {
    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = animationSpec
        )
    }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =  {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = animationSpec
        )
    }


    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = animationSpec
        )
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = animationSpec
        )
    }
}
