package com.example

import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceItem
import com.example.data.model.ResourceType
import com.example.data.repository.ResourceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResourceRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun testInitialDataLoadsSuccessfully() {
        val repo = ResourceRepository(testScope)
        val initialList = repo.resources.value
        assertTrue("Repository should have loaded sample resources", initialList.isNotEmpty())
        assertTrue("Categories should exist", repo.technicalCategories.isNotEmpty())
    }

    @Test
    fun testToggleBookmark() {
        val repo = ResourceRepository(testScope)
        val firstId = repo.resources.value.first().id
        val wasBookmarked = repo.resources.value.first().isBookmarked

        repo.toggleBookmark(firstId)
        val afterToggle = repo.resources.value.first { it.id == firstId }
        assertEquals(!wasBookmarked, afterToggle.isBookmarked)
    }

    @Test
    fun testAdminAddAndUpdateResource() {
        val repo = ResourceRepository(testScope)
        val testItem = ResourceItem(
            id = "test-res-1",
            title = "Compiler Design — End Sem 2025",
            type = ResourceType.PYQ,
            category = "Academic",
            semester = 6,
            subject = "Compiler Design",
            year = "2025"
        )

        repo.addResource(testItem)
        assertNotNull(repo.getResourceById("test-res-1"))

        val updated = testItem.copy(title = "Compiler Design — Sessional 2025")
        repo.updateResource(updated)
        assertEquals("Compiler Design — Sessional 2025", repo.getResourceById("test-res-1")?.title)

        repo.deleteResource("test-res-1")
        assertEquals(null, repo.getResourceById("test-res-1"))
    }
}
