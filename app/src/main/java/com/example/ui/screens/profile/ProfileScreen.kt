package com.example.ui.screens.profile

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobManager
import com.example.data.model.UserProfile
import com.example.ui.components.ConfirmationDialog
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    downloadsCount: Int,
    bookmarksCount: Int,
    onUpdateProfile: (name: String, email: String, branch: String, semester: Int) -> Unit,
    onNavigateToDownloads: () -> Unit,
    onLogout: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val displayName = remember(userProfile.name) {
        userProfile.name.trim().ifBlank { "Student" }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .testTag("profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechTextPrimary
                    )
                }
            }

            // User Info Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(20.dp)),
                    color = TechSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(TechSurfaceVariant)
                                .border(2.5.dp, TechAstraGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.take(1).uppercase(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechAstraGold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechTextPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = userProfile.email,
                            fontSize = 13.sp,
                            color = TechTextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val cleanBranch = userProfile.branch.trim().let {
                            if (it.equals("Computer Science & Engineering", ignoreCase = true) ||
                                it.equals("Computer Science", ignoreCase = true)
                            ) "" else it
                        }
                        if (cleanBranch.isNotBlank()) {
                            Text(
                                text = cleanBranch,
                                fontSize = 12.sp,
                                color = TechAstraGold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TechSurfaceVariant),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = TechAstraGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Edit Profile", fontSize = 12.sp, color = TechTextPrimary)
                        }
                    }
                }
            }

            // Stats Cards Row
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatTile(
                        count = "$downloadsCount",
                        label = "Downloads",
                        accentColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatTile(
                        count = "10",
                        label = "Subjects",
                        accentColor = AccentPurple,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatTile(
                        count = "$bookmarksCount",
                        label = "Bookmarks",
                        accentColor = AccentPink,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Resources Section
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "RESOURCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                    color = TechSurface
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.Download,
                            title = "My Downloads",
                            badgeText = "$downloadsCount files",
                            onClick = onNavigateToDownloads
                        )
                        HorizontalDivider(color = TechCardBorder)
                        ProfileMenuItem(
                            icon = Icons.Default.Bookmark,
                            title = "Saved Bookmarks",
                            badgeText = "$bookmarksCount items",
                            onClick = onNavigateToDownloads
                        )
                    }
                }
            }

            // Settings & Preferences
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "SETTINGS & PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                    color = TechSurface
                ) {
                    Column {
                        // Notifications Switch Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = TechAstraGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = "Exam Alerts & Uploads",
                                    fontSize = 14.sp,
                                    color = TechTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = TechAstraGold
                                )
                            )
                        }
                    }
                }
            }

            // Information & Support
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "INFORMATION & LEGAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                    color = TechSurface
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.Info,
                            title = "About TechAstra",
                            onClick = { showAboutDialog = true }
                        )
                        HorizontalDivider(color = TechCardBorder)
                        ProfileMenuItem(
                            icon = Icons.Default.Policy,
                            title = "Privacy Policy & Terms",
                            onClick = { showPrivacyDialog = true }
                        )
                        HorizontalDivider(color = TechCardBorder)
                        ProfileMenuItem(
                            icon = Icons.Default.Security,
                            title = "Ad Privacy & Consent Settings",
                            onClick = {
                                (context as? Activity)?.let { act ->
                                    AdMobManager.showPrivacyOptionsForm(act)
                                }
                            }
                        )
                        HorizontalDivider(color = TechCardBorder)
                        ProfileMenuItem(
                            icon = Icons.Default.HelpOutline,
                            title = "Help & Support",
                            onClick = { showHelpDialog = true }
                        )
                    }
                }
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("profile_logout_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out",
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        val cleanInitialBranch = remember(userProfile.branch) {
            val b = userProfile.branch.trim()
            if (b.equals("Computer Science & Engineering", ignoreCase = true) ||
                b.equals("Computer Science", ignoreCase = true)
            ) "" else b
        }
        var editName by remember { mutableStateOf(userProfile.name) }
        var editEmail by remember { mutableStateOf(userProfile.email) }
        var editBranch by remember { mutableStateOf(cleanInitialBranch) }
        var editSemester by remember { mutableStateOf(userProfile.semester.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TechTextPrimary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBranch,
                        onValueChange = { editBranch = it },
                        label = { Text("Engineering Branch") },
                        placeholder = { Text("e.g. CSE, IT, ME, ECE (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editSemester,
                        onValueChange = { editSemester = it },
                        label = { Text("Current Semester (1-8)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val semNum = editSemester.toIntOrNull() ?: userProfile.semester
                        onUpdateProfile(editName, editEmail, editBranch, semNum)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechAstraGold)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = TechTextPrimary)
                }
            },
            containerColor = TechSurface
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Log Out?",
            message = "Are you sure you want to log out of TechAstra?",
            confirmButtonText = "Log Out",
            isDestructive = true,
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About TechAstra", fontWeight = FontWeight.Bold, color = TechTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "TechAstra v1.0.0",
                        fontWeight = FontWeight.Bold,
                        color = TechAstraGold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "TechAstra is a purpose-built digital learning companion for B.Tech engineering students. Access verified previous year question papers, mid-term sessional exam archives, and hand-crafted technical notes across Web Development, Android, DSA, Java, Python, and AI/ML.",
                        fontSize = 13.sp,
                        color = TechTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TechAstraGold)
                ) {
                    Text("Close", color = Color.Black)
                }
            },
            containerColor = TechSurface
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy & Advertising", fontWeight = FontWeight.Bold, color = TechTextPrimary) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "TechAstra is dedicated to transparency and student privacy.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TechAstraGold
                    )
                    Text(
                        text = "1. Educational Materials: All lecture notes, university PYQs, syllabus guides, and reference documents are freely provided for personal and non-commercial academic study.",
                        fontSize = 12.sp,
                        color = TechTextSecondary
                    )
                    Text(
                        text = "2. Google AdMob Advertising: To support cloud infrastructure, database hosting, and development costs without charging students, this app displays advertisements using Google AdMob (Google Mobile Ads SDK). Google and certified third-party ad partners may collect and use identifiers (such as Android Advertising ID), cookies, and diagnostic app metrics to deliver relevant advertisements and prevent invalid click fraud in accordance with Google Play Developer policies.",
                        fontSize = 12.sp,
                        color = TechTextSecondary
                    )
                    Text(
                        text = "3. Consent & Data Management: Users subject to GDPR / EEA / UK / US privacy regulations can view, adjust, or revoke their advertising consent at any time using the 'Ad Privacy & Consent Settings' option in the Profile menu.",
                        fontSize = 12.sp,
                        color = TechTextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TechAstraGold)
                ) {
                    Text("Got it", color = Color.Black)
                }
            },
            containerColor = TechSurface
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support", fontWeight = FontWeight.Bold, color = TechTextPrimary) },
            text = {
                Text(
                    text = "Need specific question papers or missing university sessional sets? Reach out to your student coordinator or email support@techastra.edu.",
                    fontSize = 13.sp,
                    color = TechTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TechAstraGold)
                ) {
                    Text("Dismiss", color = Color.Black)
                }
            },
            containerColor = TechSurface
        )
    }
}

@Composable
private fun ProfileStatTile(
    count: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, TechCardBorder, RoundedCornerShape(14.dp)),
        color = TechSurface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TechTextMuted
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TechAstraGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TechTextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    color = TechTextMuted,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TechTextMuted,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
