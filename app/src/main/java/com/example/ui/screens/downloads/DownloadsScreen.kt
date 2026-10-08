package com.example.ui.screens.downloads

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceType
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.AccentGreen
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
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    onOpenResource: (String) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onNavigateToExplore: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(ResourceType.ALL) }

    val filteredDownloads = downloads.filter {
        selectedFilter == ResourceType.ALL || it.type == selectedFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .testTag("downloads_screen")
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
                        text = "Downloads",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Access offline question papers and notes anytime",
                        fontSize = 13.sp,
                        color = TechTextSecondary
                    )
                }
            }

            // Filter Tabs (All, Notes, PYQs, Sessional)
            item {
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
                                .padding(vertical = 8.dp)
                                .testTag("download_tab_${type.name}"),
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
                Spacer(modifier = Modifier.height(18.dp))
            }

            if (filteredDownloads.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Downloads Yet",
                        message = "Resources you download will appear here for fast offline reading.",
                        icon = Icons.Default.Download,
                        actionButtonText = "Browse Resources",
                        onActionClick = onNavigateToExplore
                    )
                }
            } else {
                items(filteredDownloads) { item ->
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        DownloadItemCard(
                            item = item,
                            onOpen = { onOpenResource(item.resourceId) },
                            onDelete = { onDeleteDownload(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadItemCard(
    item: DownloadItem,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, TechCardBorder, RoundedCornerShape(14.dp))
            .testTag("download_item_${item.id}"),
        color = TechSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PDF Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = TechTextPrimary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.fileSize,
                            fontSize = 12.sp,
                            color = TechTextMuted
                        )
                        Text(
                            text = " • ",
                            fontSize = 12.sp,
                            color = TechTextMuted
                        )
                        Text(
                            text = when (item.status) {
                                DownloadStatus.COMPLETED -> "Completed"
                                DownloadStatus.DOWNLOADING -> "Downloading..."
                                DownloadStatus.PENDING -> "Pending"
                            },
                            fontSize = 12.sp,
                            color = when (item.status) {
                                DownloadStatus.COMPLETED -> AccentGreen
                                DownloadStatus.DOWNLOADING -> TechAstraGold
                                DownloadStatus.PENDING -> TechTextMuted
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (item.status == DownloadStatus.COMPLETED) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = AccentGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Progress bar if currently downloading
            if (item.status == DownloadStatus.DOWNLOADING) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = TechAstraGold,
                    trackColor = TechSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(item.progress * 100).toInt()}% completed",
                    fontSize = 11.sp,
                    color = TechAstraGold,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("delete_download_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Download",
                        tint = TechTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Open button
                FilledTonalButton(
                    onClick = onOpen,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("open_download_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Open PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
