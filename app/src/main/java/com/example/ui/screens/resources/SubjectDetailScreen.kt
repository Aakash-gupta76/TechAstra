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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
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
import com.example.data.model.TechnicalCategory
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ResourceCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary

@Composable
fun SubjectDetailScreen(
    category: TechnicalCategory,
    resources: List<ResourceItem>,
    downloads: List<DownloadItem>,
    onBack: () -> Unit,
    onResourceClick: (String) -> Unit,
    onDownloadClick: (ResourceItem) -> Unit,
    onBookmarkToggle: (String) -> Unit
) {
    var selectedType by remember { mutableStateOf(ResourceType.ALL) }
    var selectedSubtopic by remember { mutableStateOf<String?>("All") }

    val categoryResources = resources.filter { res ->
        val matchesCategory = res.category.equals(category.name, ignoreCase = true) ||
                (category.id == "dsa" && res.category == "DSA") ||
                (category.id == "web_dev" && res.category == "Web Development") ||
                (category.id == "app_dev" && res.category == "Android Development") ||
                (category.id == "java" && res.category == "Java") ||
                (category.id == "python" && res.category == "Python") ||
                (category.id == "cpp" && res.category == "C++") ||
                (category.id == "aiml" && res.category == "AI & ML") ||
                (category.id == "dbms" && res.category == "Database (DBMS)") ||
                (category.id == "os" && res.category == "Operating Systems")

        val matchesType = selectedType == ResourceType.ALL || res.type == selectedType

        val matchesSubtopic = selectedSubtopic == null || selectedSubtopic == "All" ||
                res.title.contains(selectedSubtopic!!, ignoreCase = true) ||
                res.subtopic.equals(selectedSubtopic!!, ignoreCase = true)

        matchesCategory && matchesType && matchesSubtopic
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .statusBarsPadding()
            .testTag("subject_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Top Bar
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
                            .testTag("subject_back_button")
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
                            text = category.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechTextPrimary
                        )
                        Text(
                            text = "${category.fileCount} Resources Available",
                            fontSize = 12.sp,
                            color = TechTextMuted
                        )
                    }
                }
            }

            // Hero Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                    color = TechSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentCyan.copy(alpha = 0.15f))
                                    .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = category.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TechTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Notes • PYQs • Sessional Papers",
                                    fontSize = 12.sp,
                                    color = TechAstraGold
                                )
                            }
                        }
                    }
                }
            }

            // Type Filter Tabs
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ResourceType.ALL to "All",
                        ResourceType.TECHNICAL_NOTES to "Notes",
                        ResourceType.PYQ to "PYQs",
                        ResourceType.SESSIONAL to "Sessional"
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
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
                                .clickable { selectedType = type }
                                .padding(vertical = 9.dp)
                                .testTag("subject_type_${type.name}"),
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
            }

            // Subtopics Pills Row
            item {
                Spacer(modifier = Modifier.height(14.dp))
                val subtopics = listOf("All") + category.subtopics
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subtopics) { subtopic ->
                        val isSelected = selectedSubtopic == subtopic
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) TechSurfaceVariant else TechSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) TechAstraGold else TechCardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedSubtopic = subtopic }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("subtopic_$subtopic")
                        ) {
                            Text(
                                text = subtopic,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) TechAstraGold else TechTextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (categoryResources.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Resources Available",
                        message = "No resources found under '$selectedSubtopic'. Try selecting 'All' or a different type tab.",
                        actionButtonText = "Reset Filters",
                        onActionClick = {
                            selectedType = ResourceType.ALL
                            selectedSubtopic = "All"
                        }
                    )
                }
            } else {
                items(categoryResources) { res ->
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
