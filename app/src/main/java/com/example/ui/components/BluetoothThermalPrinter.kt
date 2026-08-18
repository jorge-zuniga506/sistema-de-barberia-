package com.example.ui.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.example.data.model.AppSettings
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionRecord
import com.example.ui.viewmodel.BarberDetailedCommissionReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class BluetoothPrinterDevice(
    val name: String,
    val address: String,
    val isPaired: Boolean = true
)

sealed class PrintResult {
    object Success : PrintResult()
    data class Error(val message: String) : PrintResult()
}

object BluetoothThermalPrinter {

    // Standard Bluetooth Serial Port Profile (SPP) UUID
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // ESC/POS Commands
    private val CMD_INIT = byteArrayOf(0x1B, 0x40) // ESC @
    private val CMD_ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00) // ESC a 0
    private val CMD_ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01) // ESC a 1
    private val CMD_ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02) // ESC a 2
    private val CMD_BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01) // ESC E 1
    private val CMD_BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00) // ESC E 0
    private val CMD_DOUBLE_SIZE = byteArrayOf(0x1D, 0x21, 0x11) // GS ! 0x11
    private val CMD_DOUBLE_HEIGHT = byteArrayOf(0x1D, 0x21, 0x01) // GS ! 0x01
    private val CMD_NORMAL_SIZE = byteArrayOf(0x1D, 0x21, 0x00) // GS ! 0x00
    private val CMD_FEED_LINES = byteArrayOf(0x1B, 0x64, 0x03) // ESC d 3
    private val CMD_CUT_PAPER = byteArrayOf(0x1D, 0x56, 0x42, 0x00) // GS V 66 0

    /**
     * Check if necessary Bluetooth permissions are granted.
     */
    fun hasBluetoothPermissions(context: Context): Boolean {
        return BluetoothPermissionHelper.hasAllBluetoothPermissions(context)
    }

    /**
     * Get list of paired Bluetooth devices.
     */
    @SuppressLint("MissingPermission")
    fun getPairedPrinters(context: Context): List<BluetoothPrinterDevice> {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()

        return try {
            adapter.bondedDevices?.map { device ->
                BluetoothPrinterDevice(
                    name = device.name ?: "Dispositivo desconocido",
                    address = device.address,
                    isPaired = true
                )
            } ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Build ESC/POS bytes for a fully customized customer transaction receipt.
     */
    fun buildTransactionReceiptBytes(
        transaction: TransactionRecord,
        settings: AppSettings
    ): ByteArray {
        val cols = if (settings.printerPaperWidth == "80mm") 48 else 32
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) {
            val bytes = text.toByteArray(charset("ISO-8859-1"))
            stream.write(bytes)
        }
        fun writeLine(text: String = "") {
            writeText(text)
            stream.write(byteArrayOf(0x0A))
        }

        // 1. Initialize
        write(CMD_INIT)

        // 2. Header & Branding (Center)
        write(CMD_ALIGN_CENTER)
        write(CMD_BOLD_ON)
        write(CMD_DOUBLE_SIZE)
        writeLine(settings.shopName.uppercase())
        write(CMD_NORMAL_SIZE)
        write(CMD_BOLD_OFF)

        if (settings.showSlogan && settings.shopSlogan.isNotBlank()) {
            writeLine(settings.shopSlogan)
        }

        if (settings.showAddress && settings.address.isNotBlank()) {
            writeLine(settings.address)
        }

        if (settings.showPhone && settings.contactPhone.isNotBlank()) {
            writeLine("Tel/WhatsApp: ${settings.contactPhone}")
        }

        if (settings.showInstagram && settings.instagram.isNotBlank()) {
            write(CMD_BOLD_ON)
            writeLine("Instagram: ${settings.instagram}")
            write(CMD_BOLD_OFF)
        }

        if (settings.showFacebookOrWeb && settings.facebookOrWeb.isNotBlank()) {
            writeLine("Web: ${settings.facebookOrWeb}")
        }

        if (settings.showSinpe && settings.sinpePhone.isNotBlank()) {
            write(CMD_BOLD_ON)
            writeLine("SINPE Móvil: ${settings.sinpePhone}")
            write(CMD_BOLD_OFF)
        }

        if (settings.showWifi && settings.wifiInfo.isNotBlank()) {
            writeLine(settings.wifiInfo)
        }

        writeLine(divider(cols))

        // 3. Greeting / Ticket Info
        if (settings.headerGreeting.isNotBlank()) {
            write(CMD_BOLD_ON)
            writeLine(settings.headerGreeting)
            write(CMD_BOLD_OFF)
        }

        if (settings.showTicketNumber) {
            write(CMD_BOLD_ON)
            write(CMD_DOUBLE_HEIGHT)
            writeLine("TICKET #${transaction.ticketNumber}")
            write(CMD_NORMAL_SIZE)
            write(CMD_BOLD_OFF)
        }

        if (settings.showDateTime) {
            writeLine(SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp)))
        }

        writeLine(divider(cols))

        // 4. Details (Left align)
        write(CMD_ALIGN_LEFT)
        if (settings.showBarberName) {
            writeLine(twoColumns("Barbero:", transaction.barberName, cols))
        }
        writeLine(twoColumns("Servicio:", transaction.serviceName, cols))
        if (transaction.notes.isNotBlank()) {
            writeLine("Nota: ${transaction.notes}")
        }

        writeLine(divider(cols, '='))

        // 5. Total
        write(CMD_BOLD_ON)
        write(CMD_DOUBLE_HEIGHT)
        val formattedTotal = ThermalReceiptUtils.formatCurrency(transaction.totalAmount, settings.currencySymbol)
        writeLine(twoColumns("TOTAL:", formattedTotal, cols))
        write(CMD_NORMAL_SIZE)
        write(CMD_BOLD_OFF)

        writeLine(divider(cols, '-'))

        // 6. Payment method
        val paymentMethodStr = if (transaction.paymentMethod == PaymentMethod.SINPE) "SINPE MÓVIL" else "EFECTIVO"
        write(CMD_BOLD_ON)
        writeLine(twoColumns("Forma de Pago:", paymentMethodStr, cols))
        write(CMD_BOLD_OFF)

        writeLine(divider(cols))

        // 7. Policy & Footer
        write(CMD_ALIGN_CENTER)
        if (settings.showCustomPolicy && settings.customPolicy.isNotBlank()) {
            writeLine(settings.customPolicy)
            writeLine("")
        }

        if (settings.ticketFooter.isNotBlank()) {
            write(CMD_BOLD_ON)
            writeLine(settings.ticketFooter)
            write(CMD_BOLD_OFF)
        }

        // 8. Feed and Cut
        writeLine("")
        writeLine("")
        writeLine("")
        write(CMD_FEED_LINES)
        write(CMD_CUT_PAPER)

        return stream.toByteArray()
    }

    /**
     * Build ESC/POS bytes for Barber Commission Liquidation Statement.
     */
    fun buildBarberCommissionReportBytes(
        report: BarberDetailedCommissionReport,
        settings: AppSettings
    ): ByteArray {
        val cols = if (settings.printerPaperWidth == "80mm") 48 else 32
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) {
            val bytes = text.toByteArray(charset("ISO-8859-1"))
            stream.write(bytes)
        }
        fun writeLine(text: String = "") {
            writeText(text)
            stream.write(byteArrayOf(0x0A))
        }

        write(CMD_INIT)

        // Header
        write(CMD_ALIGN_CENTER)
        write(CMD_BOLD_ON)
        write(CMD_DOUBLE_SIZE)
        writeLine(settings.shopName.uppercase())
        write(CMD_NORMAL_SIZE)
        writeLine("LIQUIDACIÓN DE COMISIONES")
        write(CMD_BOLD_OFF)
        writeLine(SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date()))
        writeLine(divider(cols))

        // Barber info & Period
        write(CMD_ALIGN_LEFT)
        writeLine(twoColumns("Barbero:", report.barber.name, cols))
        writeLine(twoColumns("Periodo:", report.period.label, cols))
        writeLine(twoColumns("Total Servicios:", "${report.totalServices} cortes", cols))
        writeLine(divider(cols, '='))

        // Financial Summary
        write(CMD_BOLD_ON)
        write(CMD_DOUBLE_HEIGHT)
        writeLine(twoColumns("COMISION GANADA:", ThermalReceiptUtils.formatCurrency(report.totalCommissions, settings.currencySymbol), cols))
        write(CMD_NORMAL_SIZE)
        write(CMD_BOLD_OFF)
        writeLine(twoColumns("Retencion Local:", ThermalReceiptUtils.formatCurrency(report.totalRetention, settings.currencySymbol), cols))
        writeLine(twoColumns("Total Facturado:", ThermalReceiptUtils.formatCurrency(report.totalRevenue, settings.currencySymbol), cols))

        writeLine(divider(cols))

        // Performance by Service
        write(CMD_BOLD_ON)
        writeLine("DETALLE POR SERVICIO:")
        write(CMD_BOLD_OFF)

        report.servicePerformances.forEach { perf ->
            writeLine("${perf.count}x ${perf.serviceName}")
            val comStr = ThermalReceiptUtils.formatCurrency(perf.totalCommission, settings.currencySymbol)
            val unitStr = ThermalReceiptUtils.formatCurrency(perf.unitCommission, settings.currencySymbol)
            writeLine(twoColumns("  Unit: $unitStr", "Sub: $comStr", cols))
        }

        writeLine(divider(cols, '-'))
        write(CMD_BOLD_ON)
        writeLine("MÉTODOS DE PAGO:")
        write(CMD_BOLD_OFF)
        writeLine(twoColumns("Efectivo:", ThermalReceiptUtils.formatCurrency(report.cashCommissions, settings.currencySymbol), cols))
        writeLine(twoColumns("SINPE Móvil:", ThermalReceiptUtils.formatCurrency(report.sinpeCommissions, settings.currencySymbol), cols))

        writeLine(divider(cols))
        write(CMD_ALIGN_CENTER)
        writeLine("Comprobante de Liquidación")
        writeLine("Firma Conforme: ____________________")

        writeLine("")
        writeLine("")
        writeLine("")
        write(CMD_FEED_LINES)
        write(CMD_CUT_PAPER)

        return stream.toByteArray()
    }

    /**
     * Build ESC/POS bytes for a test ticket.
     */
    fun buildTestTicketBytes(settings: AppSettings): ByteArray {
        val cols = if (settings.printerPaperWidth == "80mm") 48 else 32
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) {
            val bytes = text.toByteArray(charset("ISO-8859-1"))
            stream.write(bytes)
        }
        fun writeLine(text: String = "") {
            writeText(text)
            stream.write(byteArrayOf(0x0A))
        }

        write(CMD_INIT)
        write(CMD_ALIGN_CENTER)
        write(CMD_BOLD_ON)
        write(CMD_DOUBLE_SIZE)
        writeLine(settings.shopName.uppercase())
        write(CMD_NORMAL_SIZE)
        if (settings.showSlogan && settings.shopSlogan.isNotBlank()) {
            writeLine(settings.shopSlogan)
        }
        if (settings.showInstagram && settings.instagram.isNotBlank()) {
            writeLine("Instagram: ${settings.instagram}")
        }
        if (settings.showPhone && settings.contactPhone.isNotBlank()) {
            writeLine("Tel: ${settings.contactPhone}")
        }
        write(CMD_BOLD_OFF)

        writeLine("PRUEBA DE IMPRESIÓN")
        writeLine(SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date()))
        writeLine(divider(cols))

        write(CMD_ALIGN_LEFT)
        writeLine(twoColumns("Estado:", "Conectado OK", cols))
        writeLine(twoColumns("Ancho papel:", settings.printerPaperWidth, cols))
        writeLine(twoColumns("Impresora:", settings.printerName.ifBlank { "Térmica Bluetooth" }, cols))
        writeLine(twoColumns("MAC:", settings.printerMacAddress.ifBlank { "N/A" }, cols))

        writeLine(divider(cols, '='))
        write(CMD_ALIGN_CENTER)
        write(CMD_BOLD_ON)
        writeLine("¡CONFIGURACIÓN EXITOSA!")
        write(CMD_BOLD_OFF)
        if (settings.ticketFooter.isNotBlank()) {
            writeLine(settings.ticketFooter)
        }

        writeLine("")
        writeLine("")
        write(CMD_FEED_LINES)
        write(CMD_CUT_PAPER)

        return stream.toByteArray()
    }

    /**
     * Print byte array to Bluetooth device by MAC address.
     */
    @SuppressLint("MissingPermission")
    suspend fun printToBluetoothDevice(
        macAddress: String,
        data: ByteArray
    ): PrintResult = withContext(Dispatchers.IO) {
        if (macAddress.isBlank()) {
            return@withContext PrintResult.Error("No se ha configurado ninguna impresora Bluetooth.")
        }

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: return@withContext PrintResult.Error("El dispositivo no cuenta con adaptador Bluetooth.")

        if (!bluetoothAdapter.isEnabled) {
            return@withContext PrintResult.Error("El Bluetooth está apagado. Por favor enciéndalo.")
        }

        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null

        try {
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)

            try {
                bluetoothAdapter.cancelDiscovery()
            } catch (e: Exception) {
                // Ignore
            }

            socket = try {
                device.createRfcommSocketToServiceRecord(SPP_UUID)
            } catch (e: Exception) {
                val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                method.invoke(device, 1) as BluetoothSocket
            }

            socket.connect()
            outputStream = socket.outputStream

            outputStream.write(data)
            outputStream.flush()

            Thread.sleep(400)

            return@withContext PrintResult.Success
        } catch (e: SecurityException) {
            return@withContext PrintResult.Error("Permisos de Bluetooth denegados. Conceda los permisos requeridos.")
        } catch (e: Exception) {
            return@withContext PrintResult.Error("Error de conexión: ${e.localizedMessage ?: e.message ?: "No se pudo conectar con la impresora"}")
        } finally {
            try {
                outputStream?.close()
            } catch (e: Exception) {}
            try {
                socket?.close()
            } catch (e: Exception) {}
        }
    }

    private fun divider(length: Int, char: Char = '-'): String {
        return char.toString().repeat(length)
    }

    private fun twoColumns(left: String, right: String, totalWidth: Int): String {
        val maxLeft = totalWidth - right.length - 1
        val safeLeft = if (left.length > maxLeft && maxLeft > 0) left.substring(0, maxLeft) else left
        val spacesCount = totalWidth - safeLeft.length - right.length
        val spaces = if (spacesCount > 0) " ".repeat(spacesCount) else " "
        return safeLeft + spaces + right
    }
}
