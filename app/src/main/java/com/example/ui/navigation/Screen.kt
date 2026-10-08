package com.example.ui.navigation

sealed class Screen {
    data object Splash : Screen()
    data object Login : Screen()
    data object Signup : Screen()
    data object Main : Screen() // Holds Home, Explore, Downloads, Profile bottom tabs

    data class SemesterDetail(val semester: Int) : Screen()
    data class SubjectDetail(val categoryId: String) : Screen()
    data class ResourceDetail(val resourceId: String) : Screen()
    data class PdfViewer(val resourceId: String) : Screen()
}
