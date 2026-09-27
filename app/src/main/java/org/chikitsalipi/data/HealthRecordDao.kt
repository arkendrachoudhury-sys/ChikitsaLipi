package org.chikitsalipi.data

import androidx.room.*
import org.chikitsalipi.model.HealthRecord

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records ORDER BY timestamp DESC")
    suspend fun getAllRecords(): List<HealthRecord>

    @Query("SELECT * FROM health_records WHERE recordId = :id")
    suspend fun getRecordById(id: String): HealthRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecord)

    @Delete
    suspend fun deleteRecord(record: HealthRecord)

    @Query("DELETE FROM health_records WHERE recordId = :id")
    suspend fun deleteRecordById(id: String)
}
