package com.example

import com.example.data.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthFlowTest {

    @Test
    fun testUnregisteredUserCannotLoginWithoutFirebaseAccount() = runTest {
        // Without an active user session or real Firebase network account,
        // attempting to sign in must fail and return an error result
        val authRepo = AuthRepository(auth = null, firestore = null)

        val result = authRepo.signInWithEmail("unknown_user@example.com", "randompass123")
        assertTrue("Unregistered user should fail login", result.isFailure)
        assertNotNull("Should contain error message", result.exceptionOrNull())
    }

    @Test
    fun testSignOutClearsSession() = runTest {
        val authRepo = AuthRepository(auth = null, firestore = null)
        authRepo.signOut()
        assertNull("User profile should be null after sign out", authRepo.userProfile.value)
    }
}
