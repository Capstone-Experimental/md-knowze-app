package com.knowzeteam.knowze

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.knowzeteam.knowze.data.local.AboutContentData
import com.knowzeteam.knowze.ui.navigation.Screen
import com.knowzeteam.knowze.ui.screen.auth.login.LoginScreen
import com.knowzeteam.knowze.ui.screen.auth.login.LoginViewModel
import com.knowzeteam.knowze.ui.screen.auth.login.LoginWithEmailScreen
import com.knowzeteam.knowze.ui.screen.auth.register.RegisterScreen
import com.knowzeteam.knowze.ui.screen.detailcourse.AboutContentScreen
import com.knowzeteam.knowze.ui.screen.detailcourse.AboutCourseScreen
import com.knowzeteam.knowze.ui.screen.detailcourse.DetailCourseScreen
import com.knowzeteam.knowze.ui.screen.detailcourse.YoutubeScreen
import com.knowzeteam.knowze.ui.screen.gallery.CourseGallery
import com.knowzeteam.knowze.ui.screen.home.HomeScreen
import com.knowzeteam.knowze.ui.screen.home.HomeSearch
import com.knowzeteam.knowze.ui.screen.keyword.TrendingKeywordScreen
import com.knowzeteam.knowze.ui.screen.welcome.IntroOneScreen
import com.knowzeteam.knowze.ui.screen.welcome.IntroSecondScreen
import com.knowzeteam.knowze.ui.screen.welcome.IntroThridScreen
import com.knowzeteam.knowze.ui.screen.welcome.SplashScreen


@Composable
fun KnowzeApp(viewModelFactory: ViewModelProvider.Factory) {

    val gson = Gson()

    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val startDestination = if (firebaseAuth.currentUser != null) Screen.Home.route else Screen.Splash.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Onboard1.route) {
            IntroOneScreen(
                onNextClick = { navController.navigate(Screen.Onboard2.route) },
                onSkipClick = { navController.navigate(Screen.Login.route) },
            )
        }
        composable(Screen.Onboard2.route) {
            IntroSecondScreen(
                onNextClick = { navController.navigate(Screen.Onboard3.route) },
                onSkipClick = { navController.navigate(Screen.Login.route) },
            )
        }
        composable(Screen.Onboard3.route){
            IntroThridScreen(
                onNextClick = { navController.navigate(Screen.Login.route) },
                onSkipClick = { navController.navigate(Screen.Login.route) },
            )
        }

        composable(Screen.Login.route) {

            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        composable(Screen.EmailLogin.route) {
            LoginWithEmailScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            // Create the RegisterScreen composable and pass the navController
            RegisterScreen(navController = navController)
        }

        composable(Screen.Home.route){
            HomeScreen(
                navController = navController,
                viewModel = loginViewModel,
            )
        }

        composable(
            "${Screen.HomeS.route}/{focus}",
            arguments = listOf(navArgument("focus") { defaultValue = "nofocus"; type = NavType.StringType })
        ) { backStackEntry ->
            val focus = backStackEntry.arguments?.getString("focus") ?: "nofocus"
            HomeSearch(
                navController= navController,
                onBack = { navController.popBackStack() },
                initialSearchText = "",
                shouldFocus = focus == "focus",
            )
        }

        composable(
            route = "${Screen.AboutCourse.route}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract the courseId from the backStackEntry
            val courseId = backStackEntry.arguments?.getString("courseId") ?: "default_course_id"

            AboutCourseScreen(
                courseId = courseId,
                navController = navController,
            )
        }

        composable(
            route = "${Screen.AboutContent.route}/{course}",
            arguments = listOf(navArgument("course") { type = NavType.StringType })
        ) { backStackEntry ->

            val courseJson = backStackEntry.arguments?.getString("course")
            val course = gson.fromJson(courseJson, AboutContentData::class.java)

            if (courseJson != null) {
                AboutContentScreen(
                    course = course,
                    navController = navController,
                )
            }
        }

        composable(
            route = "${Screen.DetailCourse.route}/{courseId}/{subtitleId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("subtitleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val subtitleId = backStackEntry.arguments?.getString("subtitleId") ?: ""

            DetailCourseScreen(
                courseId = courseId,
                subtitleId = subtitleId,
                navController = navController
            )
        }

        composable(Screen.TrendingKeyword.route) {
            TrendingKeywordScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.CourseGallery.route){
            CourseGallery(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "${Screen.Youtube.route}/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            YoutubeScreen(videoId = backStackEntry.arguments?.getString("videoId") ?: "", navController)
        }
    }
}