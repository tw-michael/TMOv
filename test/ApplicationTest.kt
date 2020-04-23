package de.michael

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction


class ApplicationTest : FunSpec({
    context("jobs") {
        test("job should be saved and displayed after posting it to /job") {
            withTestApplication({ module(testing = true) }) {

                // given
                val jobInfoJson = """
                    {
                        "title": "${aJobTitle()}"
                    }
                """.trimIndent()

                // when
                handleRequest(HttpMethod.Post, "/job") {
                    this.addHeader("Content-Type", "application/json")
                    this.setBody(jobInfoJson)
                }.apply {

                    // then
                    response.status() shouldBe HttpStatusCode.OK
                    response.content!! shouldContainJsonKey "title"
                    response.content!! shouldContainJsonKey "id"
                }
            }

        }
        test("list of jobs should be answered when getting /job") {
            withTestApplication({ module(testing = true) }) {
                // given
                val oneJobTitle = aJobTitle().toString()
                val aSecondJobTitle = aJobTitle().toString()

                transaction { TranslationJobs.deleteAll() }
                val oneId = transaction {
                    TranslationJobs.insert {
                        it[title] = oneJobTitle
                    } get TranslationJobs.id
                }
                val aSecondId = transaction {
                    TranslationJobs.insert {
                        it[title] = aSecondJobTitle
                    } get TranslationJobs.id
                }

                // when
                handleRequest(HttpMethod.Get, "/job").apply {

                    // then
                    response.status() shouldBe HttpStatusCode.OK
                    response.content!! shouldMatchJson """
                        [
                            {
                                "id": $oneId,
                                "title": "$oneJobTitle"
                            },
                            {
                                "id": $aSecondId,
                                "title": "$aSecondJobTitle"
                            }
                        ]
                    """.trimIndent()
                }
            }

        }
    }
})
