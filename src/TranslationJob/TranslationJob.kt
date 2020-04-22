package de.michael.TranslationJob

data class TranslationJob(val id: Int? = null, val title: String)

data class JobTitle(val title: String) {
    override fun toString(): String = title
}

fun String.toJobTitle(): JobTitle =
    JobTitle(this)
