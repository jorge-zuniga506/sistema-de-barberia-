package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppSettings
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionRecord
import com.example.ui.theme.CashBlue
import com.example.ui.theme.RetentionOrange
import com.example.ui.theme.SinpeGreen
import com.example.ui.theme.SophisticatedBg
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryHero
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.viewmodel.BarberDetailedCommissionReport
import com.example.ui.viewmodel.BarberServicePerformance
import com.example.ui.viewmodel.ReportPeriod
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import android.widget.Toast

@Composable
fun BarberCommissionDetailDialog(
    report: BarberDetailedCommissionReport,
    settings: AppSettings,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onViewReceipt: (TransactionRecord) -> Unit,
    onConfigurePrinter: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPrinting by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("barber_commission_detail_dialog"),
            colors = CardDefaults.cardColors(containerColor = SophisticatedBg),
            border = BorderStroke(1.dp, SophisticatedBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top App Bar Header
                Surface(
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SophisticatedPrimaryHero),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = report.barber.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SophisticatedPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "LIQUIDACIÓN & COMISIONES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.4.sp,
                                    color = SophisticatedPrimary
                                )
                                Text(
                                    text = report.barber.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (report.barber.phone.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = SophisticatedTextSecondary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = report.barber.phone,
                                            fontSize = 11.sp,
                                            color = SophisticatedTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_barber_commission_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = SophisticatedTextSecondary
                            )
                        }
                    }
                }

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Period Selector Tabs
                    item {
                        Surface(
                            color = SophisticatedSurface,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TabRow(
                                selectedTabIndex = ReportPeriod.values().indexOf(report.period),
                                containerColor = Color.Transparent,
                                contentColor = SophisticatedPrimary,
                                divider = {}
                            ) {
                                ReportPeriod.values().forEach { period ->
                                    val isSelected = report.period == period
                                    Tab(
                                        selected = isSelected,
                                        onClick = { onPeriodSelected(period) },
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

                    // Hero Total Commission Summary Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                            border = BorderStroke(1.dp, SophisticatedPrimary.copy(alpha = 0.5f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL COMISIONES GANADAS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp,
                                            color = SophisticatedPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ThermalReceiptUtils.formatCurrency(report.totalCommissions, settings.currencySymbol),
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }

                                    Surface(
                                        color = SophisticatedPrimaryHero,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, SophisticatedPrimary.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = report.period.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SophisticatedPrimary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    color = SophisticatedBorder
                                )

                                // 3 KPI Indicators: Services, Gross Revenue, Shop Retention
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Servicios",
                                            fontSize = 11.sp,
                                            color = SophisticatedTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${report.totalServices} cortes",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1.2f)) {
                                        Text(
                                            text = "Facturación Bruta",
                                            fontSize = 11.sp,
                                            color = SophisticatedTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = ThermalReceiptUtils.formatCurrency(report.totalRevenue, settings.currencySymbol),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1.2f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "Retenido Local",
                                            fontSize = 11.sp,
                                            color = SophisticatedTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = ThermalReceiptUtils.formatCurrency(report.totalRetention, settings.currencySymbol),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RetentionOrange
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Payment breakdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "En Efectivo: ${ThermalReceiptUtils.formatCurrency(report.cashCommissions, settings.currencySymbol)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CashBlue
                                    )
                                    Text(
                                        text = "En SINPE: ${ThermalReceiptUtils.formatCurrency(report.sinpeCommissions, settings.currencySymbol)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SinpeGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Section Title: Services Breakdown
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DESGLOSE POR SERVICIO REALIZADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp,
                                color = SophisticatedPrimary
                            )
                            Text(
                                text = "${report.servicePerformances.size} categorías",
                                fontSize = 11.sp,
                                color = SophisticatedTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (report.servicePerformances.isEmpty()) {
                        item {
                            Surface(
                                color = SophisticatedSurface,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, SophisticatedBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = null,
                                        tint = SophisticatedTextMuted,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay servicios registrados en este período",
                                        fontSize = 13.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        items(report.servicePerformances, key = { it.serviceName }) { perf ->
                            ServicePerformanceCard(
                                performance = perf,
                                currencySymbol = settings.currencySymbol
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(10.dp)) }
                    }

                    // Section Title: Detailed Individual Service History
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HISTORIAL DETALLADO DE CORTES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp,
                                color = SophisticatedPrimary
                            )
                            Text(
                                text = "${report.transactions.size} tickets",
                                fontSize = 11.sp,
                                color = SophisticatedTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (report.transactions.isEmpty()) {
                        item {
                            Surface(
                                color = SophisticatedSurface,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, SophisticatedBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No se encontraron tickets individuales en este período.",
                                    fontSize = 12.sp,
                                    color = SophisticatedTextSecondary,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(report.transactions, key = { it.id }) { tx ->
                            BarberTransactionHistoryItem(
                                transaction = tx,
                                currencySymbol = settings.currencySymbol,
                                onViewReceipt = { onViewReceipt(tx) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                // Bottom Action Bar
                Surface(
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (settings.printerMacAddress.isBlank()) {
                                        onConfigurePrinter()
                                    } else {
                                        isPrinting = true
                                        coroutineScope.launch {
                                            val bytes = BluetoothThermalPrinter.buildBarberCommissionReportBytes(report, settings)
                                            val result = BluetoothThermalPrinter.printToBluetoothDevice(settings.printerMacAddress, bytes)
                                            isPrinting = false
                                            when (result) {
                                                is PrintResult.Success -> {
                                                    Toast.makeText(context, "Liquidación impresa en impresora Bluetooth", Toast.LENGTH_SHORT).show()
                                                }
                                                is PrintResult.Error -> {
                                                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    }
                                },
                                enabled = !isPrinting,
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(48.dp)
                                    .testTag("print_commission_report_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SophisticatedPrimary.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Imprimir",
                                    tint = SophisticatedPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPrinting) "Imprimiendo..." else "Imprimir",
                                    fontSize = 12.sp,
                                    color = SophisticatedPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    shareBarberCommissionStatement(
                                        context = context,
                                        settings = settings,
                                        report = report
                                    )
                                },
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(48.dp)
                                    .testTag("share_barber_commission_statement_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SophisticatedPrimary,
                                    contentColor = SophisticatedOnPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Compartir",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Compartir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServicePerformanceCard(
    performance: BarberServicePerformance,
    currencySymbol: String
) {
    Surface(
        color = SophisticatedSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SophisticatedBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SophisticatedSurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${performance.count}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = performance.serviceName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Comisión: ${ThermalReceiptUtils.formatCurrency(performance.unitCommission, currencySymbol)} c/u  •  Precio: ${ThermalReceiptUtils.formatCurrency(performance.averagePrice, currencySymbol)}",
                            fontSize = 11.sp,
                            color = SophisticatedTextSecondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = ThermalReceiptUtils.formatCurrency(performance.totalCommission, currencySymbol),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedPrimary
                    )
                    Text(
                        text = "Total facturado: ${ThermalReceiptUtils.formatCurrency(performance.totalRevenue, currencySymbol)}",
                        fontSize = 10.sp,
                        color = SophisticatedTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { performance.percentageOfTotal.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SophisticatedPrimary,
                    trackColor = SophisticatedSurfaceContainer,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${(performance.percentageOfTotal * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SophisticatedTextSecondary
                )
            }
        }
    }
}

@Composable
private fun BarberTransactionHistoryItem(
    transaction: TransactionRecord,
    currencySymbol: String,
    onViewReceipt: () -> Unit
) {
    Surface(
        color = SophisticatedSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SophisticatedBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewReceipt() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SophisticatedSurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${transaction.ticketNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = SophisticatedPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = transaction.serviceName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yy • hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp)),
                        fontSize = 11.sp,
                        color = SophisticatedTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+${ThermalReceiptUtils.formatCurrency(transaction.commissionAmount, currencySymbol)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = SophisticatedPrimary
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
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onViewReceipt,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Ver Ticket",
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun shareBarberCommissionStatement(
    context: Context,
    settings: AppSettings,
    report: BarberDetailedCommissionReport
) {
    val date = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date())
    val text = buildString {
        appendLine("✂️ *LIQUIDACIÓN DE COMISIONES*")
        appendLine("🏢 *${settings.shopName.uppercase()}*")
        appendLine("👤 *Barbero:* ${report.barber.name}")
        if (report.barber.phone.isNotBlank()) {
            appendLine("📱 *Teléfono:* ${report.barber.phone}")
        }
        appendLine("📅 *Período:* ${report.period.label}")
        appendLine("🕒 *Fecha de Emisión:* $date")
        appendLine("================================")
        appendLine("💰 *TOTAL A PAGAR:* ${ThermalReceiptUtils.formatCurrency(report.totalCommissions, settings.currencySymbol)}")
        appendLine("💈 *Total Servicios:* ${report.totalServices} cortes")
        appendLine("💵 *Facturación Bruta:* ${ThermalReceiptUtils.formatCurrency(report.totalRevenue, settings.currencySymbol)}")
        appendLine("🏢 *Retención Local:* ${ThermalReceiptUtils.formatCurrency(report.totalRetention, settings.currencySymbol)}")
        appendLine("--------------------------------")
        appendLine("📊 *DESGLOSE DE SERVICIOS:*")
        report.servicePerformances.forEach { perf ->
            appendLine("• *${perf.serviceName}* (${perf.count}x)")
            appendLine("  Comisión unitaria: ${ThermalReceiptUtils.formatCurrency(perf.unitCommission, settings.currencySymbol)}")
            appendLine("  Subtotal comisión: ${ThermalReceiptUtils.formatCurrency(perf.totalCommission, settings.currencySymbol)}")
        }
        appendLine("--------------------------------")
        appendLine("💳 *DISTRIBUCIÓN POR MÉTODO DE PAGO:*")
        appendLine("• Efectivo: ${ThermalReceiptUtils.formatCurrency(report.cashCommissions, settings.currencySymbol)}")
        appendLine("• SINPE Móvil: ${ThermalReceiptUtils.formatCurrency(report.sinpeCommissions, settings.currencySymbol)}")
        appendLine("================================")
        appendLine("Comprobante generado por Barbería App.")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Compartir Liquidación de ${report.barber.name}"))
}
