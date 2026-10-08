package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
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
import com.example.data.model.ResourceType
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary

data class ResourceFilter(
    val type: ResourceType = ResourceType.ALL,
    val semester: Int? = null,
    val category: String? = null,
    val year: String? = null
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    initialFilter: ResourceFilter,
    onDismiss: () -> Unit,
    onApply: (ResourceFilter) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialFilter.type) }
    var selectedSemester by remember { mutableStateOf(initialFilter.semester) }
    var selectedCategory by remember { mutableStateOf(initialFilter.category) }
    var selectedYear by remember { mutableStateOf(initialFilter.year) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TechSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Resources",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextPrimary
                )
                Text(
                    text = "Reset All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TechAstraGold,
                    modifier = Modifier
                        .clickable {
                            selectedType = ResourceType.ALL
                            selectedSemester = null
                            selectedCategory = null
                            selectedYear = null
                        }
                        .padding(4.dp)
                        .testTag("filter_reset_button")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Resource Type
            Text(
                text = "RESOURCE TYPE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TechTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceType.entries.forEach { type ->
                    val isSelected = selectedType == type
                    FilterChipItem(
                        text = type.displayName,
                        isSelected = isSelected,
                        onClick = { selectedType = type }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Semester
            Text(
                text = "SEMESTER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TechTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..8).forEach { sem ->
                    val isSelected = selectedSemester == sem
                    FilterChipItem(
                        text = "Sem $sem",
                        isSelected = isSelected,
                        onClick = {
                            selectedSemester = if (isSelected) null else sem
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Technical Category
            Text(
                text = "TOPIC / CATEGORY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TechTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Web Development",
                    "Android Development",
                    "DSA",
                    "Java",
                    "Python",
                    "C++",
                    "AI & ML",
                    "Database (DBMS)",
                    "Operating Systems"
                ).forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChipItem(
                        text = cat,
                        isSelected = isSelected,
                        onClick = {
                            selectedCategory = if (isSelected) null else cat
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Year
            Text(
                text = "YEAR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TechTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("2025", "2024", "2023", "2022").forEach { yr ->
                    val isSelected = selectedYear == yr
                    FilterChipItem(
                        text = yr,
                        isSelected = isSelected,
                        onClick = {
                            selectedYear = if (isSelected) null else yr
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Cancel", color = TechTextPrimary)
                }

                Button(
                    onClick = {
                        onApply(
                            ResourceFilter(
                                type = selectedType,
                                semester = selectedSemester,
                                category = selectedCategory,
                                year = selectedYear
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("apply_filter_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TechAstraGold)
                ) {
                    Text(
                        text = "Apply Filters",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TechAstraGold else TechSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) TechAstraGold else TechCardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.Black else TechTextPrimary
        )
    }
}
