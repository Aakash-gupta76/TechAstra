package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Throwable) { null },
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Throwable) { null }
) {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    val authState: Flow<FirebaseUser?> = callbackFlow {
        if (auth == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { currentAuth ->
            trySend(currentAuth.currentUser)
            if (currentAuth.currentUser != null) {
                fetchUserProfile(currentAuth.currentUser!!.uid)
            } else {
                _userProfile.value = null
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    init {
        auth?.currentUser?.let {
            fetchUserProfile(it.uid)
        }
    }

    private fun Context.findActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        val currentAuth = auth ?: return Result.failure(Exception("Firebase Auth not initialized"))
        return try {
            val result = currentAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null after sign in")
            fetchUserProfile(user.uid)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
        branch: String = "Computer Science & Engineering",
        semester: Int = 5
    ): Result<FirebaseUser> {
        val currentAuth = auth ?: return Result.failure(Exception("Firebase Auth not initialized"))
        return try {
            val result = currentAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")

            val trimmedName = name.trim().ifBlank { "Student" }
            val profileUpdates = userProfileChangeRequest {
                displayName = trimmedName
            }
            user.updateProfile(profileUpdates).await()

            val profile = UserProfile(
                uid = user.uid,
                name = trimmedName,
                email = email,
                role = "student",
                branch = branch,
                semester = semester
            )
            saveUserProfile(profile)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        val currentAuth = auth ?: return Result.failure(Exception("Firebase Auth not initialized"))
        val targetActivity = context.findActivity() ?: context

        return try {
            val credentialManager = CredentialManager.create(targetActivity)
            val webClientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                "322326063628-6pvtmh03v9mu285k0gau7c5fehk39rhk.apps.googleusercontent.com"
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = targetActivity
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = currentAuth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw Exception("User is null after Google sign-in")

                val immediateProfile = UserProfile(
                    uid = user.uid,
                    name = user.displayName?.ifBlank { null } ?: "Student",
                    email = user.email ?: "",
                    role = "student",
                    branch = "Computer Science & Engineering",
                    semester = 5
                )
                _userProfile.value = immediateProfile

                if (firestore != null) {
                    val existing = firestore.collection("users").document(user.uid).get().await()
                    if (!existing.exists()) {
                        saveUserProfile(immediateProfile)
                    } else {
                        fetchUserProfile(user.uid)
                    }
                }

                Result.success(user)
            } else {
                Result.failure(Exception("Unsupported credential type"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google Sign-In cancel kar diya gaya."))
        } catch (e: NoCredentialException) {
            Result.failure(Exception("Device me koi Google account logged in nahi hai ya Firebase me SHA-1 fingerprint add nahi hai.\n\nAap direct Email & Password se Sign Up aur Login kar sakte hain!"))
        } catch (e: Exception) {
            val rawMsg = e.localizedMessage ?: e.message ?: "Google sign-in error"
            val errorHint = when {
                rawMsg.contains("10") || rawMsg.contains("DEVELOPER_ERROR", ignoreCase = true) || rawMsg.contains("Caller not authorized", ignoreCase = true) ->
                    "Google Sign-In (Code 10 / DEVELOPER_ERROR): Firebase Console me SHA-1 fingerprint add nahi hai ya abhi sync ho raha hai.\n\nSHA-1: 9D:35:F2:9E:7F:EA:5E:02:7D:BE:E4:1F:D6:9C:C1:D8:06:D9:53:34\n\nFirebase Console -> Project Settings -> Your Apps -> Add Fingerprint me ye SHA-1 daalein."
                rawMsg.contains("16") || rawMsg.contains("Cannot find a matching credential", ignoreCase = true) ->
                    "Device/Emulator me koi Google account logged in nahi hai. Browser emulator me Google account nahi hota; APK apne real phone me daal kar test karein."
                rawMsg.contains("12500") ->
                    "Google Sign-In config error (12500): Firebase Console -> Authentication -> Sign-in method me Google provider enable karein aur Project Support Email select karein."
                rawMsg.contains("No credential", ignoreCase = true) ->
                    "Device me Google Account nahi mila ya Firebase me SHA-1 missing hai."
                else -> rawMsg
            }
            Result.failure(Exception("$errorHint\n(Technical: $rawMsg)"))
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val currentAuth = auth ?: return Result.failure(Exception("Firebase Auth not initialized"))
        return try {
            currentAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _userProfile.value = null
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val currentFirestore = firestore ?: return Result.success(Unit)
        return try {
            val data = mapOf(
                "uid" to profile.uid,
                "name" to profile.name,
                "email" to profile.email,
                "role" to profile.role,
                "branch" to profile.branch,
                "semester" to profile.semester,
                "college" to profile.college
            )
            currentFirestore.collection("users").document(profile.uid)
                .set(data, SetOptions.merge())
                .await()
            _userProfile.value = profile
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun fetchUserProfile(uid: String) {
        val currentFirestore = firestore
        if (currentFirestore == null) {
            val current = auth?.currentUser
            if (current != null) {
                _userProfile.value = UserProfile(
                    uid = current.uid,
                    name = current.displayName?.ifBlank { null } ?: "Student",
                    email = current.email ?: "",
                    role = "student"
                )
            }
            return
        }
        currentFirestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    val current = auth?.currentUser
                    if (current != null) {
                        _userProfile.value = UserProfile(
                            uid = current.uid,
                            name = current.displayName?.ifBlank { null } ?: "Student",
                            email = current.email ?: "",
                            role = "student"
                        )
                    }
                    return@addSnapshotListener
                }

                val profile = UserProfile(
                    uid = snapshot.getString("uid") ?: uid,
                    name = snapshot.getString("name")?.ifBlank { null }
                        ?: auth?.currentUser?.displayName?.ifBlank { null } ?: "Student",
                    email = snapshot.getString("email") ?: auth?.currentUser?.email ?: "",
                    role = snapshot.getString("role") ?: "student",
                    branch = snapshot.getString("branch") ?: "Computer Science & Engineering",
                    semester = (snapshot.getLong("semester") ?: 5L).toInt(),
                    college = snapshot.getString("college") ?: "Techno Institute of Technology"
                )
                _userProfile.value = profile
            }
    }
}
