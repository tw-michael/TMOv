package de.michael

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.assertEquals


class ApplicationTest : FunSpec({
    test("test root") {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
    context("create a job") {
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
    }
})
