package com.project.givuandtake.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import java.text.NumberFormat
import java.util.Locale

data class GiftResponse(
    val success: Boolean,
    val data: List<GiftDetail>
)

@Entity(tableName = "gift_details")
data class GiftDetail(
    @PrimaryKey val giftIdx: Int,          // API의 giftIdx를 id로 사용
    val giftName: String,                  // 상품 이름
    val corporationIdx: Int,               // 회사 ID
    val corporationName: String,           // 회사 이름
    val corporationSido: String,           // 회사 소재 시도
    val corporationSigungu: String,        // 회사 소재 시군구
    val categoryIdx: Int,                  // 카테고리 ID
    val categoryName: String,              // 카테고리 이름
    val giftThumbnail: String?,            // 상품 썸네일 (nullable)
    val giftContentImage: String?,         // 상품 상세 이미지 (nullable)
    val giftContent: String?,              // 상품 설명 (nullable)
    val price: Int,                        // 상품 가격
    val createdDate: String?,              // 생성 날짜 (nullable)
    val modifiedDate: String?              // 수정 날짜 (nullable)
) {
    // 커스텀 getter로 location 값을 계산
    val location: String
        get() = "$corporationSido $corporationSigungu"

    // 가격을 "10,000" 형식으로 반환하는 커스텀 getter
    val priceFormatted: String
        get() = NumberFormat.getNumberInstance(Locale.KOREA).format(price)
}


@Dao
interface GiftDetailDao {

    @Query("SELECT * FROM gift_details")
    fun getAllGiftDetails(): Flow<List<GiftDetail>>

    @Query("SELECT * FROM gift_details WHERE giftIdx IN (:ids)")
    fun getGiftDetailsByIds(ids: List<Int>): Flow<List<GiftDetail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiftDetails(giftDetails: List<GiftDetail>)

    @Query("DELETE FROM gift_details")
    suspend fun deleteAll()
}

@Database(entities = [GiftDetail::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDetailDao(): GiftDetailDao
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration() // 스키마 변경 시 데이터베이스를 재생성합니다.
                .build()
            INSTANCE = instance
            instance
        }
    }
}


data class GiftDetailResponse(
    val success: Boolean,
    val data: GiftDetailData
)

data class GiftRecentResponse(
    val success: Boolean,
    val data: List<GiftDetailData>
)


data class GiftDetailData(
    val giftIdx: Int,               // 상품 ID
    val giftName: String,           // 상품 이름
    val corporationIdx: Int,        // 회사 ID
    val corporationName: String,    // 회사 이름
    val corporationSido: String,    // 회사 소재 시/도
    val corporationSigungu: String, // 회사 소재 시/군/구
    val categoryIdx: Int,           // 카테고리 ID
    val categoryName: String,       // 카테고리 이름
    val giftThumbnail: String?,     // 상품 썸네일 URL
    val giftContentImage: String?,
    val giftContent: String?,       // 상품 설명
    val price: Int,                 // 상품 가격
    val createdDate: String?,       // 생성 날짜
    val modifiedDate: String?       // 수정 날짜
) {
    // 커스텀 getter로 상품의 전체 주소를 반환
    val location: String
        get() = "$corporationSido $corporationSigungu"

    // 커스텀 getter로 상품 설명을 파싱하여 반환 (이미지 URL, 텍스트 분리)
    val giftContentDetails: Pair<String?, String?>
        get() {
            val parts = giftContent?.split(",") ?: listOf(null, null)
            return Pair(parts.getOrNull(0), parts.getOrNull(1))
        }
    val priceFormatted: String
        get() = NumberFormat.getNumberInstance(Locale.KOREA).format(price)
}
