package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppSettings
import com.example.data.model.Barber
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceItem
import com.example.data.model.TransactionRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Barber::class,
        ServiceItem::class,
        TransactionRecord::class,
        AppSettings::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class BarberDatabase : RoomDatabase() {
    abstract fun barberDao(): BarberDao
    abstract fun serviceDao(): ServiceDao
    abstract fun transactionDao(): TransactionDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: BarberDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BarberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BarberDatabase::class.java,
                    "barberia_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(db: BarberDatabase) {
                // Initial Settings with rich ticket custom defaults
                val defaultSettings = AppSettings(
                    id = 1,
                    shopName = "Barbería Estilo & Arte",
                    shopSlogan = "Estilo, Corte & Tradición",
                    sinpePhone = "8888-2424",
                    contactPhone = "2222-3344",
                    instagram = "@barberia_estiloyarte",
                    facebookOrWeb = "fb.me/barberiaestilo",
                    address = "Av. Central, San José",
                    wifiInfo = "WiFi: BarberiaPass",
                    defaultRetention = 2000.0,
                    currencySymbol = "₡",
                    headerGreeting = "COMPROBANTE DE PAGO",
                    ticketFooter = "¡Gracias por su preferencia! Calidad y estilo en cada corte.",
                    customPolicy = "Conserve su ticket para cualquier consulta",
                    showSlogan = true,
                    showSinpe = true,
                    showPhone = true,
                    showInstagram = true,
                    showFacebookOrWeb = false,
                    showAddress = true,
                    showWifi = true,
                    showCustomPolicy = true,
                    showBarberName = true,
                    showDateTime = true,
                    showTicketNumber = true
                )
                db.appSettingsDao().saveSettings(defaultSettings)

                // Initial Barbers (Empleados)
                val barbers = listOf(
                    Barber(name = "Carlos Morales", phone = "8712-3456", isActive = true),
                    Barber(name = "Mateo Vargas", phone = "8899-1122", isActive = true),
                    Barber(name = "Andrés Soto", phone = "8344-5566", isActive = true)
                )
                val barberIds = barbers.map { db.barberDao().insertBarber(it) }

                // Initial Services
                val services = listOf(
                    ServiceItem(name = "Corte Clásico", price = 6000.0, customRetention = 2000.0, category = "Cortes"),
                    ServiceItem(name = "Perfilado de Barba", price = 4000.0, customRetention = 1500.0, category = "Barba"),
                    ServiceItem(name = "Combo Corte + Barba", price = 9000.0, customRetention = 3000.0, category = "Combos"),
                    ServiceItem(name = "Diseño / Perfilado Cejas", price = 2500.0, customRetention = 1000.0, category = "Detalles"),
                    ServiceItem(name = "Lavado & Exfoliación", price = 3500.0, customRetention = 1200.0, category = "Tratamientos"),
                    ServiceItem(name = "Tinte / Decoloración", price = 12000.0, customRetention = 4000.0, category = "Color")
                )
                val serviceIds = services.map { db.serviceDao().insertService(it) }

                // Initial Transactions for sample demonstration on first launch
                val now = System.currentTimeMillis()
                val oneHourAgo = now - (60 * 60 * 1000)
                val twoHoursAgo = now - (2 * 60 * 60 * 1000)
                val yesterday = now - (24 * 60 * 60 * 1000)

                if (barberIds.isNotEmpty() && serviceIds.isNotEmpty()) {
                    db.transactionDao().insertTransaction(
                        TransactionRecord(
                            ticketNumber = 1001,
                            barberId = barberIds[0],
                            barberName = "Carlos Morales",
                            serviceId = serviceIds[0],
                            serviceName = "Corte Clásico",
                            totalAmount = 6000.0,
                            retentionAmount = 2000.0,
                            commissionAmount = 4000.0,
                            paymentMethod = PaymentMethod.EFECTIVO,
                            notes = "Cliente recurrente",
                            timestamp = twoHoursAgo
                        )
                    )
                    db.transactionDao().insertTransaction(
                        TransactionRecord(
                            ticketNumber = 1002,
                            barberId = barberIds[1],
                            barberName = "Mateo Vargas",
                            serviceId = serviceIds[2],
                            serviceName = "Combo Corte + Barba",
                            totalAmount = 9000.0,
                            retentionAmount = 3000.0,
                            commissionAmount = 6000.0,
                            paymentMethod = PaymentMethod.SINPE,
                            notes = "Pago confirmado vía SINPE",
                            timestamp = oneHourAgo
                        )
                    )
                    db.transactionDao().insertTransaction(
                        TransactionRecord(
                            ticketNumber = 1003,
                            barberId = barberIds[0],
                            barberName = "Carlos Morales",
                            serviceId = serviceIds[1],
                            serviceName = "Perfilado de Barba",
                            totalAmount = 4000.0,
                            retentionAmount = 1500.0,
                            commissionAmount = 2500.0,
                            paymentMethod = PaymentMethod.EFECTIVO,
                            notes = "",
                            timestamp = now
                        )
                    )
                }
            }
        }
    }
}
