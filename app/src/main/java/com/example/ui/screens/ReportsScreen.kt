package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppSettings
import com.example.data.model.Barber
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionRecord
import com.example.ui.components.ThermalReceiptUtils
import com.example.ui.theme.CashBlue
import com.example.ui.theme.CommissionPurple
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.RetentionOrange
import com.example.ui.theme.SinpeGreen
import com.example.ui.theme.SophisticatedBg
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryHero
import com.example.ui.theme.SophisticatedPrimaryHeroLight
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.viewmodel.BarberCommissionSummary
import com.example.ui.viewmodel.BarberViewModel
import com.example.ui.viewmodel.ReportPeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: BarberViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val barberSummaries by viewModel.barberSummaries.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val filterBarberId by viewModel.filterBarberId.collectAsStateWithLifecycle()
    val filterPaymentMethod by viewModel.filterPaymentMethod.collectAsStateWithLifecycle()
    val barbers by viewModel.barbers.collectAsStateWithLifecycle()

    var transactionToDelete by remember { mutableStateOf<TransactionRecord?>(null) }
    var selectedReportTab by remember { mutableStateOf(0) } // 0 = Resumen & Transacciones, 1 = Comisiones por Barbero

    val totalFacturado = filteredTransactions.sumOf { it.totalAmount }
    val totalRetencion = filteredTransactions.sumOf { it.retentionAmount }
    val totalComisiones = filteredTransactions.sumOf { it.commissionAmount }
    val sinpeTotal = filteredTransactions.filter { it.paymentMethod == PaymentMethod.SINPE }.sumOf { it.totalAmount }
    val cashTotal = filteredTransactions.filter { it.paymentMethod == PaymentMethod.EFECTIVO }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedBg)
            .testTag("reports_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        // Header & Share Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RESÚMENES & MÉTRICAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp,
                        color = SophisticatedPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Reportes & Cuadre",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        shareReportSummary(
                            context = context,
                            settings = settings,
                            period = selectedPeriod,
                            totalFacturado = totalFacturado,
                            totalRetencion = totalRetencion,
                            totalComisiones = totalComisiones,
                            sinpeTotal = sinpeTotal,
                            cashTotal = cashTotal,
                            count = filteredTransactions.size,
                            barberSummaries = barberSummaries
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = SophisticatedOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("export_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir Cuadre",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cuadre", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Period Selector Tabs
        item {
            Surface(
                color = SophisticatedSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SophisticatedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = ReportPeriod.values().indexOf(selectedPeriod),
                    containerColor = Color.Transparent,
                    contentColor = SophisticatedPrimary,
                    divider = {}
                ) {
                    ReportPeriod.values().forEach { period ->
                        val isSelected = selectedPeriod == period
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectedPeriod.value = period },
                            text = {
                                Text(
                                    text = period.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SophisticatedPrimary else SophisticatedTextSecondary
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Additional Filter Chips (Barber & Payment)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // All Barbers chip
                item {
                    FilterChip(
                        selected = filterBarberId == null,
                        onClick = { viewModel.filterBarberId.value = null },
                        label = { Text("Todos los Barberos", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = SophisticatedSurface,
                            selectedContainerColor = SophisticatedPrimaryHero,
                            selectedLabelColor = SophisticatedPrimary,
                            labelColor = SophisticatedTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterBarberId == null,
                            borderColor = SophisticatedBorder,
                            selectedBorderColor = SophisticatedPrimary
                        )
                    )
                }
                // Individual Barbers
                items(barbers, key = { it.id }) { barber ->
                    val isSel = filterBarberId == barber.id
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            viewModel.filterBarberId.value = if (isSel) null else barber.id
                        },
                        label = { Text(barber.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = SophisticatedSurface,
                            selectedContainerColor = SophisticatedPrimaryHero,
                            selectedLabelColor = SophisticatedPrimary,
                            labelColor = SophisticatedTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSel,
                            borderColor = SophisticatedBorder,
                            selectedBorderColor = SophisticatedPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isAllPayments = filterPaymentMethod == null
                FilterChip(
                    selected = isAllPayments,
                    onClick = { viewModel.filterPaymentMethod.value = null },
                    label = { Text("Todos los Pagos", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SophisticatedSurface,
                        selectedContainerColor = SophisticatedPrimaryHero,
                        selectedLabelColor = SophisticatedPrimary,
                        labelColor = SophisticatedTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isAllPayments,
                        borderColor = SophisticatedBorder,
                        selectedBorderColor = SophisticatedPrimary
                    )
                )
                val isCash = filterPaymentMethod == PaymentMethod.EFECTIVO
                FilterChip(
                    selected = isCash,
                    onClick = {
                        viewModel.filterPaymentMethod.value =
                            if (isCash) null else PaymentMethod.EFECTIVO
                    },
                    label = { Text("Sólo Efectivo", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SophisticatedSurface,
                        selectedContainerColor = CashBlue.copy(alpha = 0.2f),
                        selectedLabelColor = CashBlue,
                        labelColor = SophisticatedTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isCash,
                        borderColor = SophisticatedBorder,
                        selectedBorderColor = CashBlue
                    )
                )
                val isSinpe = filterPaymentMethod == PaymentMethod.SINPE
                FilterChip(
                    selected = isSinpe,
                    onClick = {
                        viewModel.filterPaymentMethod.value =
                            if (isSinpe) null else PaymentMethod.SINPE
                    },
                    label = { Text("Sólo SINPE", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SophisticatedSurface,
                        selectedContainerColor = SinpeGreen.copy(alpha = 0.2f),
                        selectedLabelColor = SinpeGreen,
                        labelColor = SophisticatedTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSinpe,
                        borderColor = SophisticatedBorder,
                        selectedBorderColor = SinpeGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Financial Summary Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Balance del Período (${selectedPeriod.label})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Facturación Total", fontSize = 12.sp, color = SophisticatedTextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ThermalReceiptUtils.formatCurrency(totalFacturado, settings.currencySymbol),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Servicios", fontSize = 12.sp, color = SophisticatedTextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${filteredTransactions.size}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedPrimary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = SophisticatedBorder)

                    // Breakdown Local vs Barbero
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(RetentionOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ganancia Local:", fontSize = 13.sp, color = SophisticatedTextSecondary)
                        }
                        Text(
                            text = ThermalReceiptUtils.formatCurrency(totalRetencion, settings.currencySymbol),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RetentionOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SophisticatedPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Comisiones Barberos:", fontSize = 13.sp, color = SophisticatedTextSecondary)
                        }
                        Text(
                            text = ThermalReceiptUtils.formatCurrency(totalComisiones, settings.currencySymbol),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedPrimary
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SophisticatedBorder)

                    // Totals by payment method
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Efectivo: ${ThermalReceiptUtils.formatCurrency(cashTotal, settings.currencySymbol)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CashBlue
                        )
                        Text(
                            text = "SINPE: ${ThermalReceiptUtils.formatCurrency(sinpeTotal, settings.currencySymbol)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SinpeGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Sub-Tabs: 0 = Historial de Transacciones, 1 = Liquidación por Barbero
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { selectedReportTab = 0 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedReportTab == 0) SophisticatedPrimaryHero else SophisticatedSurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedReportTab == 0) SophisticatedPrimary else SophisticatedBorder
                    )
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = if (selectedReportTab == 0) SophisticatedPrimary else SophisticatedTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tickets (${filteredTransactions.size})", fontSize = 12.sp, color = if (selectedReportTab == 0) SophisticatedPrimary else SophisticatedTextSecondary, fontWeight = if (selectedReportTab == 0) FontWeight.Bold else FontWeight.Normal)
                }

                OutlinedButton(
                    onClick = { selectedReportTab = 1 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedReportTab == 1) SophisticatedPrimaryHero else SophisticatedSurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedReportTab == 1) SophisticatedPrimary else SophisticatedBorder
                    )
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = if (selectedReportTab == 1) SophisticatedPrimary else SophisticatedTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Por Barbero (${barberSummaries.size})", fontSize = 12.sp, color = if (selectedReportTab == 1) SophisticatedPrimary else SophisticatedTextSecondary, fontWeight = if (selectedReportTab == 1) FontWeight.Bold else FontWeight.Normal)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Content for Tab 0: Transacciones detalladas
        if (selectedReportTab == 0) {
            if (filteredTransactions.isEmpty()) {
                item {
                    Text(
                        text = "No se encontraron transacciones para los filtros seleccionados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SophisticatedTextSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(filteredTransactions, key = { it.id }) { tx ->
                    ReportTransactionItem(
                        transaction = tx,
                        currencySymbol = settings.currencySymbol,
                        onViewReceipt = { viewModel.showReceipt(tx) },
                        onDelete = { transactionToDelete = tx }
                    )
                }
            }
        } else {
            // Content for Tab 1: Comisiones por Barbero
            if (barberSummaries.isEmpty()) {
                item {
                    Text(
                        text = "No hay registros de comisiones para este período.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SophisticatedTextSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(barberSummaries, key = { it.barberId }) { summary ->
                    val barberObj = barbers.find { it.id == summary.barberId } 
                        ?: Barber(id = summary.barberId, name = summary.barberName)
                    BarberSummaryCard(
                        summary = summary,
                        currencySymbol = settings.currencySymbol,
                        onViewDetail = {
                            viewModel.openBarberCommissionDetail(barberObj, selectedPeriod)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (transactionToDelete != null) {
        val tx = transactionToDelete!!
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            containerColor = SophisticatedSurface,
            titleContentColor = Color.White,
            textContentColor = SophisticatedTextSecondary,
            title = { Text("¿Eliminar Transacción?") },
            text = {
                Text(
                    "¿Estás seguro de anular el ticket #${tx.ticketNumber} (${tx.serviceName} - ${ThermalReceiptUtils.formatCurrency(tx.totalAmount, settings.currencySymbol)})?\n\nEsta acción ajustará los totales del cuadre."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancelar", color = SophisticatedTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ReportTransactionItem(
    transaction: TransactionRecord,
    currencySymbol: String,
    onViewReceipt: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SophisticatedSurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${transaction.ticketNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SophisticatedPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.serviceName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "${transaction.barberName} • ${SimpleDateFormat("dd/MM hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp))}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = SophisticatedTextSecondary
                    )
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = "Comisión: ${ThermalReceiptUtils.formatCurrency(transaction.commissionAmount, currencySymbol)}",
                            fontSize = 11.sp,
                            color = SophisticatedPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = " | Ret: ${ThermalReceiptUtils.formatCurrency(transaction.retentionAmount, currencySymbol)}",
                            fontSize = 11.sp,
                            color = RetentionOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = ThermalReceiptUtils.formatCurrency(transaction.totalAmount, currencySymbol),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Surface(
                        color = if (transaction.paymentMethod == PaymentMethod.SINPE) SinpeGreen.copy(alpha = 0.15f) else CashBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (transaction.paymentMethod == PaymentMethod.SINPE) "SINPE" else "EFECTIVO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.paymentMethod == PaymentMethod.SINPE) SinpeGreen else CashBlue,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onViewReceipt,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Ver Ticket",
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = ErrorRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BarberSummaryCard(
    summary: BarberCommissionSummary,
    currencySymbol: String,
    onViewDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = SophisticatedSurface
        ),
        border = BorderStroke(1.dp, SophisticatedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SophisticatedPrimaryHero),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = summary.barberName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SophisticatedPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = summary.barberName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${summary.serviceCount} servicios realizados",
                            fontSize = 11.sp,
                            color = SophisticatedTextSecondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "A Pagar:",
                        fontSize = 11.sp,
                        color = SophisticatedTextSecondary
                    )
                    Text(
                        text = ThermalReceiptUtils.formatCurrency(summary.totalCommission, currencySymbol),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = SophisticatedPrimary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SophisticatedBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Facturación: ${ThermalReceiptUtils.formatCurrency(summary.totalRevenue, currencySymbol)}",
                        fontSize = 11.sp,
                        color = SophisticatedTextSecondary
                    )
                    Text(
                        text = "Retención Local: ${ThermalReceiptUtils.formatCurrency(summary.totalRetention, currencySymbol)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RetentionOrange
                    )
                }

                Surface(
                    color = SophisticatedPrimaryHero,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SophisticatedPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { onViewDetail() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ver Historial",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = SophisticatedPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun shareReportSummary(
    context: android.content.Context,
    settings: AppSettings,
    period: ReportPeriod,
    totalFacturado: Double,
    totalRetencion: Double,
    totalComisiones: Double,
    sinpeTotal: Double,
    cashTotal: Double,
    count: Int,
    barberSummaries: List<BarberCommissionSummary>
) {
    val date = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date())
    val text = buildString {
        appendLine("📊 *CUADRE DE CAJA - ${settings.shopName.uppercase()}*")
        appendLine("📅 Período: ${period.label} | Generado: $date")
        appendLine("--------------------------------")
        appendLine("💈 *Total Servicios:* $count")
        appendLine("💰 *Facturación Total:* ${ThermalReceiptUtils.formatCurrency(totalFacturado, settings.currencySymbol)}")
        appendLine("🏢 *Ganancia Local (Retención):* ${ThermalReceiptUtils.formatCurrency(totalRetencion, settings.currencySymbol)}")
        appendLine("✂️ *Comisiones Barberos:* ${ThermalReceiptUtils.formatCurrency(totalComisiones, settings.currencySymbol)}")
        appendLine("--------------------------------")
        appendLine("💵 *Efectivo en Caja:* ${ThermalReceiptUtils.formatCurrency(cashTotal, settings.currencySymbol)}")
        appendLine("📱 *SINPE Móvil:* ${ThermalReceiptUtils.formatCurrency(sinpeTotal, settings.currencySymbol)}")
        appendLine("--------------------------------")
        appendLine("👥 *LIQUIDACIÓN POR BARBERO:*")
        barberSummaries.forEach { b ->
            appendLine("• *${b.barberName}* (${b.serviceCount} cortes)")
            appendLine("  Comisión: ${ThermalReceiptUtils.formatCurrency(b.totalCommission, settings.currencySymbol)} | Retenido: ${ThermalReceiptUtils.formatCurrency(b.totalRetention, settings.currencySymbol)}")
        }
        appendLine("--------------------------------")
        appendLine("¡Reporte generado desde Barbería App!")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Compartir Cuadre de Caja"))
}
