package com.example.givuandtake

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonGroup(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val radioOptions = listOf("진행 중", "완료")
    Row(verticalAlignment = Alignment.CenterVertically) {
        radioOptions.forEach { option ->
            Row(
                Modifier
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.padding(end = 0.dp)
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun SortDropdownMenu(selectedSort: String, onSortChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedSort,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Icon"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("종료 임박순") },
                onClick = {
                    onSortChange("종료 임박순")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("최근 등록순") },
                onClick = {
                    onSortChange("최근 등록순")
                    expanded = false
                }
            )
        }
    }
}

