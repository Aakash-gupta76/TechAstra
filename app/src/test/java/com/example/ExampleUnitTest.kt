package com.example

import com.example.data.model.UserProfile
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying user profile roles and admin access gating.
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun normalUserIsNotAdmin() {
        val normalUser = UserProfile(
            name = "Aakash Gupta",
            email = "aakasgupta89@gmail.com",
            role = "student"
        )
        assertFalse("Normal student must not have admin access", normalUser.isAdmin)
    }

    @Test
    fun adminUserHasAdminAccess() {
        val adminUserByRole = UserProfile(
            name = "Admin User",
            email = "user@example.com",
            role = "admin"
        )
        assertTrue("User with admin role must have admin access", adminUserByRole.isAdmin)

        val adminUserByEmail = UserProfile(
            name = "Admin User",
            email = "admin@techastra.com",
            role = "student"
        )
        assertTrue("User with admin in email must have admin access", adminUserByEmail.isAdmin)
    }
}

