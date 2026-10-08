package com.example.ui.screens.pdf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceItem
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
fun PdfViewerScreen(
    resource: ResourceItem,
    downloadItem: DownloadItem?,
    onBack: () -> Unit,
    onDownload: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(1) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var showMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = TechBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = TechSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("pdf_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TechTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Text(
                                text = resource.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TechTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${resource.fileSize} • Page $currentPage of ${resource.pageCount}",
                                fontSize = 11.sp,
                                color = TechAstraGold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Download Icon
                        IconButton(
                            onClick = {
                                onDownload()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Downloading ${resource.title}...")
                                }
                            },
                            modifier = Modifier.testTag("pdf_top_download")
                        ) {
                            Icon(
                                imageVector = if (downloadItem?.status == DownloadStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = "Download",
                                tint = if (downloadItem?.status == DownloadStatus.COMPLETED) AccentGreen else TechAstraGold
                            )
                        }

                        // More Menu
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = TechTextPrimary
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(TechSurface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Reset Zoom (100%)", color = TechTextPrimary) },
                                    onClick = {
                                        zoomScale = 1.0f
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Print / Export PDF", color = TechTextPrimary) },
                                    onClick = {
                                        showMenu = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Export prepared for printing.")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = TechSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    // Page navigation bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prev Page
                        IconButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TechSurfaceVariant)
                                .testTag("pdf_prev_page")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Page",
                                tint = if (currentPage > 1) TechTextPrimary else TechTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "$currentPage / ${resource.pageCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TechTextPrimary
                        )

                        // Next Page
                        IconButton(
                            onClick = { if (currentPage < resource.pageCount) currentPage++ },
                            enabled = currentPage < resource.pageCount,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TechSurfaceVariant)
                                .testTag("pdf_next_page")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Page",
                                tint = if (currentPage < resource.pageCount) TechTextPrimary else TechTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Download Action button
                    Button(
                        onClick = {
                            onDownload()
                            scope.launch {
                                snackbarHostState.showSnackbar("Resource saved to Downloads!")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("pdf_bottom_download_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (downloadItem?.status == DownloadStatus.COMPLETED) AccentGreen else TechAstraGold,
                            contentColor = Color.Black
                        )
                    ) {
                        when (downloadItem?.status) {
                            DownloadStatus.COMPLETED -> {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Downloaded to Device ✓", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            DownloadStatus.DOWNLOADING -> {
                                CircularProgressIndicator(
                                    progress = { downloadItem.progress },
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Downloading ${(downloadItem.progress * 100).toInt()}%...", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            else -> {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Download PDF (${resource.fileSize})", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Document Page View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PDF Document Page Canvas
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(zoomScale)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                    color = Color(0xFFFDFDFD), // Clean white paper
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Page Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TECHO-ASTRA B.TECH REPOSITORY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF64748B),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "P. $currentPage",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color(0xFFCBD5E1)
                        )

                        // Document Title
                        Text(
                            text = resource.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Subject: ${resource.subject} • ${resource.examType} (${resource.year})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Page content mock
                        Text(
                            text = if (currentPage == 1) resource.samplePreview else """
# Section $currentPage: Core Analytical Problems

Q1. Discuss the worst-case, best-case, and average-case analysis for the primary algorithms covered in this module.
Q2. Derive the closed-form recurrence relation using Master's Method and substitution.
Q3. Outline the practical tradeoffs when deploying on distributed high-concurrency systems.

```kotlin
// Example Implementation
fun analyzeComplexity(n: Int): Long {
    var count = 0L
    for (i in 0 until n) {
        for (j in i until n) {
            count += (i * j)
        }
    }
    return count
}
```

Key Formulae:
- Sum(i=1 to n) i = n(n+1)/2
- T(n) = 2T(n/2) + O(n) => O(n log n)
                            """.trimIndent(),
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        // Document Stamp / Watermark
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Verified Study Notes • TechAstra Academic Network",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Floating Zoom Controls (+ / -)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, TechCardBorder, RoundedCornerShape(20.dp)),
                color = TechSurface,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (zoomScale > 0.8f) zoomScale -= 0.1f },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            tint = TechTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${(zoomScale * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechAstraGold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { if (zoomScale < 1.4f) zoomScale += 0.1f },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = TechTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
