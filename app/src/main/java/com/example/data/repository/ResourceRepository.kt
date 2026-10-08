package com.example.data.repository

import android.net.Uri
import com.example.data.mock.MockData
import com.example.data.model.AdminStats
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceItem
import com.example.data.model.ResourceType
import com.example.data.model.TechnicalCategory
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResourceRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Throwable) { null },
    private val storage: FirebaseStorage? = try { FirebaseStorage.getInstance() } catch (e: Throwable) { null },
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Throwable) { null }
) {
    private val _resources = MutableStateFlow<List<ResourceItem>>(MockData.initialResources)
    val resources: StateFlow<List<ResourceItem>> = _resources.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(MockData.initialDownloads)
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    val technicalCategories: List<TechnicalCategory> = MockData.technicalCategories

    private var snapshotRegistration: ListenerRegistration? = null

    init {
        listenToFirestoreResources()
    }

    private fun listenToFirestoreResources() {
        if (firestore == null) return
        snapshotRegistration?.remove()
        snapshotRegistration = firestore.collection("resources")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    if (snapshot.isEmpty) {
                        seedInitialResourcesToFirestore()
                    } else {
                        val firestoreList = snapshot.documents.mapNotNull { doc ->
                            try {
                                val typeStr = doc.getString("type") ?: ResourceType.TECHNICAL_NOTES.name
                                val typeEnum = try {
                                    ResourceType.valueOf(typeStr)
                                } catch (e: Exception) {
                                    ResourceType.TECHNICAL_NOTES
                                }

                                ResourceItem(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    type = typeEnum,
                                    category = doc.getString("category") ?: "",
                                    semester = doc.getLong("semester")?.toInt(),
                                    subject = doc.getString("subject") ?: "",
                                    year = doc.getString("year") ?: "2025",
                                    examType = doc.getString("examType") ?: "End Semester",
                                    fileSize = doc.getString("fileSize") ?: "2.4 MB",
                                    pageCount = (doc.getLong("pageCount") ?: 42L).toInt(),
                                    uploadedDate = doc.getString("uploadedDate") ?: "Oct 2025",
                                    description = doc.getString("description") ?: "",
                                    subtopic = doc.getString("subtopic") ?: "General",
                                    isBookmarked = doc.getBoolean("isBookmarked") ?: false,
                                    fileUrl = doc.getString("fileUrl"),
                                    storagePath = doc.getString("storagePath"),
                                    samplePreview = doc.getString("samplePreview") ?: MockData.initialResources.first().samplePreview
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (firestoreList.isNotEmpty()) {
                            _resources.value = firestoreList
                        }
                    }
                }
            }
    }

    private fun seedInitialResourcesToFirestore() {
        if (firestore == null) return
        scope.launch {
            try {
                val batch = firestore.batch()
                MockData.initialResources.forEach { item ->
                    val docRef = firestore.collection("resources").document(item.id)
                    val map = resourceToMap(item)
                    batch.set(docRef, map)
                }
                batch.commit().await()
            } catch (e: Exception) {
                // Keep local in-memory fallback
            }
        }
    }

    private fun resourceToMap(item: ResourceItem): Map<String, Any?> {
        return mapOf(
            "id" to item.id,
            "title" to item.title,
            "type" to item.type.name,
            "category" to item.category,
            "semester" to item.semester,
            "subject" to item.subject,
            "year" to item.year,
            "examType" to item.examType,
            "fileSize" to item.fileSize,
            "pageCount" to item.pageCount,
            "uploadedDate" to item.uploadedDate,
            "description" to item.description,
            "subtopic" to item.subtopic,
            "isBookmarked" to item.isBookmarked,
            "fileUrl" to item.fileUrl,
            "storagePath" to item.storagePath,
            "samplePreview" to item.samplePreview
        )
    }

    fun getResourceById(id: String): ResourceItem? {
        return _resources.value.firstOrNull { it.id == id }
    }

    fun toggleBookmark(id: String) {
        val current = _resources.value.firstOrNull { it.id == id } ?: return
        val newBookmarkedState = !current.isBookmarked

        _resources.update { currentList ->
            currentList.map { res ->
                if (res.id == id) res.copy(isBookmarked = newBookmarkedState) else res
            }
        }

        if (firestore != null) {
            scope.launch {
                try {
                    firestore.collection("resources").document(id)
                        .update("isBookmarked", newBookmarkedState)
                } catch (e: Exception) {
                    // Non-blocking
                }
            }
        }
    }

    fun startDownload(resource: ResourceItem) {
        val existing = _downloads.value.firstOrNull { it.resourceId == resource.id }
        if (existing != null && existing.status == DownloadStatus.COMPLETED) {
            return
        }

        val downloadId = existing?.id ?: "down-${System.currentTimeMillis()}"
        val initialItem = DownloadItem(
            id = downloadId,
            resourceId = resource.id,
            title = resource.title,
            category = resource.category,
            type = resource.type,
            fileSize = resource.fileSize,
            status = DownloadStatus.DOWNLOADING,
            progress = 0.2f
        )

        _downloads.update { list ->
            listOf(initialItem) + list.filterNot { it.id == downloadId }
        }

        scope.launch {
            delay(350)
            _downloads.update { list ->
                list.map { if (it.id == downloadId) it.copy(progress = 0.6f) else it }
            }
            delay(400)
            _downloads.update { list ->
                list.map { if (it.id == downloadId) it.copy(progress = 0.9f) else it }
            }
            delay(300)
            _downloads.update { list ->
                list.map {
                    if (it.id == downloadId) it.copy(
                        progress = 1.0f,
                        status = DownloadStatus.COMPLETED
                    ) else it
                }
            }
        }
    }

    fun removeDownload(downloadId: String) {
        _downloads.update { list ->
            list.filterNot { it.id == downloadId }
        }
    }

    fun updateUserProfile(name: String, email: String, branch: String, semester: Int) {
        val updated = _userProfile.value.copy(
            name = name,
            email = email,
            branch = branch,
            semester = semester
        )
        _userProfile.value = updated

        val currentUid = auth?.currentUser?.uid
        if (currentUid != null && firestore != null) {
            scope.launch {
                try {
                    firestore.collection("users").document(currentUid)
                        .set(
                            mapOf(
                                "name" to name,
                                "email" to email,
                                "branch" to branch,
                                "semester" to semester
                            ),
                            SetOptions.merge()
                        )
                } catch (e: Exception) {
                    // Non-blocking
                }
            }
        }
    }

    fun addResource(
        item: ResourceItem,
        fileUri: Uri? = null,
        onProgress: (Float) -> Unit = {}
    ) {
        val resourceId = if (item.id.isNotBlank()) item.id else "res-${System.currentTimeMillis()}"
        val updatedItem = item.copy(id = resourceId)

        _resources.update { listOf(updatedItem) + it }

        scope.launch {
            var downloadUrl: String? = updatedItem.fileUrl
            var storagePath: String? = updatedItem.storagePath

            if (fileUri != null && storage != null) {
                try {
                    val path = "resources/$resourceId.pdf"
                    val storageRef = storage.reference.child(path)
                    val uploadTask = storageRef.putFile(fileUri)

                    uploadTask.addOnProgressListener { taskSnapshot ->
                        val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
                        onProgress(progress)
                    }

                    uploadTask.await()
                    downloadUrl = storageRef.downloadUrl.await().toString()
                    storagePath = path
                } catch (e: Exception) {
                    // Non-blocking
                }
            }

            val finalItem = updatedItem.copy(
                fileUrl = downloadUrl,
                storagePath = storagePath
            )

            if (firestore != null) {
                try {
                    firestore.collection("resources").document(resourceId)
                        .set(resourceToMap(finalItem), SetOptions.merge())
                        .await()
                } catch (e: Exception) {
                    // Non-blocking
                }
            }
        }
    }

    fun updateResource(item: ResourceItem) {
        _resources.update { list ->
            list.map { if (it.id == item.id) item else it }
        }

        if (firestore != null) {
            scope.launch {
                try {
                    firestore.collection("resources").document(item.id)
                        .set(resourceToMap(item), SetOptions.merge())
                        .await()
                } catch (e: Exception) {
                    // Non-blocking
                }
            }
        }
    }

    fun deleteResource(id: String) {
        val existing = _resources.value.firstOrNull { it.id == id }
        _resources.update { list ->
            list.filterNot { it.id == id }
        }
        _downloads.update { list ->
            list.filterNot { it.resourceId == id }
        }

        if (firestore != null) {
            scope.launch {
                try {
                    firestore.collection("resources").document(id).delete().await()
                    if (existing?.storagePath != null && storage != null) {
                        storage.reference.child(existing.storagePath).delete().await()
                    }
                } catch (e: Exception) {
                    // Non-blocking
                }
            }
        }
    }

    fun getAdminStats(): AdminStats {
        val list = _resources.value
        return AdminStats(
            totalResources = list.size,
            totalPyqs = list.count { it.type == ResourceType.PYQ },
            totalSessional = list.count { it.type == ResourceType.SESSIONAL },
            totalTechnicalNotes = list.count { it.type == ResourceType.TECHNICAL_NOTES },
            totalDownloads = _downloads.value.size
        )
    }
}
