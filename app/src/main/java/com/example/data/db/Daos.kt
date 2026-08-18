package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import com.example.data.model.AppSettings
import com.example.data.model.Barber
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceItem
import com.example.data.model.TransactionRecord
import kotlinx.coroutines.flow.Flow

class DatabaseConverters {
    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = runCatching {
        PaymentMethod.valueOf(value)
    }.getOrDefault(PaymentMethod.EFECTIVO)
}

@Dao
interface BarberDao {
    @Query("SELECT * FROM barbers WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveBarbers(): Flow<List<Barber>>

    @Query("SELECT * FROM barbers ORDER BY name ASC")
    fun getAllBarbers(): Flow<List<Barber>>

    @Query("SELECT * FROM barbers WHERE id = :id")
    suspend fun getBarberById(id: Long): Barber?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarber(barber: Barber): Long

    @Update
    suspend fun updateBarber(barber: Barber)

    @Delete
    suspend fun deleteBarber(barber: Barber)

    @Query("DELETE FROM barbers WHERE id = :id")
    suspend fun deleteBarberById(id: Long)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY price ASC")
    fun getActiveServices(): Flow<List<ServiceItem>>

    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAllServices(): Flow<List<ServiceItem>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Long): ServiceItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceItem): Long

    @Update
    suspend fun updateService(service: ServiceItem)

    @Delete
    suspend fun deleteService(service: ServiceItem)

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteServiceById(id: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startTime: Long, endTime: Long): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE barberId = :barberId ORDER BY timestamp DESC")
    fun getTransactionsByBarber(barberId: Long): Flow<List<TransactionRecord>>

    @Query("SELECT MAX(ticketNumber) FROM transactions")
    suspend fun getMaxTicketNumber(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionRecord)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettingsOnce(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}
