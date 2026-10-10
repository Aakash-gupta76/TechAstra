package com.example.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.ui.components.BannerAdView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceItem
import com.example.data.model.ResourceType
import com.example.data.model.TechnicalCategory
import com.example.data.model.UserProfile
import com.example.ui.components.EmptyStateView
import com.example.ui.components.QuickAccessCard
import com.example.ui.components.ResourceCard
import com.example.ui.components.TechAstraLogoBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    resources: List<ResourceItem>,
    downloads: List<DownloadItem>,
    categories: List<TechnicalCategory>,
    onResourceClick: (String) -> Unit,
    onDownloadClick: (ResourceItem) -> Unit,
    onBookmarkToggle: (String) -> Unit,
    onNavigateToSemester: (Int) -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectChip by remember { mutableStateOf("All") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val popularSubjects = listOf("All", "DSA", "Java", "Python", "C++", "Web Development", "Android Development", "AI & ML")
    val studentName = remember(userProfile.name) {
        userProfile.name.trim().ifBlank { "Student" }
    }

    // Filter resources based on search query and selected chip
    val filteredResources = resources.filter { res ->
        val matchesQuery = searchQuery.isBlank() ||
                res.title.contains(searchQuery, ignoreCase = true) ||
                res.subject.contains(searchQuery, ignoreCase = true) ||
                res.category.contains(searchQuery, ignoreCase = true) ||
                res.year.contains(searchQuery, ignoreCase = true) ||
                (res.semester != null && "Semester ${res.semester}".contains(searchQuery, ignoreCase = true))

        val matchesChip = selectedSubjectChip == "All" ||
                res.category.equals(selectedSubjectChip, ignoreCase = true) ||
                res.subject.contains(selectedSubjectChip, ignoreCase = true)

        matchesQuery && matchesChip
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Top App Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TechAstraLogoBadge(size = 38.dp, showRingGlow = false)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tech",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechTextPrimary
                        )
                        Text(
                            text = "Astra",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TechAstraGold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("You're up to date! 3 new PYQs added this week.")
                                }
                            },
                            modifier = Modifier.testTag("notification_bell")
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = TechAstraGold,
                                        contentColor = Color.Black
                                    ) {
                                        Text("3", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = TechTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Profile Avatar
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TechSurfaceVariant)
                                .border(1.5.dp, TechAstraGold, CircleShape)
                                .clickable { onNavigateToProfile() }
                                .testTag("home_avatar"),
                            contentAlignment = Alignment.Center
                         ) {
                            Text(
                                text = studentName.take(1).uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechAstraGold
                            )
                        }
                    }
                }
            }

            // Student Greeting
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Hello, $studentName 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What do you want to study today?",
                        fontSize = 14.sp,
                        color = TechTextSecondary
                    )
                }
            }

            // Search Bar
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search notes, topics, or subjects...",
                                color = TechTextMuted,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TechAstraGold
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = TechTextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_bar"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechAstraGold,
                            unfocusedBorderColor = TechCardBorder,
                            focusedContainerColor = TechSurface,
                            unfocusedContainerColor = TechSurface,
                            focusedTextColor = TechTextPrimary,
                            unfocusedTextColor = TechTextPrimary
                        )
                    )
                }
            }

            // Hero Banner Card
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(20.dp))
                        .testTag("hero_banner_card"),
                    color = TechSurface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_banner),
                            contentDescription = "Banner Artwork",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Gradient overlay for crisp readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xF00B111E),
                                            Color(0xB00B111E),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TechAstraGold)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "B.TECH DIGITAL HUB",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Learn Today,\nBuild Tomorrow",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechTextPrimary,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Your B.Tech resources, all in one place.",
                                fontSize = 12.sp,
                                color = TechTextSecondary
                            )
                        }
                    }
                }
            }

            // Quick Access Section (4 tiles)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Quick Access",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessCard(
                        title = "Semester PYQs",
                        icon = Icons.Default.School,
                        gradientColors = listOf(AccentOrange, Color(0xFFEA580C)),
                        onClick = { onNavigateToSemester(userProfile.semester) },
                        modifier = Modifier.weight(1f)
                    )

                    QuickAccessCard(
                        title = "Sessional Papers",
                        icon = Icons.Default.Assignment,
                        gradientColors = listOf(AccentCyan, Color(0xFF0284C7)),
                        onClick = { onNavigateToExplore() },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessCard(
                        title = "Technical Notes",
                        icon = Icons.Default.Description,
                        gradientColors = listOf(AccentPurple, Color(0xFF7C3AED)),
                        onClick = { onNavigateToCategory("web_dev") },
                        modifier = Modifier.weight(1f)
                    )

                    QuickAccessCard(
                        title = "Downloads",
                        icon = Icons.Default.Download,
                        gradientColors = listOf(AccentPink, Color(0xFFDB2777)),
                        onClick = { onNavigateToDownloads() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Google AdMob Test Banner Ad
            item {
                Spacer(modifier = Modifier.height(14.dp))
                BannerAdView()
            }

            // Popular Subjects Chips
            item {
                Spacer(modifier = Modifier.height(26.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Subjects",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechTextPrimary
                    )
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TechAstraGold,
                        modifier = Modifier
                            .clickable { onNavigateToExplore() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(popularSubjects) { chip ->
                        val isSelected = selectedSubjectChip == chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) TechAstraGold else TechSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) TechAstraGold else TechCardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedSubjectChip = chip }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("subject_chip_$chip")
                        ) {
                            Text(
                                text = chip,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TechTextPrimary
                            )
                        }
                    }
                }
            }

            // Latest Resources Section
            item {
                Spacer(modifier = Modifier.height(26.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Search Results (${filteredResources.size})" else "Latest Resources",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechTextPrimary
                    )

                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Explore",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TechAstraGold,
                            modifier = Modifier
                                .clickable { onNavigateToExplore() }
                                .padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (filteredResources.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Resources Found",
                        message = "We couldn't find any resources matching '$searchQuery'. Try checking spelling or explore other categories.",
                        actionButtonText = "Reset Search",
                        onActionClick = {
                            searchQuery = ""
                            selectedSubjectChip = "All"
                        }
                    )
                }
            } else {
                items(filteredResources) { res ->
                    val downloadItem = downloads.firstOrNull { it.resourceId == res.id }
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        ResourceCard(
                            resource = res,
                            downloadStatus = downloadItem?.status,
                            downloadProgress = downloadItem?.progress ?: 0f,
                            onViewClick = { onResourceClick(res.id) },
                            onDownloadClick = {
                                onDownloadClick(res)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Downloading ${res.title}...")
                                }
                            },
                            onBookmarkClick = { onBookmarkToggle(res.id) }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
