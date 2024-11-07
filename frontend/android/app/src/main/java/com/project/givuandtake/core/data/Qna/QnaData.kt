data class QnaData(
    val success: Boolean,
    val data: List<UserQna>
)

data class UserQna(
    val qnaIdx: Int,
    val userIdx: Int,
    val userName: String,
    val userProfileImage: String,
    val qnaTitle: String,
    val qnaContent: String,
    val createdDate: String,
    val answer: Answer?  // null일 수 있는 answer 객체
)

data class Answer(
    val answerIdx: Int,
    val userIdx: Int,
    val userName: String,
    val userProfileImage: String,
    val qnaIdx: Int,
    val answerContent: String
)