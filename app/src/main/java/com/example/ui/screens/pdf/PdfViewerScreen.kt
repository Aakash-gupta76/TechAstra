package com.example.ui.screens.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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

/**
 * Normalizes Google Drive and remote PDF URLs so they can be viewed smoothly.
 * Supports:
 * - https://drive.google.com/file/d/ID/view
 * - https://drive.google.com/file/d/ID/preview
 * - https://drive.google.com/open?id=ID
 * - https://drive.google.com/uc?id=ID
 * - Raw PDF URLs using Google Docs Viewer
 */
fun resolvePreviewUrl(rawUrl: String?): String? {
    if (rawUrl.isNullOrBlank()) return null
    val trimmed = rawUrl.trim()

    // Match Google Drive file ID
    val fileIdRegex = Regex("drive\\.google\\.com\\/(?:file\\/d\\/|open\\?id=|uc\\?id=)([a-zA-Z0-9_-]+)")
    val match = fileIdRegex.find(trimmed)
    if (match != null) {
        val fileId = match.groupValues[1]
        // Google Drive preview URL embeds cleanly in mobile WebViews
        return "https://drive.google.com/file/d/$fileId/preview"
    }

    // Direct PDF link from cloud storage or other host
    if (trimmed.endsWith(".pdf", ignoreCase = true) || trimmed.contains(".pdf?", ignoreCase = true)) {
        return "https://docs.google.com/gview?embedded=true&url=" + Uri.encode(trimmed)
    }

    return trimmed
}

fun openInExternalBrowser(context: Context, rawUrl: String?) {
    if (rawUrl.isNullOrBlank()) return
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawUrl.trim())).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback if no specific browser handler
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PdfViewerScreen(
    resource: ResourceItem,
    downloadItem: DownloadItem?,
    onBack: () -> Unit,
    onDownload: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showMenu by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var loadProgress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val rawUrl = resource.fileUrl?.takeIf { it.isNotBlank() }
    val previewUrl = remember(rawUrl) { resolvePreviewUrl(rawUrl) }

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
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                    text = "${resource.subject} • ${resource.fileSize}",
                                    fontSize = 11.sp,
                                    color = TechAstraGold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Open in Browser / Drive button
                            if (!rawUrl.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        openInExternalBrowser(context, rawUrl)
                                    },
                                    modifier = Modifier.testTag("pdf_open_drive_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Open in Drive / Browser",
                                        tint = TechAstraGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

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
                                        text = { Text("Refresh Document", color = TechTextPrimary) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Refresh, contentDescription = null, tint = TechTextPrimary)
                                        },
                                        onClick = {
                                            showMenu = false
                                            isError = false
                                            isLoading = true
                                            webViewInstance?.reload()
                                        }
                                    )
                                    if (!rawUrl.isNullOrBlank()) {
                                        DropdownMenuItem(
                                            text = { Text("Open in Google Drive / Browser", color = TechTextPrimary) },
                                            leadingIcon = {
                                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = TechAstraGold)
                                            },
                                            onClick = {
                                                showMenu = false
                                                openInExternalBrowser(context, rawUrl)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Progress indicator below top bar
                    if (isLoading && previewUrl != null) {
                        LinearProgressIndicator(
                            progress = { loadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp),
                            color = TechAstraGold,
                            trackColor = TechSurfaceVariant
                        )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Open in Google Drive / Browser action
                    if (!rawUrl.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { openInExternalBrowser(context, rawUrl) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("pdf_bottom_open_drive"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TechAstraGold)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = TechAstraGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Open in Drive",
                                color = TechAstraGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Download Action button
                    Button(
                        onClick = {
                            onDownload()
                            scope.launch {
                                snackbarHostState.showSnackbar("Resource saved to Downloads!")
                            }
                        },
                        modifier = Modifier
                            .weight(1.2f)
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
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saved to Device ✓", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                            DownloadStatus.DOWNLOADING -> {
                                CircularProgressIndicator(
                                    progress = { downloadItem.progress },
                                    modifier = Modifier.size(18.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Downloading ${(downloadItem.progress * 100).toInt()}%...", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                            else -> {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download PDF", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
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
                .background(TechBackground)
        ) {
            if (previewUrl == null) {
                // No URL attached to this resource yet
                NoPdfUrlView(
                    resource = resource,
                    onOpenBrowser = {
                        if (!rawUrl.isNullOrBlank()) openInExternalBrowser(context, rawUrl)
                    }
                )
            } else {
                // Interactive WebView rendering the Google Drive / PDF viewer
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("pdf_webview"),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                                allowFileAccess = true
                                mediaPlaybackRequiresUserGesture = false
                                userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    isError = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    if (request?.isForMainFrame == true) {
                                        isLoading = false
                                        isError = true
                                        errorMessage = error?.description?.toString() ?: "Failed to connect"
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    loadProgress = newProgress
                                    if (newProgress >= 90) {
                                        isLoading = false
                                    }
                                }
                            }

                            loadUrl(previewUrl)
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                    }
                )

                // Error State overlay with fallback button
                if (isError) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(TechBackground.copy(alpha = 0.95f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(AccentRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = AccentRed,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = "Unable to load embedded preview",
                                color = TechTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Google Drive permissions might require opening in browser, or internet is slow.",
                                color = TechTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isError = false
                                        isLoading = true
                                        webViewInstance?.reload()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TechCardBorder)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TechTextPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry", color = TechTextPrimary)
                                }

                                Button(
                                    onClick = {
                                        openInExternalBrowser(context, rawUrl)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TechAstraGold,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open in Google Drive", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoPdfUrlView(
    resource: ResourceItem,
    onOpenBrowser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, TechCardBorder, RoundedCornerShape(16.dp)),
            color = TechSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(TechAstraGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = TechAstraGold,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = resource.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TechTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Subject: ${resource.subject} • ${resource.examType}",
                    fontSize = 13.sp,
                    color = TechTextSecondary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "This resource does not have an attached PDF URL yet. Please add the Google Drive or PDF link in the Admin Web Panel to view it here.",
                    fontSize = 12.sp,
                    color = TechTextMuted,
                    textAlign = TextAlign.Center
                )

                if (!resource.description.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = TechSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = resource.description,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = TechTextSecondary
                        )
                    }
                }
            }
        }
    }
}
