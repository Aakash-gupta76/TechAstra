package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceItem
import com.example.data.model.ResourceType
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourceCard(
    resource: ResourceItem,
    downloadStatus: DownloadStatus? = null,
    downloadProgress: Float = 0f,
    onViewClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val typeBadgeColor = when (resource.type) {
        ResourceType.PYQ -> AccentOrange
        ResourceType.SESSIONAL -> AccentCyan
        ResourceType.TECHNICAL_NOTES -> AccentPurple
        ResourceType.ALL -> TechAstraGold
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp))
            .clickable { onViewClick() }
            .testTag("resource_card_${resource.id}"),
        color = TechSurface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // PDF Icon badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentRed.copy(alpha = 0.15f))
                        .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF File",
                        tint = AccentRed,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = resource.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = TechTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = resource.subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = TechTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bookmark Icon
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("bookmark_button_${resource.id}")
                ) {
                    Icon(
                        imageVector = if (resource.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (resource.isBookmarked) TechAstraGold else TechTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Type Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeBadgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = resource.type.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = typeBadgeColor
                    )
                }

                // Semester badge if applicable
                if (resource.semester != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TechSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Sem ${resource.semester}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TechTextSecondary
                        )
                    }
                }

                // Year
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TechSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = resource.year,
                        fontSize = 11.sp,
                        color = TechTextMuted
                    )
                }

                // File Size
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TechSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = resource.fileSize,
                        fontSize = 11.sp,
                        color = TechTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row: View button + Download state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onViewClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("view_button_${resource.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "View PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Download state action
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            when (downloadStatus) {
                                DownloadStatus.COMPLETED -> AccentGreen.copy(alpha = 0.15f)
                                DownloadStatus.DOWNLOADING -> TechAstraGold.copy(alpha = 0.15f)
                                else -> TechSurfaceVariant
                            }
                        )
                        .clickable { onDownloadClick() }
                        .testTag("download_button_${resource.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    when (downloadStatus) {
                        DownloadStatus.COMPLETED -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Downloaded",
                                tint = AccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DownloadStatus.DOWNLOADING -> {
                            CircularProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.size(20.dp),
                                color = TechAstraGold,
                                strokeWidth = 2.5.dp
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download PDF",
                                tint = TechAstraGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
