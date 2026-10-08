package com.example.ui.screens.resources

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadItem
import com.example.data.model.ResourceItem
import com.example.data.model.ResourceType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ResourceCard
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary

@Composable
fun SemesterDetailScreen(
    semester: Int,
    resources: List<ResourceItem>,
    downloads: List<DownloadItem>,
    onBack: () -> Unit,
    onResourceClick: (String) -> Unit,
    onDownloadClick: (ResourceItem) -> Unit,
    onBookmarkToggle: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(ResourceType.ALL) }

    val semesterResources = resources.filter {
        (it.semester == semester || (it.semester == null && it.category == "Academic")) &&
                (selectedFilter == ResourceType.ALL || it.type == selectedFilter)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .statusBarsPadding()
            .testTag("semester_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TechSurfaceVariant)
                            .testTag("semester_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TechTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Semester $semester",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechTextPrimary
                        )
                        Text(
                            text = "Previous Year & Sessional Papers",
                            fontSize = 12.sp,
                            color = TechTextMuted
                        )
                    }
                }
            }

            // Semester Banner Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                    color = TechSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentOrange.copy(alpha = 0.15f))
                                .border(1.dp, AccentOrange.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "B.Tech Semester $semester Archive",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TechTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Includes university question papers from 2020 - 2025",
                                fontSize = 12.sp,
                                color = TechTextSecondary
                            )
                        }
                    }
                }
            }

            // Filter Tabs (All, PYQs, Sessional)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        ResourceType.ALL to "All Papers",
                        ResourceType.PYQ to "End Sem PYQs",
                        ResourceType.SESSIONAL to "Sessional Papers"
                    ).forEach { (type, label) ->
                        val isSelected = selectedFilter == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TechAstraGold else TechSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) TechAstraGold else TechCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedFilter = type }
                                .padding(vertical = 10.dp)
                                .testTag("sem_filter_${type.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TechTextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (semesterResources.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Papers Available",
                        message = "No papers found for Semester $semester under '${selectedFilter.displayName}'. Check other filters or semesters.",
                        actionButtonText = "Show All",
                        onActionClick = { selectedFilter = ResourceType.ALL }
                    )
                }
            } else {
                items(semesterResources) { res ->
                    val download = downloads.firstOrNull { it.resourceId == res.id }
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        ResourceCard(
                            resource = res,
                            downloadStatus = download?.status,
                            downloadProgress = download?.progress ?: 0f,
                            onViewClick = { onResourceClick(res.id) },
                            onDownloadClick = { onDownloadClick(res) },
                            onBookmarkClick = { onBookmarkToggle(res.id) }
                        )
                    }
                }
            }
        }
    }
}
