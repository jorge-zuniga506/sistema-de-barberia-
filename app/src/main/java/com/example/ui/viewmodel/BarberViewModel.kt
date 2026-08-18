package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BarberDatabase
import com.example.data.model.AppSettings
import com.example.data.model.Barber
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceItem
import com.example.data.model.TransactionRecord
import com.example.data.repository.BarberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ReportPeriod(val label: String) {
    TODAY("Hoy"),
    THIS_WEEK("Esta Semana"),
    THIS_MONTH("Este Mes"),
    ALL("Histórico")
}

data class BarberServicePerformance(
    val serviceName: String,
    val count: Int,
    val averagePrice: Double,
    val unitCommission: Double,
    val totalCommission: Double,
    val totalRevenue: Double,
    val percentageOfTotal: Float = 0f
)

data class BarberCommissionSummary(
    val barberId: Long,
    val barberName: String,
    val serviceCount: Int,
    val totalRevenue: Double,
    val totalRetention: Double,
    val totalCommission: Double
)

data class BarberDetailedCommissionReport(
    val barber: Barber,
    val period: ReportPeriod,
    val totalServices: Int,
    val totalCommissions: Double,
    val totalRevenue: Double,
    val totalRetention: Double,
    val averageCommissionPerService: Double,
    val sinpeCommissions: Double,
    val cashCommissions: Double,
    val servicePerformances: List<BarberServicePerformance>,
    val transactions: List<TransactionRecord>
)

data class RegisterUiState(
    val selectedBarber: Barber? = null,
    val selectedService: ServiceItem? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val notes: String = "",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class BarberViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BarberRepository

    init {
        val db = BarberDatabase.getDatabase(application, viewModelScope)
        repository = BarberRepository(
            barberDao = db.barberDao(),
            serviceDao = db.serviceDao(),
            transactionDao = db.transactionDao(),
            appSettingsDao = db.appSettingsDao()
        )
    }

    // Repository Flows
    val barbers: StateFlow<List<Barber>> = repository.allBarbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBarbers: StateFlow<List<Barber>> = repository.activeBarbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<ServiceItem>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeServices: StateFlow<List<ServiceItem>> = repository.activeServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionRecord>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings> = repository.settings
        .map { it ?: AppSettings() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppSettings()
        )

    // Register Sale UI State
    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // Active Dialog Receipt (for newly created or viewed ticket)
    private val _viewingReceipt = MutableStateFlow<TransactionRecord?>(null)
    val viewingReceipt: StateFlow<TransactionRecord?> = _viewingReceipt.asStateFlow()

    // Bluetooth Printer Setup Dialog State
    private val _showBluetoothPrinterDialog = MutableStateFlow(false)
    val showBluetoothPrinterDialog: StateFlow<Boolean> = _showBluetoothPrinterDialog.asStateFlow()

    // Reports Filters
    val selectedPeriod = MutableStateFlow(ReportPeriod.TODAY)
    val filterBarberId = MutableStateFlow<Long?>(null) // null = all barbers
    val filterPaymentMethod = MutableStateFlow<PaymentMethod?>(null) // null = all methods

    // Dedicated Barber Commission History State
    val selectedBarberForCommissionDetail = MutableStateFlow<Barber?>(null)
    val commissionDetailPeriod = MutableStateFlow(ReportPeriod.THIS_WEEK)

    // Detailed Commission Report for Selected Barber Flow
    val barberCommissionDetailReport: StateFlow<BarberDetailedCommissionReport?> = combine(
        selectedBarberForCommissionDetail,
        commissionDetailPeriod,
        allTransactions
    ) { barber, period, txList ->
        if (barber == null) return@combine null

        val (start, end) = getPeriodBounds(period)
        val barberTxs = txList.filter { it.barberId == barber.id && it.timestamp in start..end }
            .sortedByDescending { it.timestamp }

        val totalServices = barberTxs.size
        val totalCommissions = barberTxs.sumOf { it.commissionAmount }
        val totalRevenue = barberTxs.sumOf { it.totalAmount }
        val totalRetention = barberTxs.sumOf { it.retentionAmount }
        val avgCommission = if (totalServices > 0) totalCommissions / totalServices else 0.0
        val sinpeCommissions = barberTxs.filter { it.paymentMethod == PaymentMethod.SINPE }.sumOf { it.commissionAmount }
        val cashCommissions = barberTxs.filter { it.paymentMethod == PaymentMethod.EFECTIVO }.sumOf { it.commissionAmount }

        // Group services performed by this barber
        val serviceGroups = barberTxs.groupBy { it.serviceName }
        val servicePerformances = serviceGroups.map { (serviceName, txs) ->
            val count = txs.size
            val serviceRevenue = txs.sumOf { it.totalAmount }
            val serviceCommission = txs.sumOf { it.commissionAmount }
            val avgPrice = if (count > 0) serviceRevenue / count else 0.0
            val unitCommission = if (count > 0) serviceCommission / count else 0.0
            val pct = if (totalCommissions > 0) (serviceCommission / totalCommissions).toFloat() else 0f
            BarberServicePerformance(
                serviceName = serviceName,
                count = count,
                averagePrice = avgPrice,
                unitCommission = unitCommission,
                totalCommission = serviceCommission,
                totalRevenue = serviceRevenue,
                percentageOfTotal = pct
            )
        }.sortedByDescending { it.count }

        BarberDetailedCommissionReport(
            barber = barber,
            period = period,
            totalServices = totalServices,
            totalCommissions = totalCommissions,
            totalRevenue = totalRevenue,
            totalRetention = totalRetention,
            averageCommissionPerService = avgCommission,
            sinpeCommissions = sinpeCommissions,
            cashCommissions = cashCommissions,
            servicePerformances = servicePerformances,
            transactions = barberTxs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered Transactions Flow
    val filteredTransactions: StateFlow<List<TransactionRecord>> = combine(
        allTransactions,
        selectedPeriod,
        filterBarberId,
        filterPaymentMethod
    ) { txList, period, barberId, payment ->
        val (start, end) = getPeriodBounds(period)
        txList.filter { tx ->
            val matchesPeriod = tx.timestamp in start..end
            val matchesBarber = barberId == null || tx.barberId == barberId
            val matchesPayment = payment == null || tx.paymentMethod == payment
            matchesPeriod && matchesBarber && matchesPayment
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Barber Summaries Breakdown
    val barberSummaries: StateFlow<List<BarberCommissionSummary>> = filteredTransactions.combine(barbers) { txList, allBarberList ->
        val map = mutableMapOf<Long, BarberCommissionSummary>()
        txList.forEach { tx ->
            val current = map[tx.barberId] ?: BarberCommissionSummary(
                barberId = tx.barberId,
                barberName = tx.barberName,
                serviceCount = 0,
                totalRevenue = 0.0,
                totalRetention = 0.0,
                totalCommission = 0.0
            )
            map[tx.barberId] = current.copy(
                serviceCount = current.serviceCount + 1,
                totalRevenue = current.totalRevenue + tx.totalAmount,
                totalRetention = current.totalRetention + tx.retentionAmount,
                totalCommission = current.totalCommission + tx.commissionAmount
            )
        }
        map.values.sortedByDescending { it.totalRevenue }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Stats for Dashboard
    val todayStats = allTransactions.combine(settings) { txList, _ ->
        val (startToday, endToday) = getPeriodBounds(ReportPeriod.TODAY)
        val todayTxs = txList.filter { it.timestamp in startToday..endToday }
        val totalRevenue = todayTxs.sumOf { it.totalAmount }
        val totalRetention = todayTxs.sumOf { it.retentionAmount }
        val totalCommission = todayTxs.sumOf { it.commissionAmount }
        val count = todayTxs.size
        val sinpeCount = todayTxs.count { it.paymentMethod == PaymentMethod.SINPE }
        val cashCount = todayTxs.count { it.paymentMethod == PaymentMethod.EFECTIVO }
        TodayMetrics(
            totalRevenue = totalRevenue,
            totalRetention = totalRetention,
            totalCommission = totalCommission,
            count = count,
            sinpeCount = sinpeCount,
            cashCount = cashCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayMetrics())

    // Register Form Actions
    fun selectBarber(barber: Barber) {
        _registerState.update { it.copy(selectedBarber = barber, errorMessage = null) }
    }

    fun selectService(service: ServiceItem) {
        _registerState.update { it.copy(selectedService = service, errorMessage = null) }
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _registerState.update { it.copy(paymentMethod = method) }
    }

    fun setNotes(notes: String) {
        _registerState.update { it.copy(notes = notes) }
    }

    fun resetRegisterForm() {
        _registerState.value = RegisterUiState()
    }

    fun registerCurrentSale() {
        val state = _registerState.value
        val barber = state.selectedBarber
        val service = state.selectedService

        if (barber == null) {
            _registerState.update { it.copy(errorMessage = "Selecciona un barbero") }
            return
        }
        if (service == null) {
            _registerState.update { it.copy(errorMessage = "Selecciona un servicio") }
            return
        }

        viewModelScope.launch {
            try {
                val transaction = repository.registerSale(
                    barber = barber,
                    service = service,
                    paymentMethod = state.paymentMethod,
                    notes = state.notes
                )
                // Show receipt popup
                _viewingReceipt.value = transaction
                // Reset form
                _registerState.value = RegisterUiState()
            } catch (e: Exception) {
                _registerState.update { it.copy(errorMessage = "Error al registrar venta: ${e.message}") }
            }
        }
    }

    fun showReceipt(transaction: TransactionRecord) {
        _viewingReceipt.value = transaction
    }

    fun closeReceipt() {
        _viewingReceipt.value = null
    }

    fun openBluetoothPrinterDialog() {
        _showBluetoothPrinterDialog.value = true
    }

    fun closeBluetoothPrinterDialog() {
        _showBluetoothPrinterDialog.value = false
    }

    fun updatePrinterSettings(mac: String, name: String, paperWidth: String) {
        viewModelScope.launch {
            val current = repository.getSettingsOnce()
            val updated = current.copy(
                printerMacAddress = mac.trim(),
                printerName = name.trim(),
                printerPaperWidth = paperWidth.trim()
            )
            repository.saveSettings(updated)
        }
    }

    fun openBarberCommissionDetail(barber: Barber, period: ReportPeriod = ReportPeriod.THIS_WEEK) {
        selectedBarberForCommissionDetail.value = barber
        commissionDetailPeriod.value = period
    }

    fun setCommissionDetailPeriod(period: ReportPeriod) {
        commissionDetailPeriod.value = period
    }

    fun closeBarberCommissionDetail() {
        selectedBarberForCommissionDetail.value = null
    }

    fun deleteTransaction(transaction: TransactionRecord) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            if (_viewingReceipt.value?.id == transaction.id) {
                _viewingReceipt.value = null
            }
        }
    }

    // Barber CRUD
    fun saveBarber(name: String, phone: String, existingBarber: Barber? = null) {
        if (name.isBlank()) return
        viewModelScope.launch {
            if (existingBarber != null) {
                repository.updateBarber(existingBarber.copy(name = name.trim(), phone = phone.trim()))
            } else {
                repository.insertBarber(Barber(name = name.trim(), phone = phone.trim(), isActive = true))
            }
        }
    }

    fun toggleBarberStatus(barber: Barber) {
        viewModelScope.launch {
            repository.updateBarber(barber.copy(isActive = !barber.isActive))
        }
    }

    fun deleteBarber(barber: Barber) {
        viewModelScope.launch {
            repository.deleteBarber(barber)
        }
    }

    // Service CRUD
    fun saveService(
        name: String,
        price: Double,
        customRetention: Double?,
        category: String,
        existingService: ServiceItem? = null
    ) {
        if (name.isBlank() || price <= 0.0) return
        viewModelScope.launch {
            if (existingService != null) {
                repository.updateService(
                    existingService.copy(
                        name = name.trim(),
                        price = price,
                        customRetention = customRetention,
                        category = category.trim()
                    )
                )
            } else {
                repository.insertService(
                    ServiceItem(
                        name = name.trim(),
                        price = price,
                        customRetention = customRetention,
                        category = category.trim(),
                        isActive = true
                    )
                )
            }
        }
    }

    fun toggleServiceStatus(service: ServiceItem) {
        viewModelScope.launch {
            repository.updateService(service.copy(isActive = !service.isActive))
        }
    }

    fun deleteService(service: ServiceItem) {
        viewModelScope.launch {
            repository.deleteService(service)
        }
    }

    // App Settings
    fun updateSettings(
        shopName: String,
        sinpePhone: String,
        address: String,
        defaultRetention: Double,
        currencySymbol: String,
        ticketFooter: String
    ) {
        viewModelScope.launch {
            val current = repository.getSettingsOnce()
            val updated = current.copy(
                id = 1,
                shopName = shopName.trim(),
                sinpePhone = sinpePhone.trim(),
                address = address.trim(),
                defaultRetention = defaultRetention,
                currencySymbol = currencySymbol.trim(),
                ticketFooter = ticketFooter.trim()
            )
            repository.saveSettings(updated)
        }
    }

    fun saveFullAppSettings(updatedSettings: AppSettings) {
        viewModelScope.launch {
            repository.saveSettings(updatedSettings.copy(id = 1))
        }
    }

    fun updateTicketDesign(
        shopSlogan: String,
        contactPhone: String,
        instagram: String,
        facebookOrWeb: String,
        wifiInfo: String,
        headerGreeting: String,
        ticketFooter: String,
        customPolicy: String,
        showSlogan: Boolean,
        showSinpe: Boolean,
        showPhone: Boolean,
        showInstagram: Boolean,
        showFacebookOrWeb: Boolean,
        showAddress: Boolean,
        showWifi: Boolean,
        showCustomPolicy: Boolean,
        showBarberName: Boolean,
        showDateTime: Boolean,
        showTicketNumber: Boolean
    ) {
        viewModelScope.launch {
            val current = repository.getSettingsOnce()
            val updated = current.copy(
                shopSlogan = shopSlogan.trim(),
                contactPhone = contactPhone.trim(),
                instagram = instagram.trim(),
                facebookOrWeb = facebookOrWeb.trim(),
                wifiInfo = wifiInfo.trim(),
                headerGreeting = headerGreeting.trim(),
                ticketFooter = ticketFooter.trim(),
                customPolicy = customPolicy.trim(),
                showSlogan = showSlogan,
                showSinpe = showSinpe,
                showPhone = showPhone,
                showInstagram = showInstagram,
                showFacebookOrWeb = showFacebookOrWeb,
                showAddress = showAddress,
                showWifi = showWifi,
                showCustomPolicy = showCustomPolicy,
                showBarberName = showBarberName,
                showDateTime = showDateTime,
                showTicketNumber = showTicketNumber
            )
            repository.saveSettings(updated)
        }
    }

    private fun getPeriodBounds(period: ReportPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis

        when (period) {
            ReportPeriod.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return Pair(cal.timeInMillis, end)
            }
            ReportPeriod.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return Pair(cal.timeInMillis, end)
            }
            ReportPeriod.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return Pair(cal.timeInMillis, end)
            }
            ReportPeriod.ALL -> {
                return Pair(0L, Long.MAX_VALUE)
            }
        }
    }
}

data class TodayMetrics(
    val totalRevenue: Double = 0.0,
    val totalRetention: Double = 0.0,
    val totalCommission: Double = 0.0,
    val count: Int = 0,
    val sinpeCount: Int = 0,
    val cashCount: Int = 0
)
