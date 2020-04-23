package de.michael

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseTest : FunSpec({

    beforeSpec {
        DbSettings.db
        transaction {
            SchemaUtils.create(TranslationJobs)
        }
    }

    context("jobs") {
        test("should create ID when job is saved") {
            // given
            val translationJob = aTranslationJob()

            // when
            val result = transaction {
                saveTranslationJob(translationJob)
            }

            // then
            result shouldNotBe null
        }
        test("should retrieve value for saved job's id") {
            // given
            val translationJob = aTranslationJob()

            // when
            transaction {
                saveTranslationJob(translationJob)
            }.let { jobId ->
                transaction {
                    val result = findJobById(jobId)

                    // then
                    result.id shouldBe jobId
                    result.title shouldBe translationJob.title
                }
            }
        }
    }
})