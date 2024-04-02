package com.example.trytolie.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.trytolie.R


object TryToLieRoute {
    const val HOME = "Home"
    const val PROFILE = "Profile"
    const val SIGN_IN = "Sign In"
    const val SIGN_UP = "Sign Up"
    const val CONTACT = "Contact Us"
    const val FIND_GAME = "Multiplayer Game"
    const val ONLINE_GAME = "Online Game"
    const val SELECT_COLOR = "Select Color"
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
        route = TryToLieRoute.CONTACT,
        selectedIcon = Icons.Default.Email,
        unselectedIcon = Icons.Outlined.Email,
        iconTextId = R.string.tab_contacts
    )
)

