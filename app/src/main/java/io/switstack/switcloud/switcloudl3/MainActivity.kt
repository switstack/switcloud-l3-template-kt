package io.switstack.switcloud.switcloudl3

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.switstack.switcloud.switcloudclt.domain.SwitcloudClt
import io.switstack.switcloud.switcloudl3.ui.about.AboutScreen
import io.switstack.switcloud.switcloudl3.ui.home.HomeScreen
import io.switstack.switcloud.switcloudl3.ui.theme.Switcloudl3Theme

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Switcloudl3Theme {
                AppNavigation()
            }
        }

        SwitcloudClt.setActivity(this)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(Routes.ABOUT) {
            AboutScreen(navController)
        }
    }
}