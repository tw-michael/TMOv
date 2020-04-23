package de.michael

import de.michael.TranslationJob.TranslationJob
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val logger = KotlinLogging.logger {}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(StatusPages) {
        exception<AuthenticationException> { cause ->
            logger.error(cause) { cause.localizedMessage }
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { cause ->
            logger.error(cause) { cause.localizedMessage }
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<RuntimeException> { cause ->
            logger.error(cause) { cause.localizedMessage }
            call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
        }

    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        gson {

        }
    }

    DbSettings.db
    transaction {
        SchemaUtils.create(TranslationJobs)
    }

    routing {
        post("/job") {
            val job = call.receive<TranslationJob>()
            val jobId = transaction {
                saveTranslationJob(job)
            }
            call.respond(JobSavedResponse(job.title, jobId))
        }
        get("/job") {
            transaction {
                TranslationJobs.selectAll().toList()
            }.map {
                it.toTranslationJob()
            }.let {
                call.respond(it)
            }
        }
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

data class JobSavedResponse(val title: String, val id: Int)

