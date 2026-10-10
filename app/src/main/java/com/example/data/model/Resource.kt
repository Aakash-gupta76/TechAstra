package com.example.data.model

enum class ResourceType(val displayName: String) {
    ALL("All"),
    PYQ("PYQs"),
    SESSIONAL("Sessional"),
    TECHNICAL_NOTES("Notes")
}

data class ResourceItem(
    val id: String = "",
    val title: String = "",
    val type: ResourceType = ResourceType.TECHNICAL_NOTES,
    val category: String = "",
    val semester: Int? = null,
    val subject: String = "",
    val year: String = "2025",
    val examType: String = "End Semester",
    val fileSize: String = "2.4 MB",
    val pageCount: Int = 42,
    val uploadedDate: String = "Sep 2025",
    val description: String = "Complete high-yield questions, detailed verified solutions, diagrams, and exam preparation material for B.Tech students.",
    val subtopic: String = "General",
    val isBookmarked: Boolean = false,
    val fileUrl: String? = null,
    val storagePath: String? = null,
    val samplePreview: String = """
# Unit Overview & Core Concepts
1. Asymptotic Notations: Big-O, Big-Omega, Big-Theta
2. Amortized Analysis & Recurrence Relations
3. Master Theorem Solutions: T(n) = aT(n/b) + f(n)
4. Solved University Previous Year Problems (2020-2025)
5. Important Formulae, Diagrams & Complexity Cheatsheet
    """.trimIndent()
)

enum class DownloadStatus {
    COMPLETED,
    DOWNLOADING,
    PENDING
}

data class DownloadItem(
    val id: String,
    val resourceId: String,
    val title: String,
    val category: String,
    val type: ResourceType,
    val fileSize: String,
    val status: DownloadStatus = DownloadStatus.COMPLETED,
    val progress: Float = 1.0f
)

data class TechnicalCategory(
    val id: String,
    val name: String,
    val fileCount: Int,
    val iconName: String,
    val subtopics: List<String>
)

data class UserProfile(
    val uid: String = "",
    val name: String = "Student",
    val email: String = "",
    val role: String = "student", // "student" or "admin"
    val branch: String = "",
    val semester: Int = 5,
    val college: String = "Techno Institute of Technology"
) {
    val isAdmin: Boolean
        get() = role.equals("admin", ignoreCase = true) || email.contains("admin", ignoreCase = true)
}

data class AdminStats(
    val totalResources: Int,
    val totalPyqs: Int,
    val totalSessional: Int,
    val totalTechnicalNotes: Int,
    val totalDownloads: Int
)
