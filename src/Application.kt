package de.michael

import de.michael.TranslationJob
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.JsonClass
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import io.ktor.features.*
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.*

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
        moshi()
    }

    DbSettings.db

    routing {
        post("/job") {
            val job = call.receive<TranslationJob>()
            val jobId = transaction {
                saveTranslationJob(job)
            }
            call.respond(JobSavedResponse(job.title, jobId))
        }
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

@JsonClass(generateAdapter = true)
data class JobSavedResponse(val title: String, val id: Int)

