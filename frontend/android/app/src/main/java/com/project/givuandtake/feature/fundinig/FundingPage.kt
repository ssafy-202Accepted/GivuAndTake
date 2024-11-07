package com.example.givuandtake

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PaginationControls(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        // 첫 페이지에서도 이전 버튼을 표시
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Previous Page",
            modifier = Modifier
                .clickable(enabled = currentPage > 0) { if (currentPage > 0) onPageChange(currentPage - 1) }
                .padding(8.dp),
            tint = if (currentPage > 0) MaterialTheme.colorScheme.primary else Color.Gray
        )

        // 페이지 번호 버튼
        for (pageIndex in 0 until totalPages) {
            Text(
                text = (pageIndex + 1).toString(),
                modifier = Modifier
                    .clickable { onPageChange(pageIndex) }
                    .padding(8.dp),
                color = if (pageIndex == currentPage) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }

        // 마지막 페이지에서도 다음 버튼을 표시
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Next Page",
            modifier = Modifier
                .clickable(enabled = currentPage < totalPages - 1) { if (currentPage < totalPages - 1) onPageChange(currentPage + 1) }
                .padding(8.dp),
            tint = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}
