package de.michael

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranslationJob(val id: Int? = null, val title: String)

data class JobTitle(val title: String)

fun String.toJobTitle(): JobTitle = JobTitle(this)
