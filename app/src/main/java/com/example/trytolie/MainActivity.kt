package com.example.trytolie
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserAuthStateType
import com.example.trytolie.ui.components.IndeterminateLoaderIndicator
import com.example.trytolie.ui.navigation.ExitApplicationComponent
import com.example.trytolie.ui.theme.TryToLieTheme
import com.example.trytolie.ui.utils.graphics.CubeRendererOpenGL
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {

    private val signInViewModel: SignInViewModel by viewModels()

    private var userAuthState =  mutableStateOf(UserAuthStateType.UNDEFINED)
    // private val db = Firebase.firestore
    private var loadingText = "Setting up the game..."


    private var glSurfaceView: GLSurfaceView? = null
    private var renderer: CubeRendererOpenGL? = null

    private val authUIClient by lazy {
        AuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            loginToogle = { userAuthState.value = UserAuthStateType.UNDEFINED},
            loadingText = {s : String -> loadingText = s},
            signInViewModel = signInViewModel
        )
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
        }
    }


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin()

        //handle asynchronous tasks - observer to monitor the authentication state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signInViewModel.getAuthenticationState(handler = authUIClient).run {
                    signInViewModel.isAuthenticated.collect {  userAuthState.value = it.state }
                }
            }
        }

        setContent {
            TryToLieTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)
                val authState by signInViewModel.signInState.collectAsStateWithLifecycle()
                val userData by signInViewModel.userData.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = userData) {
                    signInViewModel.getAuthenticationState(handler = authUIClient).run {
                        signInViewModel.isAuthenticated.collect { userAuthState.value = it.state }
                    }
                }

                ExitApplicationComponent(this)

                when (userAuthState.value) {
                    UserAuthStateType.UNDEFINED -> {
                        if (checkOpenGL3()) {
                            glSurfaceView = GLSurfaceView(this)
                            glSurfaceView?.setEGLContextClientVersion(3)

                            renderer = CubeRendererOpenGL(this,MaterialTheme.colorScheme.inverseOnSurface)
                            glSurfaceView?.setRenderer(renderer)

                            IndeterminateLoaderIndicator(loadingText = loadingText, drawing = glSurfaceView!!)
                        } else {
                            finish()
                        }
                    }

                    UserAuthStateType.UNAUTHENTICATED -> {
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = authUIClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        signInViewModel.onSignInResult(
                                            signInResult,
                                            applicationContext
                                        )
                                    }
                                }
                            }
                        )

                        TryToLieApp(
                            windowSize = windowSize,
                            displayFeatures = displayFeatures,
                            isAuthenticated = false,
                            authViewModel = signInViewModel,
                            authHandler = authUIClient,
                            authState= authState,
                            googleIntentLauncher = launcher,
                            context = applicationContext
                        )
                    }

                    UserAuthStateType.AUTHENTICATED -> {

                    }

                    UserAuthStateType.GUEST -> {

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }


    // Check if OpenGL ES 3.0 is supported
    private fun checkOpenGL3(): Boolean {

        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000

    }

}


