package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.repository.AniListRepository
import com.example.ui.screens.AiringScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ThemePreferences
import com.example.ui.viewmodel.AiringViewModel
import com.example.ui.viewmodel.AiringViewModelFactory
import com.example.ui.viewmodel.DetailViewModel
import com.example.ui.viewmodel.DetailViewModelFactory
import com.example.ui.viewmodel.ExploreViewModel
import com.example.ui.viewmodel.ExploreViewModelFactory
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.LibraryViewModelFactory
import com.example.ui.viewmodel.SearchViewModel
import com.example.ui.viewmodel.SearchViewModelFactory

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Explore : Screen("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Search : Screen("search?sort={sort}", "Search", Icons.Filled.Search, Icons.Outlined.Search) {
        fun createRoute(sort: String? = null) = if (sort != null) "search?sort=$sort" else "search"
    }
    object Airing : Screen("airing", "Airing", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Library : Screen("library", "Library", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Detail : Screen("detail/{mediaId}", "Detail", Icons.Filled.Explore, Icons.Outlined.Explore) {
        fun createRoute(mediaId: Int) = "detail/$mediaId"
    }
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val bottomNavItems = listOf(
    Screen.Explore,
    Screen.Search,
    Screen.Airing,
    Screen.Library
)

@Composable
fun AniChanApp(
    repository: AniListRepository,
    themePreferences: ThemePreferences,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = currentRoute in listOf(
        Screen.Explore.route,
        Screen.Search.route,
        "search",
        "search?sort={sort}",
        Screen.Airing.route,
        Screen.Library.route
    )

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = when (screen) {
                            Screen.Explore -> currentRoute == Screen.Explore.route
                            Screen.Search -> currentRoute?.startsWith("search") == true
                            Screen.Airing -> currentRoute == Screen.Airing.route
                            Screen.Library -> currentRoute == Screen.Library.route
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (screen == Screen.Explore) {
                                    if (currentRoute != Screen.Explore.route) {
                                        navController.navigate(Screen.Explore.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = false
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                } else {
                                    if (!isSelected) {
                                        val targetRoute = if (screen == Screen.Search) Screen.Search.createRoute(null) else screen.route
                                        navController.navigate(targetRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.title.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Explore.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Explore.route) {
                val exploreViewModel: ExploreViewModel = viewModel(
                    factory = ExploreViewModelFactory(repository)
                )
                ExploreScreen(
                    viewModel = exploreViewModel,
                    onNavigateToDetail = { mediaId ->
                        navController.navigate(Screen.Detail.createRoute(mediaId))
                    },
                    onNavigateToSearch = { sort ->
                        val targetRoute = Screen.Search.createRoute(sort)
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(
                route = Screen.Search.route,
                arguments = listOf(navArgument("sort") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val sortArg = backStackEntry.arguments?.getString("sort")
                val searchViewModel: SearchViewModel = viewModel(
                    factory = SearchViewModelFactory(repository)
                )
                if (sortArg != null) {
                    searchViewModel.selectSort(sortArg)
                }
                SearchScreen(
                    viewModel = searchViewModel,
                    onNavigateToDetail = { mediaId ->
                        navController.navigate(Screen.Detail.createRoute(mediaId))
                    },
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Explore.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Airing.route) {
                val airingViewModel: AiringViewModel = viewModel(
                    factory = AiringViewModelFactory(repository)
                )
                AiringScreen(
                    viewModel = airingViewModel,
                    onNavigateToDetail = { mediaId ->
                        navController.navigate(Screen.Detail.createRoute(mediaId))
                    }
                )
            }

            composable(Screen.Library.route) {
                val libraryViewModel: LibraryViewModel = viewModel(
                    factory = LibraryViewModelFactory(repository)
                )
                LibraryScreen(
                    viewModel = libraryViewModel,
                    onNavigateToDetail = { mediaId ->
                        navController.navigate(Screen.Detail.createRoute(mediaId))
                    },
                    onNavigateToExplore = {
                        navController.navigate(Screen.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getInt("mediaId") ?: 1
                val detailViewModel: DetailViewModel = viewModel(
                    factory = DetailViewModelFactory(repository, mediaId)
                )
                DetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMedia = { newMediaId ->
                        navController.navigate(Screen.Detail.createRoute(newMediaId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    themePreferences = themePreferences,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
