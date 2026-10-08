package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminStats
import com.example.data.model.DownloadItem
import com.example.data.model.ResourceItem
import com.example.data.model.TechnicalCategory
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.ResourceRepository
import com.example.ui.components.BottomNavTab
import com.example.ui.navigation.Screen
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TechAstraViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val resourceRepository = ResourceRepository(viewModelScope)

    val resources: StateFlow<List<ResourceItem>> = resourceRepository.resources
    val downloads: StateFlow<List<DownloadItem>> = resourceRepository.downloads
    val technicalCategories: List<TechnicalCategory> = resourceRepository.technicalCategories

    // Auth State
    val authState: StateFlow<FirebaseUser?> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.currentUser)

    private val _userProfile = MutableStateFlow(
        UserProfile(
            uid = authRepository.currentUser?.uid ?: "",
            name = authRepository.currentUser?.displayName?.ifBlank { "Student" } ?: "Student",
            email = authRepository.currentUser?.email ?: "",
            role = "student"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Navigation state
    private val backStack = mutableListOf<Screen>(Screen.Splash)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    init {
        // Observe auth changes to update user profile
        viewModelScope.launch {
            authRepository.userProfile.collect { profile ->
                if (profile != null) {
                    _userProfile.value = profile
                } else if (authRepository.currentUser != null) {
                    val user = authRepository.currentUser!!
                    _userProfile.value = UserProfile(
                        uid = user.uid,
                        name = user.displayName ?: "Student",
                        email = user.email ?: "",
                        role = if (user.email?.contains("admin") == true) "admin" else "student"
                    )
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            _currentScreen.value = backStack.last()
            return true
        }
        return false
    }

    fun setBottomTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    // Firebase Auth Operations
    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null && !user.displayName.isNullOrBlank()) {
                    _userProfile.value = _userProfile.value.copy(
                        uid = user.uid,
                        name = user.displayName!!,
                        email = user.email ?: email
                    )
                }
                onSuccess()
            } else {
                val rawMsg = result.exceptionOrNull()?.localizedMessage ?: "Sign in failed"
                val friendlyMsg = when {
                    rawMsg.contains("no user record", ignoreCase = true) ||
                    rawMsg.contains("user-not-found", ignoreCase = true) ->
                        "Yeh account registered nahi hai! Pehle neeche 'Sign Up' karke account banayein."
                    rawMsg.contains("invalid-credential", ignoreCase = true) ||
                    rawMsg.contains("wrong-password", ignoreCase = true) ->
                        "Email ya Password galat hai. Agar naya account hai to pehle Sign Up karein."
                    rawMsg.contains("network", ignoreCase = true) ->
                        "Internet connection error. Kripya connection check karein."
                    else -> rawMsg
                }
                onError(friendlyMsg)
            }
        }
    }

    fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
        branch: String = "Computer Science & Engineering",
        semester: Int = 5,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.signUpWithEmail(name, email, password, branch, semester)
            if (result.isSuccess) {
                val user = result.getOrNull()
                val signedUpName = name.trim().ifBlank { user?.displayName ?: "Student" }
                val newProfile = UserProfile(
                    uid = user?.uid ?: "",
                    name = signedUpName,
                    email = email,
                    role = "student",
                    branch = branch,
                    semester = semester
                )
                _userProfile.value = newProfile
                onSuccess()
            } else {
                val rawMsg = result.exceptionOrNull()?.localizedMessage ?: "Sign up failed"
                val friendlyMsg = when {
                    rawMsg.contains("already in use", ignoreCase = true) ||
                    rawMsg.contains("email-already-in-use", ignoreCase = true) ->
                        "Yeh email address pehle se registered hai! Kripya neeche 'Login' karein."
                    rawMsg.contains("weak-password", ignoreCase = true) ||
                    rawMsg.contains("password should be at least", ignoreCase = true) ->
                        "Password kam se kam 6 characters ka hona chahiye."
                    rawMsg.contains("badly formatted", ignoreCase = true) ||
                    rawMsg.contains("invalid-email", ignoreCase = true) ->
                        "Email address ka format sahi nahi hai. Kripya valid email daalein."
                    rawMsg.contains("network", ignoreCase = true) ->
                        "Network connection error. Kripya apna internet connection check karein."
                    else -> rawMsg
                }
                onError(friendlyMsg)
            }
        }
    }

    fun signInWithGoogle(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(context)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Google sign-in failed")
            }
        }
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to send reset email")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _currentScreen.value = Screen.Login
    }

    // Student Operations
    fun toggleBookmark(resourceId: String) {
        resourceRepository.toggleBookmark(resourceId)
    }

    fun startDownload(resource: ResourceItem) {
        resourceRepository.startDownload(resource)
    }

    fun removeDownload(downloadId: String) {
        resourceRepository.removeDownload(downloadId)
    }

    fun updateUserProfile(name: String, email: String, branch: String, semester: Int) {
        val trimmed = name.trim().ifBlank { "Student" }
        val updated = _userProfile.value.copy(
            name = trimmed,
            email = email,
            branch = branch,
            semester = semester
        )
        _userProfile.value = updated
        viewModelScope.launch {
            authRepository.saveUserProfile(updated)
        }
        resourceRepository.updateUserProfile(trimmed, email, branch, semester)
    }

    // Admin Operations backed by Firestore & Storage
    fun addResource(item: ResourceItem, fileUri: Uri? = null, onProgress: (Float) -> Unit = {}) {
        resourceRepository.addResource(item, fileUri, onProgress)
    }

    fun updateResource(item: ResourceItem) {
        resourceRepository.updateResource(item)
    }

    fun deleteResource(id: String) {
        resourceRepository.deleteResource(id)
    }

    fun getAdminStats(): AdminStats {
        return resourceRepository.getAdminStats()
    }
}
