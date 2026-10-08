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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.ResourceCard
import com.example.ui.components.ResourceFilter
import com.example.ui.components.SemesterTileCard
import com.example.ui.components.TechnicalCategoryListItem
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    resources: List<ResourceItem>,
    downloads: List<DownloadItem>,
    categories: List<TechnicalCategory>,
    onResourceClick: (String) -> Unit,
    onDownloadClick: (ResourceItem) -> Unit,
    onBookmarkToggle: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSemesterClick: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeTab by remember { mutableStateOf(ResourceType.ALL) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeFilter by remember { mutableStateOf(ResourceFilter()) }

    // Primary tab: 0 = Technical Categories, 1 = Academic Semesters, 2 = All Raw Files
    var browsingTab by remember { mutableIntStateOf(0) }

    // Filter resources matching tab, search query, and bottom sheet filter
    val filteredResources = resources.filter { item ->
        val matchesTab = selectedTypeTab == ResourceType.ALL || item.type == selectedTypeTab
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.subject.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)

        val matchesFilterSem = activeFilter.semester == null || item.semester == activeFilter.semester
        val matchesFilterCat = activeFilter.category == null || item.category.equals(activeFilter.category, ignoreCase = true)
        val matchesFilterYr = activeFilter.year == null || item.year == activeFilter.year
        val matchesFilterType = activeFilter.type == ResourceType.ALL || item.type == activeFilter.type

        matchesTab && matchesQuery && matchesFilterSem && matchesFilterCat && matchesFilterYr && matchesFilterType
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .testTag("resources_screen")
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
                        text = "Explore Resources",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Semester PYQs, sessionals, and technical study notes",
                        fontSize = 13.sp,
                        color = TechTextSecondary
                    )
                }
            }

            // Search Bar + Filter Icon
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search notes, PYQs, subjects...",
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
                                        contentDescription = "Clear",
                                        tint = TechTextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("resources_search_bar"),
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

                    Spacer(modifier = Modifier.width(10.dp))

                    // Filter Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(TechSurface)
                            .border(1.dp, TechCardBorder, RoundedCornerShape(14.dp))
                            .clickable { showFilterSheet = true }
                            .testTag("filter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (activeFilter.semester != null || activeFilter.category != null || activeFilter.year != null) TechAstraGold else TechTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Resource Type Quick Tabs (All, PYQs, Notes, Sessional)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResourceType.entries.forEach { type ->
                        val isSelected = selectedTypeTab == type
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
                                .clickable { selectedTypeTab = type }
                                .padding(vertical = 8.dp)
                                .testTag("type_tab_${type.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TechTextPrimary
                            )
                        }
                    }
                }
            }

            // Browsing Category Tab Row (Technical vs Academic vs Files)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                TabRow(
                    selectedTabIndex = browsingTab,
                    containerColor = TechSurface,
                    contentColor = TechAstraGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[browsingTab]),
                            color = TechAstraGold,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, TechCardBorder, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = browsingTab == 0,
                        onClick = { browsingTab = 0 },
                        text = {
                            Text(
                                text = "Technical",
                                fontWeight = if (browsingTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = browsingTab == 1,
                        onClick = { browsingTab = 1 },
                        text = {
                            Text(
                                text = "Semesters",
                                fontWeight = if (browsingTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = browsingTab == 2,
                        onClick = { browsingTab = 2 },
                        text = {
                            Text(
                                text = "All Files",
                                fontWeight = if (browsingTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tab Content
            when (browsingTab) {
                0 -> {
                    // Technical Topics
                    val filteredCats = categories.filter { cat ->
                        searchQuery.isBlank() ||
                                cat.name.contains(searchQuery, ignoreCase = true) ||
                                cat.subtopics.any { it.contains(searchQuery, ignoreCase = true) }
                    }

                    if (filteredCats.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No Topics Found",
                                message = "No technical categories match '$searchQuery'",
                                actionButtonText = "Reset Search",
                                onActionClick = { searchQuery = "" }
                            )
                        }
                    } else {
                        items(filteredCats) { cat ->
                            Box(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                            ) {
                                TechnicalCategoryListItem(
                                    category = cat,
                                    onClick = { onCategoryClick(cat.id) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Academic Semesters 1 to 8
                    val semesters = (1..8).toList()
                    items(semesters) { sem ->
                        val pyqs = resources.count { it.semester == sem && it.type == ResourceType.PYQ }
                        val sessionals = resources.count { it.semester == sem && it.type == ResourceType.SESSIONAL }
                        Box(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        ) {
                            SemesterTileCard(
                                semesterNum = sem,
                                pyqCount = pyqs,
                                sessionalCount = sessionals,
                                onClick = { onSemesterClick(sem) }
                            )
                        }
                    }
                }
                2 -> {
                    // Raw Filtered Files
                    if (filteredResources.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No Files Match",
                                message = "No resources found matching the current filters.",
                                actionButtonText = "Reset Filters",
                                onActionClick = {
                                    searchQuery = ""
                                    selectedTypeTab = ResourceType.ALL
                                    activeFilter = ResourceFilter()
                                }
                            )
                        }
                    } else {
                        items(filteredResources) { res ->
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

        // Filter Bottom Sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                sheetState = sheetState,
                initialFilter = activeFilter,
                onDismiss = { showFilterSheet = false },
                onApply = { newFilter ->
                    activeFilter = newFilter
                    if (newFilter.type != ResourceType.ALL) {
                        selectedTypeTab = newFilter.type
                    }
                    showFilterSheet = false
                    browsingTab = 2 // Switch to files view to observe applied filters
                }
            )
        }
    }
}
