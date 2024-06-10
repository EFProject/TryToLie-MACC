package com.example.trytolie.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.trytolie.R


object TryToLieRoute {
    const val HOME = "Home"
    const val HISTORY = "Match History"
    const val PROFILE = "Profile"
    const val SIGN_IN = "Sign In"
    const val SIGN_UP = "Sign Up"
    const val PASSWORD_RESET = "Password Reset"
    const val INFO = "Info"
    const val FIND_GAME = "Multiplayer Game"
    const val ONLINE_GAME = "Online Game"
}

data class TryToLieTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class TryToLieNavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: TryToLieTopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    TryToLieTopLevelDestination(
        route = TryToLieRoute.HOME,
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.tab_home
    ),
    TryToLieTopLevelDestination(
        route = TryToLieRoute.HISTORY,
        selectedIcon = Icons.Default.History,
        unselectedIcon = Icons.Outlined.History,
        iconTextId = R.string.tab_history
    ),
    TryToLieTopLevelDestination(
        route = TryToLieRoute.PROFILE,
        selectedIcon = Icons.Default.Person,
        unselectedIcon = Icons.Outlined.Person,
        iconTextId = R.string.profile
    )
)

val LOGIN_LEVEL_DESTINATIONS = listOf(
    TryToLieTopLevelDestination(
        route = TryToLieRoute.SIGN_IN,
        selectedIcon = Icons.AutoMirrored.Filled.Login,
        unselectedIcon = Icons.AutoMirrored.Outlined.Login,
        iconTextId = R.string.tab_sign_in
    ),
    TryToLieTopLevelDestination(
        route = TryToLieRoute.SIGN_UP,
        selectedIcon = Icons.Default.AppRegistration,
        unselectedIcon = Icons.Outlined.AppRegistration,
        iconTextId = R.string.tab_sign_up
    ),
    TryToLieTopLevelDestination(
        route = TryToLieRoute.INFO,
        selectedIcon = Icons.Default.Info,
        unselectedIcon = Icons.Outlined.Info,
        iconTextId = R.string.tab_info
    )
)

