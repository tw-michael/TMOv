package de.michael.helloworld

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DataBase() {

    init {
        val dbConnection = Database.connect(
            url = "jdbc:postgresql://localhost:5432/hello_world_db",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "password"
        )
    }
}