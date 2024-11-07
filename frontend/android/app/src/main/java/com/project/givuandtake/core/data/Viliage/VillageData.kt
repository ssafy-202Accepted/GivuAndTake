package com.project.givuandtake.core.data.Viliage

data class VillageData(
    val success: Boolean,
    val data: List<ExperienceVillage>
)

data class ExperienceVillage(
    val experienceVillageIdx: Int,
    val experienceVillageName: String,
    val experienceVillageAddress: String,
    val experienceVillageProgram: String,
    val experienceVillagePhone: String = "",
    val experienceVillageHomepageUrl: String = ""
)