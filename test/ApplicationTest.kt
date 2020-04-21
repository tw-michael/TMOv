package de.michael

import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest : FunSpec({
    test("test root") {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
})
