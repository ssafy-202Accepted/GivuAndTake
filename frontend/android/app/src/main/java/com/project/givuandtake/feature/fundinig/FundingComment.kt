package com.project.givuandtake.feature.fundinig

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class Comment(
    val id: Int,
    val author: String,
    val content: String
)

@Composable
fun FundingComment(commentCount: Int, comments: List<Comment>) {
    var newComment by remember { mutableStateOf("") }
    val commentList by remember { mutableStateOf(comments) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Total comments count
        Text(
            text = "따뜻한 댓글을 남겨주세요",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input field for new comment
        OutlinedTextField(
            value = newComment,
            onValueChange = { newComment = it },
            label = { Text("댓글 작성하기") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (newComment.isNotEmpty()) {
                    scope.launch {
                        // Add comment to the list (just for demo, won't persist)
                        commentList.toMutableList().add(
                            Comment(id = commentList.size + 1, author = "새 댓글", content = newComment)
                        )
                        newComment = ""
                    }
                }
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display list of comments
        commentList.forEach { comment ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = comment.author, style = MaterialTheme.typography.bodySmall)
                    Text(text = comment.content, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Pagination controls (for future)
        // Pagination controls (for future)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically // 추가
        ) {
            IconButton(onClick = { /* Previous page action */ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격 추가
            Text(text = "1", fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격 추가
            IconButton(onClick = { /* Next page action */ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
