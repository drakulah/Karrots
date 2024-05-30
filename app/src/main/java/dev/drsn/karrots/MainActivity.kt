package dev.drsn.karrots

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import dev.drsn.karrots.audio.AudioPlayer
import dev.drsn.karrots.audio.service.PlayerService
import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.loader.ContentLoader
import dev.drsn.karrots.loader.ExploreLoader
import dev.drsn.karrots.ui.component.BottomModalSheet
import dev.drsn.karrots.ui.component.Layer
import dev.drsn.karrots.ui.component.ModalBottomSheetState
import dev.drsn.karrots.ui.component.dismissedAnchor
import dev.drsn.karrots.ui.component.rememberModalBottomSheetState
import dev.drsn.karrots.ui.router.ScreenRouter
import dev.drsn.karrots.ui.theme.AppTheme
import dev.drsn.karrots.ui.theme.LocalTheme
import dev.drsn.karrots.ui.theme.Theme
import dev.drsn.karrots.ui.theme.darkScheme
import dev.drsn.karrots.ui.theme.lightScheme
import dev.drsn.karrots.ui.theme.typography

class MainActivity : ComponentActivity() {
	private var player by mutableStateOf<AudioPlayer?>(null)
	private var innertube by mutableStateOf<Innertube?>(null)

	private val serviceConnection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			val binder = service as PlayerService.PlayerServiceBinder
			player = binder.getPlayer()
			innertube = binder.getInnertube()
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			player = null
			innertube = null
		}
	}

	@androidx.annotation.OptIn(UnstableApi::class)
	@OptIn(ExperimentalFoundationApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		/** Initialize & start player service **/
		val playerServiceIntent = Intent(this, PlayerService::class.java)
		bindService(playerServiceIntent, serviceConnection, BIND_AUTO_CREATE)
		startService(playerServiceIntent)

//		definedProcessor.roboticProcessor.setPhaseIncrement(RoboticProcessor.MAX_PHASE_INC_LEVEL)
//

//		val exoPlayer = ExoPlayer.Builder(this)
//			.setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
//			.setRenderersFactory(renderFactory)
//			.build()
//
//		val player = AudioPlayer(exoPlayer, definedProcessor)

		enableEdgeToEdge(
			navigationBarStyle = SystemBarStyle.auto(
				android.graphics.Color.TRANSPARENT,
				android.graphics.Color.TRANSPARENT,
			)
		)

		setContent {
			val context = LocalContext.current
			val config = LocalConfiguration.current
			val pageRouter = rememberNavController()
			val screenRouter = rememberNavController()
			val darkTheme = isSystemInDarkTheme()
			val enableDynamicColor by remember { mutableStateOf(true) }
			val colorScheme by remember {
				mutableStateOf(
					when {
						enableDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
							if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
						}

						darkTheme -> darkScheme
						else -> lightScheme
					}
				)
			}
			val themeState by remember { mutableStateOf(Theme(colorScheme, typography)) }
			val bottomModalSheet = rememberModalBottomSheetState(
				dismissedBound = 0.dp,
				expandedBound = config.screenHeightDp.dp,
				initialAnchor = dismissedAnchor
			)

			if (innertube != null && player != null) {
				val contentLoader by remember {
					mutableStateOf(
						ContentLoader(
							explore = ExploreLoader(innertube!!)
						)
					)
				}

				CompositionLocalProvider(
					LocalTheme provides themeState,
					LocalInnertube provides innertube!!,
					LocalPlayer provides player!!,
					LocalContentLoader provides contentLoader,
					LocalPageRouter provides pageRouter,
					LocalScreenRouter provides screenRouter,
					LocalBottomModalSheet provides bottomModalSheet
				) {

					val theme = LocalTheme.current
					val sheetState = LocalBottomModalSheet.current

					AppTheme(
						theme = theme
					) {

						Layer(
							modifier = Modifier
								.fillMaxSize()
								.background(theme.colorScheme.background),
						) {
							ScreenRouter()
						}

						BottomModalSheet(sheetState)
					}
				}
			} else {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(Color.White),
					contentAlignment = Alignment.Center
				) {

					AsyncImage(
						model = R.mipmap.ic_launcher_foreground,
						contentDescription = "App Icon"
					)
				}
			}
		}
	}
}

val LocalInnertube = staticCompositionLocalOf<Innertube> { error("Innertube not initialized") }
val LocalPlayer = staticCompositionLocalOf<AudioPlayer> { error("Player not initialized") }
val LocalContentLoader =
	staticCompositionLocalOf<ContentLoader> { error("Content loader not initialized") }
val LocalPageRouter =
	staticCompositionLocalOf<NavHostController> { error("Page router not initialized") }
val LocalScreenRouter =
	staticCompositionLocalOf<NavHostController> { error("Screen router not initialized") }
val LocalBottomModalSheet =
	staticCompositionLocalOf<ModalBottomSheetState> { error("Bottom sheet not initialized") }