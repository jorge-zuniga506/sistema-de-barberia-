package com.example.data.repository

import com.example.data.db.AppSettingsDao
import com.example.data.db.BarberDao
import com.example.data.db.ServiceDao
import com.example.data.db.TransactionDao
import com.example.data.model.AppSettings
import com.example.data.model.Barber
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceItem
import com.example.data.model.TransactionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BarberRepository(
    private val barberDao: BarberDao,
    private val serviceDao: ServiceDao,
    private val transactionDao: TransactionDao,
    private val appSettingsDao: AppSettingsDao
) {
    // Barbers
    val activeBarbers: Flow<List<Barber>> = barberDao.getActiveBarbers()
    val allBarbers: Flow<List<Barber>> = barberDao.getAllBarbers()

    suspend fun insertBarber(barber: Barber): Long = barberDao.insertBarber(barber)
    suspend fun updateBarber(barber: Barber) = barberDao.updateBarber(barber)
    suspend fun deleteBarber(barber: Barber) = barberDao.deleteBarber(barber)
    suspend fun deleteBarberById(id: Long) = barberDao.deleteBarberById(id)

    // Services
    val activeServices: Flow<List<ServiceItem>> = serviceDao.getActiveServices()
    val allServices: Flow<List<ServiceItem>> = serviceDao.getAllServices()

    suspend fun insertService(service: ServiceItem): Long = serviceDao.insertService(service)
    suspend fun updateService(service: ServiceItem) = serviceDao.updateService(service)
    suspend fun deleteService(service: ServiceItem) = serviceDao.deleteService(service)
    suspend fun deleteServiceById(id: Long) = serviceDao.deleteServiceById(id)

    // Transactions
    val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions()

    fun getTransactionsByDateRange(startTime: Long, endTime: Long): Flow<List<TransactionRecord>> =
        transactionDao.getTransactionsByDateRange(startTime, endTime)

    fun getTransactionsByBarber(barberId: Long): Flow<List<TransactionRecord>> =
        transactionDao.getTransactionsByBarber(barberId)

    suspend fun registerSale(
        barber: Barber,
        service: ServiceItem,
        paymentMethod: PaymentMethod,
        notes: String = ""
    ): TransactionRecord {
        val settings = appSettingsDao.getSettingsOnce() ?: AppSettings()
        val retention = service.customRetention ?: settings.defaultRetention
        val commission = (service.price - retention).coerceAtLeast(0.0)

        val lastTicket = transactionDao.getMaxTicketNumber() ?: 1000L
        val nextTicket = lastTicket + 1

        val transaction = TransactionRecord(
            ticketNumber = nextTicket,
            barberId = barber.id,
            barberName = barber.name,
            serviceId = service.id,
            serviceName = service.name,
            totalAmount = service.price,
            retentionAmount = retention,
            commissionAmount = commission,
            paymentMethod = paymentMethod,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )

        val insertedId = transactionDao.insertTransaction(transaction)
        return transaction.copy(id = insertedId)
    }

    suspend fun deleteTransaction(transaction: TransactionRecord) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    // Settings
    val settings: Flow<AppSettings?> = appSettingsDao.getSettings()
    suspend fun getSettingsOnce(): AppSettings = appSettingsDao.getSettingsOnce() ?: AppSettings()
    suspend fun saveSettings(settings: AppSettings) = appSettingsDao.saveSettings(settings)
}
