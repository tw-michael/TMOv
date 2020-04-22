package de.michael

import de.michael.TranslationJob.JobTitle
import de.michael.TranslationJob.TranslationJob
import de.michael.TranslationJob.toJobTitle
import kotlin.random.Random

fun aTranslationJob(): TranslationJob =
    TranslationJob(title = aJobTitle().toString())

fun aJobTitle(): JobTitle = "TranslationJob${randomId()}".toJobTitle()
fun randomId(): String = Random.nextInt(100000, 9999999).toString()