package com.smartledger.app.data

import com.smartledger.core.database.PersonDao
import com.smartledger.core.database.PersonEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PeopleRepository(private val dao: PersonDao) {
    fun observe(): Flow<List<PersonEntity>> = dao.observePeople()
    suspend fun add(name: String, phone: String?, note: String?) = dao.insert(PersonEntity(UUID.randomUUID().toString(), name.trim(), phone?.trim()?.ifBlank { null }, note?.trim()?.ifBlank { null }))
}
