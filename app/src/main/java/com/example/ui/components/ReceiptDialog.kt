package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppSettings
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionRecord
import com.example.ui.theme.CashBlue
import com.example.ui.theme.SinpeGreen
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryHero
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedTextSecondary
import kotlinx.coroutines.launch

@Composable
fun ReceiptDialog(
    transaction: TransactionRecord,
    settings: AppSettings,
    onConfigurePrinter: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPrintingBluetooth by remember { mutableStateOf(false) }
    var printStatusMessage by remember { mutableStateOf<String?>(null) }
    var isPrintSuccess by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    fun doBluetoothPrint() {
        isPrintingBluetooth = true
        printStatusMessage = null
        coroutineScope.launch {
            val bytes = BluetoothThermalPrinter.buildTransactionReceiptBytes(transaction, settings)
            val result = BluetoothThermalPrinter.printToBluetoothDevice(settings.printerMacAddress, bytes)
            isPrintingBluetooth = false
            when (result) {
                is PrintResult.Success -> {
                    isPrintSuccess = true
                    printStatusMessage = "¡Ticket impreso correctamente!"
                    Toast.makeText(context, "Imprimiendo ticket...", Toast.LENGTH_SHORT).show()
                }
                is PrintResult.Error -> {
                    isPrintSuccess = false
                    printStatusMessage = result.message
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            if (settings.printerMacAddress.isBlank()) {
                onConfigurePrinter()
            } else {
                doBluetoothPrint()
            }
        } else {
            Toast.makeText(
                context,
                "Se requieren permisos de Bluetooth para imprimir.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    if (showPermissionRationale) {
        BluetoothPermissionRationaleDialog(
            onGrantRequested = {
                showPermissionRationale = false
                permissionLauncher.launch(BluetoothPermissionHelper.getRequiredPermissions())
            },
            onDismiss = { showPermissionRationale = false }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("receipt_dialog"),
            colors = CardDefaults.cardColors(
                containerColor = SophisticatedSurface
            ),
            border = BorderStroke(1.dp, SophisticatedBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SophisticatedPrimaryHero),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null,
                                tint = SophisticatedPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "COMPROBANTE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                color = SophisticatedPrimary
                            )
                            Text(
                                text = "Ticket Digital #${transaction.ticketNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_receipt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = SophisticatedTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Realistic Thermal Paper Card with Full Custom Layout
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF9FAFB)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Shop Name
                        Text(
                            text = settings.shopName.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF111827),
                            textAlign = TextAlign.Center
                        )

                        // Slogan
                        if (settings.showSlogan && settings.shopSlogan.isNotBlank()) {
                            Text(
                                text = settings.shopSlogan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Address
                        if (settings.showAddress && settings.address.isNotBlank()) {
                            Text(
                                text = settings.address,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Contact Phone
                        if (settings.showPhone && settings.contactPhone.isNotBlank()) {
                            Text(
                                text = "Tel/WhatsApp: ${settings.contactPhone}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF1F2937),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Instagram
                        if (settings.showInstagram && settings.instagram.isNotBlank()) {
                            Text(
                                text = "Instagram: ${settings.instagram}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Web / Facebook
                        if (settings.showFacebookOrWeb && settings.facebookOrWeb.isNotBlank()) {
                            Text(
                                text = "Web: ${settings.facebookOrWeb}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563),
                                textAlign = TextAlign.Center
                            )
                        }

                        // SINPE Phone
                        if (settings.showSinpe && settings.sinpePhone.isNotBlank()) {
                            Text(
                                text = "SINPE Móvil: ${settings.sinpePhone}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937),
                                textAlign = TextAlign.Center
                            )
                        }

                        // WiFi
                        if (settings.showWifi && settings.wifiInfo.isNotBlank()) {
                            Text(
                                text = settings.wifiInfo,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF4B5563),
                                textAlign = TextAlign.Center
                            )
                        }

                        DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                        if (settings.headerGreeting.isNotBlank()) {
                            Text(
                                text = settings.headerGreeting,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF111827),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (settings.showTicketNumber) {
                            Text(
                                text = "TICKET #${transaction.ticketNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF111827)
                            )
                        }

                        if (settings.showDateTime) {
                            Text(
                                text = ThermalReceiptUtils.formatDate(transaction.timestamp),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                        // Detail Rows
                        if (settings.showBarberName) {
                            ReceiptRow(label = "Barbero:", value = transaction.barberName)
                        }
                        ReceiptRow(label = "Servicio:", value = transaction.serviceName, isBold = true)

                        if (transaction.notes.isNotBlank()) {
                            ReceiptRow(label = "Nota:", value = transaction.notes)
                        }

                        DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                        // Total Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL:",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = ThermalReceiptUtils.formatCurrency(transaction.totalAmount, settings.currencySymbol),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF111827)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Payment Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Método:",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF374151)
                            )
                            Surface(
                                color = if (transaction.paymentMethod == PaymentMethod.SINPE) SinpeGreen.copy(alpha = 0.15f) else CashBlue.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (transaction.paymentMethod == PaymentMethod.SINPE) SinpeGreen else CashBlue
                                )
                            ) {
                                Text(
                                    text = transaction.paymentMethod.displayName.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (transaction.paymentMethod == PaymentMethod.SINPE) SinpeGreen else CashBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                        // Policy
                        if (settings.showCustomPolicy && settings.customPolicy.isNotBlank()) {
                            Text(
                                text = settings.customPolicy,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF4B5563),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        if (settings.ticketFooter.isNotBlank()) {
                            Text(
                                text = settings.ticketFooter,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF111827),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bluetooth Printer status / selector banner
                Surface(
                    color = SophisticatedSurfaceContainer,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SophisticatedBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConfigurePrinter() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (settings.printerMacAddress.isNotBlank()) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = if (settings.printerMacAddress.isNotBlank()) SophisticatedPrimary else SophisticatedTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (settings.printerMacAddress.isNotBlank())
                                    "${settings.printerName.ifBlank { settings.printerMacAddress }} (${settings.printerPaperWidth})"
                                else
                                    "Sin impresora Bluetooth vinculada",
                                fontSize = 11.sp,
                                color = if (settings.printerMacAddress.isNotBlank()) Color.White else SophisticatedTextSecondary,
                                fontWeight = if (settings.printerMacAddress.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        Text(
                            text = if (settings.printerMacAddress.isNotBlank()) "Cambiar" else "Vincular",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedPrimary
                        )
                    }
                }

                if (printStatusMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = printStatusMessage!!,
                        fontSize = 11.sp,
                        color = if (isPrintSuccess) SinpeGreen else Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Action: Print Bluetooth ESC/POS
                Button(
                    onClick = {
                        if (!BluetoothPermissionHelper.hasAllBluetoothPermissions(context)) {
                            showPermissionRationale = true
                        } else if (settings.printerMacAddress.isBlank()) {
                            onConfigurePrinter()
                        } else {
                            doBluetoothPrint()
                        }
                    },
                    enabled = !isPrintingBluetooth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("print_bluetooth_receipt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = SophisticatedOnPrimary
                    )
                ) {
                    if (isPrintingBluetooth) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = SophisticatedOnPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Imprimiendo en Bluetooth...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Imprimir Bluetooth",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (settings.printerMacAddress.isNotBlank()) "Imprimir en Impresora Bluetooth" else "Vincular & Imprimir Bluetooth",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Actions: Print via System/PDF + Share WhatsApp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { ThermalReceiptUtils.shareReceipt(context, transaction, settings) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("share_receipt_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartir", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }

                    OutlinedButton(
                        onClick = { ThermalReceiptUtils.printReceipt(context, transaction, settings) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("print_system_receipt_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF / Sistema",
                            tint = SophisticatedPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF / Sistema", fontSize = 12.sp, color = SophisticatedPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF4B5563)
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    Text(
        text = "- - - - - - - - - - - - - - - - - - - - - - - - -",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = Color(0xFF9CA3AF),
        maxLines = 1,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
