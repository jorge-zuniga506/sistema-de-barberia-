package com.example.ui.components

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppSettings
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
import kotlinx.coroutines.launch

@Composable
fun BluetoothPrinterDialog(
    currentSettings: AppSettings,
    onSavePrinterSettings: (mac: String, name: String, paperWidth: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var pairedDevices by remember { mutableStateOf<List<BluetoothPrinterDevice>>(emptyList()) }
    var selectedMac by remember { mutableStateOf(currentSettings.printerMacAddress) }
    var selectedName by remember { mutableStateOf(currentSettings.printerName) }
    var selectedPaperWidth by remember { mutableStateOf(currentSettings.printerPaperWidth) }

    var isTestingPrint by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }

    var hasPermissions by remember {
        mutableStateOf(BluetoothPermissionHelper.hasAllBluetoothPermissions(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        hasPermissions = granted
        if (granted) {
            pairedDevices = BluetoothThermalPrinter.getPairedPrinters(context)
            Toast.makeText(context, "Permisos de Bluetooth concedidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Se requieren permisos de Bluetooth para detectar la impresora",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun requestPermissionsAndRefresh() {
        permissionLauncher.launch(BluetoothPermissionHelper.getRequiredPermissions())
    }

    LaunchedEffect(Unit) {
        if (hasPermissions) {
            pairedDevices = BluetoothThermalPrinter.getPairedPrinters(context)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("bluetooth_printer_dialog"),
            colors = CardDefaults.cardColors(containerColor = SophisticatedBg),
            border = BorderStroke(1.dp, SophisticatedBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SophisticatedPrimaryHero),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = SophisticatedPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "IMPRESORA TÉRMICA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.4.sp,
                                    color = SophisticatedPrimary
                                )
                                Text(
                                    text = "Configuración Bluetooth",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_bluetooth_printer_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = SophisticatedTextSecondary
                            )
                        }
                    }
                }

                // Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Paper Width Selector Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                            border = BorderStroke(1.dp, SophisticatedBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Ancho de Papel Térmico",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Selecciona según el modelo de tu impresora:",
                                    fontSize = 11.sp,
                                    color = SophisticatedTextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedPaperWidth == "58mm",
                                        onClick = { selectedPaperWidth = "58mm" },
                                        label = {
                                            Text(
                                                "58 mm (Portátil Estándar)",
                                                fontSize = 12.sp,
                                                fontWeight = if (selectedPaperWidth == "58mm") FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SophisticatedPrimaryHero,
                                            selectedLabelColor = SophisticatedPrimary,
                                            containerColor = SophisticatedSurfaceContainer,
                                            labelColor = SophisticatedTextSecondary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedPaperWidth == "58mm",
                                            borderColor = SophisticatedBorder,
                                            selectedBorderColor = SophisticatedPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    FilterChip(
                                        selected = selectedPaperWidth == "80mm",
                                        onClick = { selectedPaperWidth = "80mm" },
                                        label = {
                                            Text(
                                                "80 mm (POS Grande)",
                                                fontSize = 12.sp,
                                                fontWeight = if (selectedPaperWidth == "80mm") FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SophisticatedPrimaryHero,
                                            selectedLabelColor = SophisticatedPrimary,
                                            containerColor = SophisticatedSurfaceContainer,
                                            labelColor = SophisticatedTextSecondary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedPaperWidth == "80mm",
                                            borderColor = SophisticatedBorder,
                                            selectedBorderColor = SophisticatedPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Bluetooth Permissions banner if missing
                    if (!hasPermissions) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Permisos de Bluetooth requeridos",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Para detectar y conectar con tu impresora térmica inalámbrica, debes otorgar permiso de Bluetooth a la aplicación.",
                                        fontSize = 12.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { requestPermissionsAndRefresh() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SophisticatedPrimary,
                                                contentColor = SophisticatedOnPrimary
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Conceder Permisos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { BluetoothPermissionHelper.openAppSettings(context) },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, SophisticatedBorder)
                                        ) {
                                            Text("Ajustes del Teléfono", fontSize = 12.sp, color = SophisticatedTextSecondary)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Device list Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DISPOSITIVOS BLUETOOTH VINCULADOS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = SophisticatedPrimary
                            )

                            IconButton(
                                onClick = {
                                    if (hasPermissions) {
                                        pairedDevices = BluetoothThermalPrinter.getPairedPrinters(context)
                                    } else {
                                        requestPermissionsAndRefresh()
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refrescar",
                                    tint = SophisticatedPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Device List
                    if (pairedDevices.isEmpty()) {
                        item {
                            Surface(
                                color = SophisticatedSurface,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, SophisticatedBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bluetooth,
                                        contentDescription = null,
                                        tint = SophisticatedTextMuted,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No se encontraron impresoras vinculadas",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "1. Enciende tu impresora térmica\n2. Ve a los Ajustes de Bluetooth de tu teléfono y emparéjala (código usualmente 0000 ó 1234)\n3. Toca 'Refrescar' aquí",
                                        fontSize = 11.sp,
                                        color = SophisticatedTextSecondary,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Abre Ajustes > Bluetooth", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, SophisticatedBorder)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = SophisticatedPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Abrir Ajustes Bluetooth", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    } else {
                        items(pairedDevices) { device ->
                            val isSelected = selectedMac == device.address

                            Surface(
                                color = if (isSelected) SophisticatedPrimaryHero else SophisticatedSurface,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) SophisticatedPrimary else SophisticatedBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedMac = device.address
                                        selectedName = device.name
                                        statusMessage = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) SophisticatedPrimary else SophisticatedSurfaceContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                                                contentDescription = null,
                                                tint = if (isSelected) SophisticatedOnPrimary else SophisticatedPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = device.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = device.address,
                                                fontSize = 11.sp,
                                                color = SophisticatedTextSecondary
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Seleccionada",
                                            tint = SophisticatedPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Status feedback message
                    if (statusMessage != null) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = if (isSuccessStatus) SinpeGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSuccessStatus) SinpeGreen.copy(alpha = 0.4f) else Color(0xFFEF4444).copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = statusMessage!!,
                                    fontSize = 12.sp,
                                    color = if (isSuccessStatus) SinpeGreen else Color(0xFFFCA5A5),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                // Footer Actions
                Surface(
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Test Print Button
                            OutlinedButton(
                                onClick = {
                                    if (!hasPermissions) {
                                        requestPermissionsAndRefresh()
                                        return@OutlinedButton
                                    }
                                    if (selectedMac.isBlank()) {
                                        Toast.makeText(context, "Selecciona una impresora primero", Toast.LENGTH_SHORT).show()
                                        return@OutlinedButton
                                    }

                                    isTestingPrint = true
                                    statusMessage = null
                                    coroutineScope.launch {
                                        val testSettings = currentSettings.copy(
                                            printerMacAddress = selectedMac,
                                            printerName = selectedName,
                                            printerPaperWidth = selectedPaperWidth
                                        )
                                        val bytes = BluetoothThermalPrinter.buildTestTicketBytes(testSettings)
                                        val res = BluetoothThermalPrinter.printToBluetoothDevice(selectedMac, bytes)
                                        isTestingPrint = false
                                        when (res) {
                                            is PrintResult.Success -> {
                                                isSuccessStatus = true
                                                statusMessage = "¡Impresión de prueba exitosa!"
                                            }
                                            is PrintResult.Error -> {
                                                isSuccessStatus = false
                                                statusMessage = res.message
                                            }
                                        }
                                    }
                                },
                                enabled = !isTestingPrint && selectedMac.isNotBlank(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                if (isTestingPrint) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = SophisticatedPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Probando...", fontSize = 12.sp, color = SophisticatedPrimary)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = null,
                                        tint = SophisticatedPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Imprimir Prueba", fontSize = 12.sp, color = SophisticatedPrimary)
                                }
                            }

                            // Save & Link Button
                            Button(
                                onClick = {
                                    onSavePrinterSettings(selectedMac, selectedName, selectedPaperWidth)
                                    Toast.makeText(context, "Impresora vinculada correctamente", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SophisticatedPrimary,
                                    contentColor = SophisticatedOnPrimary
                                )
                            ) {
                                Text("Guardar & Usar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
