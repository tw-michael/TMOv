package de.michael

import de.michael.TranslationJob.TranslationJob
import de.michael.TranslationJobs.id
import de.michael.TranslationJobs.title
import org.jetbrains.exposed.sql.*

object DbSettings {
    val db: Database by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5111/tmov",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "changeme"
        )
    }
}

fun saveTranslationJob(translationJob: TranslationJob): Int {
    return TranslationJobs.insert {
        it[title] = translationJob.title.toString()
    } get id
}

fun findJobById(jobId: Int): TranslationJob {
    return TranslationJobs.select {
        id eq jobId
    }.single()
        .toTranslationJob()
}

private fun ResultRow.toTranslationJob(): TranslationJob =
    TranslationJob(this[id], this[title])

object TranslationJobs : Table() {
    val id = integer("id").autoIncrement() // Column<Int>
    val title = varchar("title", 250) // Column<String>

    override val primaryKey = PrimaryKey(id)
}