package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PaymentMethod(val displayName: String) {
    EFECTIVO("Efectivo"),
    SINPE("SINPE Móvil")
}

@Entity(tableName = "barbers")
data class Barber(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class ServiceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val customRetention: Double? = null,
    val category: String = "Corte",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ticketNumber: Long,
    val barberId: Long,
    val barberName: String,
    val serviceId: Long,
    val serviceName: String,
    val totalAmount: Double,
    val retentionAmount: Double, // Ganancia que corresponde al local
    val commissionAmount: Double, // Ganancia que corresponde al barbero
    val paymentMethod: PaymentMethod,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 1,
    val shopName: String = "Barbería Estilo & Arte",
    val shopSlogan: String = "Estilo, Corte & Tradición",
    val sinpePhone: String = "8888-2424",
    val contactPhone: String = "2222-3344",
    val instagram: String = "@barberia_estiloyarte",
    val facebookOrWeb: String = "fb.me/barberiaestilo",
    val address: String = "Av. Central, San José",
    val wifiInfo: String = "WiFi: BarberiaPass",
    val defaultRetention: Double = 2000.0,
    val currencySymbol: String = "₡",
    val headerGreeting: String = "COMPROBANTE DE PAGO",
    val ticketFooter: String = "¡Gracias por su preferencia! Calidad y estilo en cada corte.",
    val customPolicy: String = "Conserve su ticket para cualquier consulta",
    
    // Toggles for customizable ticket printing
    val showSlogan: Boolean = true,
    val showSinpe: Boolean = true,
    val showPhone: Boolean = true,
    val showInstagram: Boolean = true,
    val showFacebookOrWeb: Boolean = false,
    val showAddress: Boolean = true,
    val showWifi: Boolean = true,
    val showCustomPolicy: Boolean = true,
    val showBarberName: Boolean = true,
    val showDateTime: Boolean = true,
    val showTicketNumber: Boolean = true,

    // Bluetooth Printer Settings
    val printerMacAddress: String = "",
    val printerName: String = "",
    val printerPaperWidth: String = "58mm", // "58mm" or "80mm"
    val autoPrintReceipt: Boolean = false
)
