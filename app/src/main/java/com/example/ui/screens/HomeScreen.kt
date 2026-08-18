package com.example.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionRecord
import com.example.ui.components.ThermalReceiptUtils
import com.example.ui.theme.CashBlue
import com.example.ui.theme.CashBlueBg
import com.example.ui.theme.CommissionPurple
import com.example.ui.theme.RetentionOrange
import com.example.ui.theme.SinpeGreen
import com.example.ui.theme.SinpeGreenBg
import com.example.ui.theme.SophisticatedBg
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryHero
import com.example.ui.theme.SophisticatedPrimaryHeroLight
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.viewmodel.BarberViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: BarberViewModel,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val todayMetrics by viewModel.todayStats.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val barbers by viewModel.barbers.collectAsStateWithLifecycle()

    val todayDateFormatted = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "CR"))
        .format(Date())
        .replaceFirstChar { it.uppercase() }

    val recentTransactions = allTransactions.take(5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedBg)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PREMIUM DASHBOARD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp,
                        color = SophisticatedPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = settings.shopName.ifBlank { "Vanguard Barbers" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = SophisticatedTextPrimary
                    )
                }

                // Header Pill Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SophisticatedSurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Local",
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            HorizontalDivider(
                color = SophisticatedBorder,
                thickness = 1.dp
            )
        }

        // Hero Card (Velvet Purple Royal Card)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedPrimaryHero),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Decorative ambient circle
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .offset(x = 240.dp, y = (-40).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.06f))
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Top Row: Label + Date Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "INGRESOS DEL DÍA",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = SophisticatedPrimaryHeroLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ThermalReceiptUtils.formatCurrency(todayMetrics.totalRevenue, settings.currencySymbol),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Light,
                                        letterSpacing = (-0.5).sp,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = SophisticatedPrimaryHeroLight,
                                    contentColor = SophisticatedPrimaryHero
                                ) {
                                    Text(
                                        text = "${todayMetrics.count} SERVICIOS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grid of 2 Sub-metrics (Ganancia Local & Comisiones)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Ganancia Local
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Black.copy(alpha = 0.22f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Ganancia Local",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SophisticatedPrimaryHeroLight.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = ThermalReceiptUtils.formatCurrency(todayMetrics.totalRetention, settings.currencySymbol),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Comisiones Barberos
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Black.copy(alpha = 0.22f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Comis. Barberos",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SophisticatedPrimaryHeroLight.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = ThermalReceiptUtils.formatCurrency(todayMetrics.totalCommission, settings.currencySymbol),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Payment Method Highlights
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Efectivo Pill Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CashBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalAtm,
                                contentDescription = null,
                                tint = CashBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Efectivo",
                                fontSize = 11.sp,
                                color = SophisticatedTextSecondary
                            )
                            Text(
                                text = "${todayMetrics.cashCount} cobros",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedTextPrimary
                            )
                        }
                    }
                }

                // SINPE Pill Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SinpeGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = SinpeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SINPE Móvil",
                                fontSize = 11.sp,
                                color = SophisticatedTextSecondary
                            )
                            Text(
                                text = "${todayMetrics.sinpeCount} cobros",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Action CTA Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("home_new_sale_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = SophisticatedOnPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NUEVO REGISTRO / COBRAR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Recent Activity Section (Container Card)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Actividad Reciente",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SophisticatedTextPrimary
                        )
                        Text(
                            text = "${allTransactions.size} registros",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SophisticatedPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (recentTransactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = SophisticatedTextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay ventas registradas hoy",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SophisticatedTextSecondary
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recentTransactions.forEach { tx ->
                                SophisticatedTransactionRow(
                                    transaction = tx,
                                    currencySymbol = settings.currencySymbol,
                                    onViewReceipt = { viewModel.showReceipt(tx) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Commission Access Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = SophisticatedPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Comisiones del Equipo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedTextPrimary
                            )
                        }
                        Text(
                            text = "Toca para ver desglose",
                            fontSize = 11.sp,
                            color = SophisticatedTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val activeBarbers = barbers.filter { it.isActive }
                    if (activeBarbers.isEmpty()) {
                        Text(
                            text = "No hay barberos registrados.",
                            fontSize = 12.sp,
                            color = SophisticatedTextSecondary
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeBarbers.forEach { barberItem ->
                                val barberTxs = allTransactions.filter { it.barberId == barberItem.id }
                                val totalCom = barberTxs.sumOf { it.commissionAmount }
                                val count = barberTxs.size

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openBarberCommissionDetail(barberItem) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = SophisticatedSurfaceContainer
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(SophisticatedPrimaryHero),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = barberItem.name.take(2).uppercase(),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SophisticatedPrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = barberItem.name,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "$count servicios realizados",
                                                    fontSize = 11.sp,
                                                    color = SophisticatedTextSecondary
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = ThermalReceiptUtils.formatCurrency(totalCom, settings.currencySymbol),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SophisticatedPrimary
                                                )
                                                Text(
                                                    text = "Comisión acumulada",
                                                    fontSize = 10.sp,
                                                    color = SophisticatedTextSecondary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = "Ver detalle",
                                                tint = SophisticatedPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SophisticatedTransactionRow(
    transaction: TransactionRecord,
    currencySymbol: String,
    onViewReceipt: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewReceipt() }
            .testTag("tx_item_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        color = SophisticatedSurfaceContainer
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SophisticatedPrimaryHero.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.serviceName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${transaction.barberName} • ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(transaction.timestamp))} • ${transaction.paymentMethod.displayName}",
                        fontSize = 11.sp,
                        color = SophisticatedTextSecondary
                    )
                }
            }

            Text(
                text = ThermalReceiptUtils.formatCurrency(transaction.totalAmount, currencySymbol),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

