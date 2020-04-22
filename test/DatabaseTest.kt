package de.michael

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
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
            transaction {
                val result = saveTranslationJob(translationJob)

                // then
                result shouldNotBe null
            }
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