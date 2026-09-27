package org.chikitsalipi.data

import org.chikitsalipi.model.HealthRecord

class HealthRecordRepository(private val dao: HealthRecordDao) {
    suspend fun getAllRecords(): List<HealthRecord> = dao.getAllRecords()
    suspend fun getRecordById(id: String): HealthRecord? = dao.getRecordById(id)
    suspend fun insertRecord(record: HealthRecord) = dao.insertRecord(record)
    suspend fun deleteRecord(record: HealthRecord) = dao.deleteRecord(record)
}
