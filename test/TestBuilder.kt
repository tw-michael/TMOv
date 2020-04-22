package de.michael

import kotlin.random.Random

fun aTranslationJob(): TranslationJob = TranslationJob(title = "TranslationJob${randomId()}")

fun randomId(): String = Random.nextInt(100000, 9999999).toString()
