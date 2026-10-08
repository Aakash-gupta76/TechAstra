package com.example.ui.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.ResourceItem
import com.example.ui.theme.AccentCyan
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
import kotlinx.coroutines.launch

@Composable
fun ResourceDetailsScreen(
    resource: ResourceItem,
    downloadItem: DownloadItem?,
    onBack: () -> Unit,
    onViewPdf: () -> Unit,
    onDownload: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = TechBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = TechSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // View PDF primary button
                    Button(
                        onClick = onViewPdf,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .testTag("details_view_pdf_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TechAstraGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "View PDF", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    // Download action button
                    OutlinedButton(
                        onClick = {
                            onDownload()
                            scope.launch {
                                snackbarHostState.showSnackbar("Download started for ${resource.title}")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("details_download_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechCardBorder)
                    ) {
                        when (downloadItem?.status) {
                            DownloadStatus.COMPLETED -> {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Saved", color = AccentGreen, fontWeight = FontWeight.SemiBold)
                            }
                            DownloadStatus.DOWNLOADING -> {
                                CircularProgressIndicator(
                                    progress = { downloadItem.progress },
                                    modifier = Modifier.size(18.dp),
                                    color = TechAstraGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "${(downloadItem.progress * 100).toInt()}%", color = TechAstraGold)
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = TechAstraGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Download", color = TechTextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(scrollState)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TechSurfaceVariant)
                        .testTag("details_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TechTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Resource link copied to clipboard! 📋")
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TechSurfaceVariant)
                            .testTag("details_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TechTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TechSurfaceVariant)
                            .testTag("details_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (resource.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (resource.isBookmarked) TechAstraGold else TechTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Thumbnail Preview Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, TechCardBorder, RoundedCornerShape(20.dp)),
                color = TechSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(AccentRed.copy(alpha = 0.15f))
                            .border(1.5.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = resource.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TechTextPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${resource.subject} • ${resource.examType}",
                        fontSize = 13.sp,
                        color = TechAstraGold
                    )
                }
            }

            // Metadata Grid
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                color = TechSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Resource Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TechTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    MetadataRow("Category", resource.category)
                    MetadataRow("Semester", if (resource.semester != null) "Semester ${resource.semester}" else "General")
                    MetadataRow("Exam Type", resource.examType)
                    MetadataRow("Academic Year", resource.year)
                    MetadataRow("File Size", resource.fileSize)
                    MetadataRow("Pages", "${resource.pageCount} Pages")
                    MetadataRow("Uploaded", resource.uploadedDate)
                }
            }

            // Description
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
                color = TechSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Overview & Syllabus Coverage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TechTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resource.description,
                        fontSize = 14.sp,
                        color = TechTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TechTextMuted, fontSize = 13.sp)
            Text(text = value, color = TechTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        HorizontalDivider(color = TechCardBorder.copy(alpha = 0.5f))
    }
}
