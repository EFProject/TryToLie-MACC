package com.example.trytolie

import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.example.trytolie.game.ui.app.GameOrchestrator
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.game.GameViewModel
import com.example.trytolie.multiplayer.room.RoomUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInState
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.navigation.ModalNavigationDrawerContent
import com.example.trytolie.ui.navigation.TryToLieBottomNavigationBar
import com.example.trytolie.ui.navigation.TryToLieNavigationActions
import com.example.trytolie.ui.navigation.TryToLieNavigationRail
import com.example.trytolie.ui.navigation.TryToLieRoute
import com.example.trytolie.ui.navigation.TryToLieTopLevelDestination
import com.example.trytolie.ui.pages.authenticated.HomePage
import com.example.trytolie.ui.pages.authenticated.MatchHistory
import com.example.trytolie.ui.pages.authenticated.ProfileScreen
import com.example.trytolie.ui.pages.game.FindGameScreen
import com.example.trytolie.ui.pages.guest.HomePageGuest
import com.example.trytolie.ui.pages.guest.ProfileScreenGuest
import com.example.trytolie.ui.pages.unauthenticated.InfoScreen
import com.example.trytolie.ui.pages.unauthenticated.PasswordResetScreen
import com.example.trytolie.ui.pages.unauthenticated.SignInScreen
import com.example.trytolie.ui.pages.unauthenticated.SignUpScreen
import com.example.trytolie.ui.utils.DevicePosture
import com.example.trytolie.ui.utils.TryToLieNavigationContentPosition
import com.example.trytolie.ui.utils.TryToLieNavigationType
import com.example.trytolie.ui.utils.isBookPosture
import com.example.trytolie.ui.utils.isSeparating
import kotlinx.coroutines.launch

@Composable
fun TryToLieApp(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    isAuthenticated: Boolean,
    authState: SignInState? = null,
    authHandler: AuthUIClient?,
    authViewModel:  SignInViewModel? = null,
    roomViewModel: RoomViewModel? = null,
    gameViewModel: GameViewModel? = null,
    googleIntentLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? = null,
    roomUIClient: RoomUIClient? = null,
    gameUIClient: GameUIClient? = null,
    immersivePage: String? = null,
    userData: UserData? = null,
    context: Context? = null
) {
    val navigationType: TryToLieNavigationType

    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) ->
            DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) ->
            DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

        else -> DevicePosture.NormalPosture
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = TryToLieNavigationType.BOTTOM_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = TryToLieNavigationType.NAVIGATION_RAIL
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                TryToLieNavigationType.NAVIGATION_RAIL
            } else {
                TryToLieNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }
        else -> {
            navigationType = TryToLieNavigationType.BOTTOM_NAVIGATION
        }
    }
    val navigationContentPosition = when (windowSize.heightSizeClass) {
        WindowHeightSizeClass.Compact -> {
            TryToLieNavigationContentPosition.TOP
        }
        WindowHeightSizeClass.Medium,
        WindowHeightSizeClass.Expanded -> {
            TryToLieNavigationContentPosition.CENTER
        }
        else -> {
            TryToLieNavigationContentPosition.TOP
        }
    }

    when (immersivePage) {
        TryToLieRoute.FIND_GAME -> FindGameScreen(
            modifier = Modifier,
            gameUIClient = gameUIClient!!,
            roomUIClient = roomUIClient!!,
            roomViewModel = roomViewModel!!,
            authViewModel = authViewModel,
            userData = userData!!
        )
        TryToLieRoute.ONLINE_GAME -> Surface(color = MaterialTheme.colorScheme.background) {
            Log.d("GAME", gameViewModel!!.getGameData().toString())
            GameOrchestrator(
                gameViewModel = gameViewModel,
                roomViewModel = roomViewModel!!,
                gameUIClient = gameUIClient,
                userData = userData,
            )
        }
        else -> TryToLieNavigationWrapper(
            navigationType = navigationType,
            navigationContentPosition = navigationContentPosition,
            isAuthenticated = isAuthenticated,
            authState = authState,
            authHandler = authHandler,
            authViewModel = authViewModel,
            roomViewModel= roomViewModel,
            gameUIClient = gameUIClient,
            googleIntentLauncher = googleIntentLauncher,
            context = context
        )
    }

}

@Composable
private fun TryToLieNavigationWrapper(
    navigationType: TryToLieNavigationType,
    navigationContentPosition: TryToLieNavigationContentPosition,
    isAuthenticated: Boolean,
    authState: SignInState? = null,
    authHandler: AuthUIClient? = null,
    authViewModel: SignInViewModel? = null,
    roomViewModel: RoomViewModel? = null,
    gameUIClient: GameUIClient? = null,
    googleIntentLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? = null,
    context: Context? = null
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        TryToLieNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: TryToLieRoute.HOME

    ModalNavigationDrawer(
        drawerContent = {
            ModalNavigationDrawerContent(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.close()
                    } },
                isAuthenticate = isAuthenticated
            ) },
        drawerState = drawerState
    ) {
        TryToLieAppContent(
            navigationType = navigationType,
            navigationContentPosition = navigationContentPosition,
            navController = navController,
            selectedDestination = selectedDestination,
            authState= authState,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            onDrawerClicked = {
                scope.launch {
                    drawerState.open()
                } },
            isAuthenticated = isAuthenticated,
            authViewModel = authViewModel,
            roomViewModel= roomViewModel,
            gameUIClient = gameUIClient,
            authHandler = authHandler,
            googleIntentLauncher = googleIntentLauncher,
            context = context
        )
    }
}

@Composable
fun TryToLieAppContent(
    modifier: Modifier = Modifier,
    navigationType: TryToLieNavigationType,
    navigationContentPosition: TryToLieNavigationContentPosition,
    navController: NavHostController,
    selectedDestination: String,
    authState: SignInState? = null,
    navigateToTopLevelDestination: (TryToLieTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
    isAuthenticated: Boolean,
    authViewModel: SignInViewModel? = null,
    roomViewModel: RoomViewModel? = null,
    gameUIClient: GameUIClient? = null,
    authHandler: AuthUIClient? = null,
    googleIntentLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? = null,
    context: Context? = null
) {
    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == TryToLieNavigationType.NAVIGATION_RAIL) {
            TryToLieNavigationRail(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
                isAuthenticate = isAuthenticated
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            TryToLieNavHost(
                modifier = Modifier.weight(1f),
                navController = navController,
                authState = authState,
                isAuthenticated = isAuthenticated,
                authHandler = authHandler,
                authViewModel = authViewModel,
                roomViewModel = roomViewModel,
                gameUIClient = gameUIClient,
                googleIntentLauncher = googleIntentLauncher,
                context = context
            )
            AnimatedVisibility(visible = navigationType == TryToLieNavigationType.BOTTOM_NAVIGATION) {
                TryToLieBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination,
                    isAuthenticated = isAuthenticated
                )
            }
        }
    }
}

@Composable
private fun TryToLieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authState: SignInState? = null,
    isAuthenticated: Boolean,
    authHandler: AuthUIClient? = null,
    authViewModel: SignInViewModel? = null,
    roomViewModel: RoomViewModel? = null,
    gameUIClient: GameUIClient? = null,
    googleIntentLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? = null,
    context: Context? = null
) {
    if (isAuthenticated && authViewModel?.isGuest() != true) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = TryToLieRoute.HOME,
        ) {
            composable(TryToLieRoute.HOME) {
                HomePage(
                    modifier = modifier,
                    authViewModel = authViewModel,
                    roomViewModel = roomViewModel!!,
                    gameUIClient = gameUIClient!!
                )
            }
            composable(TryToLieRoute.HISTORY) {
                MatchHistory(
                    gameUIClient = gameUIClient!!,
                    authViewModel = authViewModel
                )
            }
            composable(TryToLieRoute.PROFILE) {
                ProfileScreen(
                    modifier= modifier,
                    authHandler = authHandler,
                    authViewModel = authViewModel
                )
            }
        }
    } else if (authViewModel?.isGuest() != true) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = TryToLieRoute.SIGN_IN,
        ) {
            composable(TryToLieRoute.SIGN_IN) {
                SignInScreen(
                    state = authState,
                    modifier = modifier,
                    authHandler = authHandler,
                    authViewModel = authViewModel,
                    googleIntentLauncher = googleIntentLauncher,
                    navController = navController,
                    context = context
                )
            }
            composable(TryToLieRoute.SIGN_UP) {
                SignUpScreen(
                    state = authState,
                    modifier = modifier,
                    authHandler = authHandler,
                    authViewModel = authViewModel,
                    context = context
                )
            }
            composable(TryToLieRoute.PASSWORD_RESET) {
                PasswordResetScreen(
                    state = authState,
                    modifier = modifier,
                    authHandler = authHandler
                )
            }
            composable(TryToLieRoute.INFO) {
                InfoScreen(
                    modifier = modifier
                )
            }
        }
    } else {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = TryToLieRoute.HOME,
        ) {
            composable(TryToLieRoute.HOME) {
                HomePageGuest(
                    modifier = modifier,
                    authViewModel = authViewModel,
                    roomViewModel = roomViewModel!!,
                    gameUIClient = gameUIClient!!
                )
            }
            composable(TryToLieRoute.HISTORY) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "You must be registered to see the match history",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        onClick = {
                            authViewModel.signOutFromGuest()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "Exit icon",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Back to Login", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            composable(TryToLieRoute.PROFILE) {
                ProfileScreenGuest(
                    modifier= modifier,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
