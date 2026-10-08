package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavTab
import com.example.ui.components.TechBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignupScreen
import com.example.ui.screens.downloads.DownloadsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.pdf.PdfViewerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.resources.ResourceDetailsScreen
import com.example.ui.screens.resources.ResourcesScreen
import com.example.ui.screens.resources.SemesterDetailScreen
import com.example.ui.screens.resources.SubjectDetailScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.TechBackground
import com.example.ui.viewmodel.TechAstraViewModel

@Composable
fun TechAstraApp(
    viewModel: TechAstraViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val resources by viewModel.resources.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val authUser by viewModel.authState.collectAsState()
    val categories = viewModel.technicalCategories

    val activeDownloadsCount = downloads.count { it.status == com.example.data.model.DownloadStatus.DOWNLOADING }
    val bookmarksCount = resources.count { it.isBookmarked }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
    ) {
        when (val screen = currentScreen) {
            is Screen.Splash -> {
                SplashScreen(
                    onTimeout = {
                        if (authUser != null) {
                            viewModel.navigateTo(Screen.Main)
                        } else {
                            viewModel.navigateTo(Screen.Login)
                        }
                    }
                )
            }

            is Screen.Login -> {
                LoginScreen(
                    onLoginClick = { email, password, onError ->
                        viewModel.signInWithEmail(
                            email = email,
                            password = password,
                            onSuccess = { viewModel.navigateTo(Screen.Main) },
                            onError = onError
                        )
                    },
                    onGoogleLoginClick = { onError ->
                        viewModel.signInWithGoogle(
                            context = context,
                            onSuccess = { viewModel.navigateTo(Screen.Main) },
                            onError = onError
                        )
                    },
                    onForgotPasswordClick = { email, onDone, onError ->
                        viewModel.sendPasswordReset(
                            email = email,
                            onSuccess = onDone,
                            onError = onError
                        )
                    },
                    onNavigateToSignup = {
                        viewModel.navigateTo(Screen.Signup)
                    }
                )
            }

            is Screen.Signup -> {
                BackHandler { viewModel.navigateBack() }
                SignupScreen(
                    onSignupClick = { name, email, password, onError ->
                        viewModel.signUpWithEmail(
                            name = name,
                            email = email,
                            password = password,
                            branch = userProfile.branch,
                            semester = userProfile.semester,
                            onSuccess = { viewModel.navigateTo(Screen.Main) },
                            onError = onError
                        )
                    },
                    onGoogleSignupClick = { onError ->
                        viewModel.signInWithGoogle(
                            context = context,
                            onSuccess = { viewModel.navigateTo(Screen.Main) },
                            onError = onError
                        )
                    },
                    onNavigateBackToLogin = {
                        viewModel.navigateBack()
                    }
                )
            }

            is Screen.Main -> {
                BackHandler(enabled = currentTab != BottomNavTab.HOME) {
                    viewModel.setBottomTab(BottomNavTab.HOME)
                }

                Scaffold(
                    containerColor = TechBackground,
                    bottomBar = {
                        TechBottomNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { tab ->
                                viewModel.setBottomTab(tab)
                            },
                            downloadBadgeCount = activeDownloadsCount
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .statusBarsPadding()
                    ) {
                        when (currentTab) {
                            BottomNavTab.HOME -> {
                                HomeScreen(
                                    userProfile = userProfile,
                                    resources = resources,
                                    downloads = downloads,
                                    categories = categories,
                                    onResourceClick = { resId ->
                                        viewModel.navigateTo(Screen.ResourceDetail(resId))
                                    },
                                    onDownloadClick = { res ->
                                        viewModel.startDownload(res)
                                    },
                                    onBookmarkToggle = { resId ->
                                        viewModel.toggleBookmark(resId)
                                    },
                                    onNavigateToSemester = { sem ->
                                        viewModel.navigateTo(Screen.SemesterDetail(sem))
                                    },
                                    onNavigateToCategory = { catId ->
                                        viewModel.navigateTo(Screen.SubjectDetail(catId))
                                    },
                                    onNavigateToDownloads = {
                                        viewModel.setBottomTab(BottomNavTab.DOWNLOADS)
                                    },
                                    onNavigateToExplore = {
                                        viewModel.setBottomTab(BottomNavTab.EXPLORE)
                                    },
                                    onNavigateToProfile = {
                                        viewModel.setBottomTab(BottomNavTab.PROFILE)
                                    }
                                )
                            }

                            BottomNavTab.EXPLORE -> {
                                ResourcesScreen(
                                    resources = resources,
                                    downloads = downloads,
                                    categories = categories,
                                    onResourceClick = { resId ->
                                        viewModel.navigateTo(Screen.ResourceDetail(resId))
                                    },
                                    onDownloadClick = { res ->
                                        viewModel.startDownload(res)
                                    },
                                    onBookmarkToggle = { resId ->
                                        viewModel.toggleBookmark(resId)
                                    },
                                    onCategoryClick = { catId ->
                                        viewModel.navigateTo(Screen.SubjectDetail(catId))
                                    },
                                    onSemesterClick = { sem ->
                                        viewModel.navigateTo(Screen.SemesterDetail(sem))
                                    }
                                )
                            }

                            BottomNavTab.DOWNLOADS -> {
                                DownloadsScreen(
                                    downloads = downloads,
                                    onOpenResource = { resId ->
                                        viewModel.navigateTo(Screen.PdfViewer(resId))
                                    },
                                    onDeleteDownload = { downId ->
                                        viewModel.removeDownload(downId)
                                    },
                                    onNavigateToExplore = {
                                        viewModel.setBottomTab(BottomNavTab.EXPLORE)
                                    }
                                )
                            }

                            BottomNavTab.PROFILE -> {
                                ProfileScreen(
                                    userProfile = userProfile,
                                    downloadsCount = downloads.size,
                                    bookmarksCount = bookmarksCount,
                                    onUpdateProfile = { name, email, branch, sem ->
                                        viewModel.updateUserProfile(name, email, branch, sem)
                                    },
                                    onNavigateToDownloads = {
                                        viewModel.setBottomTab(BottomNavTab.DOWNLOADS)
                                    },
                                    onLogout = {
                                        viewModel.signOut()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            is Screen.SemesterDetail -> {
                BackHandler { viewModel.navigateBack() }
                SemesterDetailScreen(
                    semester = screen.semester,
                    resources = resources,
                    downloads = downloads,
                    onBack = { viewModel.navigateBack() },
                    onResourceClick = { resId ->
                        viewModel.navigateTo(Screen.ResourceDetail(resId))
                    },
                    onDownloadClick = { res ->
                        viewModel.startDownload(res)
                    },
                    onBookmarkToggle = { resId ->
                        viewModel.toggleBookmark(resId)
                    }
                )
            }

            is Screen.SubjectDetail -> {
                BackHandler { viewModel.navigateBack() }
                val category = categories.firstOrNull { it.id == screen.categoryId } ?: categories.first()
                SubjectDetailScreen(
                    category = category,
                    resources = resources,
                    downloads = downloads,
                    onBack = { viewModel.navigateBack() },
                    onResourceClick = { resId ->
                        viewModel.navigateTo(Screen.ResourceDetail(resId))
                    },
                    onDownloadClick = { res ->
                        viewModel.startDownload(res)
                    },
                    onBookmarkToggle = { resId ->
                        viewModel.toggleBookmark(resId)
                    }
                )
            }

            is Screen.ResourceDetail -> {
                BackHandler { viewModel.navigateBack() }
                val resource = resources.firstOrNull { it.id == screen.resourceId } ?: resources.first()
                val downloadItem = downloads.firstOrNull { it.resourceId == resource.id }
                ResourceDetailsScreen(
                    resource = resource,
                    downloadItem = downloadItem,
                    onBack = { viewModel.navigateBack() },
                    onViewPdf = {
                        viewModel.navigateTo(Screen.PdfViewer(resource.id))
                    },
                    onDownload = {
                        viewModel.startDownload(resource)
                    },
                    onBookmarkToggle = {
                        viewModel.toggleBookmark(resource.id)
                    }
                )
            }

            is Screen.PdfViewer -> {
                BackHandler { viewModel.navigateBack() }
                val resource = resources.firstOrNull { it.id == screen.resourceId } ?: resources.first()
                val downloadItem = downloads.firstOrNull { it.resourceId == resource.id }
                PdfViewerScreen(
                    resource = resource,
                    downloadItem = downloadItem,
                    onBack = { viewModel.navigateBack() },
                    onDownload = {
                        viewModel.startDownload(resource)
                    }
                )
            }
        }
    }
}
