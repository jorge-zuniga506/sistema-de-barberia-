package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppSettings
import com.example.data.model.Barber
import com.example.data.model.ServiceItem
import com.example.ui.components.BluetoothPermissionHelper
import com.example.ui.components.BluetoothPermissionRationaleDialog
import com.example.ui.components.BluetoothThermalPrinter
import com.example.ui.components.DashedDivider
import com.example.ui.components.PrintResult
import com.example.ui.components.ThermalReceiptUtils
import com.example.ui.theme.ErrorRed
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
import com.example.ui.viewmodel.BarberViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: BarberViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val allBarbers by viewModel.barbers.collectAsStateWithLifecycle()
    val allServices by viewModel.services.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Negocio, 1 = Ticket, 2 = Impresora, 3 = Barberos, 4 = Servicios

    // Dialog States
    var showBarberDialog by remember { mutableStateOf(false) }
    var editingBarber by remember { mutableStateOf<Barber?>(null) }
    var barberToDelete by remember { mutableStateOf<Barber?>(null) }

    var showServiceDialog by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<ServiceItem?>(null) }
    var serviceToDelete by remember { mutableStateOf<ServiceItem?>(null) }

    // Shop Form State
    var shopName by remember { mutableStateOf(settings.shopName) }
    var defaultRetentionStr by remember { mutableStateOf(settings.defaultRetention.toInt().toString()) }
    var currencySymbol by remember { mutableStateOf(settings.currencySymbol) }

    // Ticket Design State
    var shopSlogan by remember { mutableStateOf(settings.shopSlogan) }
    var sinpePhone by remember { mutableStateOf(settings.sinpePhone) }
    var contactPhone by remember { mutableStateOf(settings.contactPhone) }
    var instagram by remember { mutableStateOf(settings.instagram) }
    var facebookOrWeb by remember { mutableStateOf(settings.facebookOrWeb) }
    var address by remember { mutableStateOf(settings.address) }
    var wifiInfo by remember { mutableStateOf(settings.wifiInfo) }
    var headerGreeting by remember { mutableStateOf(settings.headerGreeting) }
    var ticketFooter by remember { mutableStateOf(settings.ticketFooter) }
    var customPolicy by remember { mutableStateOf(settings.customPolicy) }

    // Toggles for Ticket Layout
    var showSlogan by remember { mutableStateOf(settings.showSlogan) }
    var showSinpe by remember { mutableStateOf(settings.showSinpe) }
    var showPhone by remember { mutableStateOf(settings.showPhone) }
    var showInstagram by remember { mutableStateOf(settings.showInstagram) }
    var showFacebookOrWeb by remember { mutableStateOf(settings.showFacebookOrWeb) }
    var showAddress by remember { mutableStateOf(settings.showAddress) }
    var showWifi by remember { mutableStateOf(settings.showWifi) }
    var showCustomPolicy by remember { mutableStateOf(settings.showCustomPolicy) }
    var showBarberName by remember { mutableStateOf(settings.showBarberName) }
    var showDateTime by remember { mutableStateOf(settings.showDateTime) }
    var showTicketNumber by remember { mutableStateOf(settings.showTicketNumber) }

    // Printer State
    var isTestingPrinter by remember { mutableStateOf(false) }
    var printerStatusMsg by remember { mutableStateOf<String?>(null) }
    var isPrinterSuccess by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    var hasBluetoothPerms by remember {
        mutableStateOf(BluetoothPermissionHelper.hasAllBluetoothPermissions(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms.values.all { it }
        hasBluetoothPerms = granted
        if (granted) {
            Toast.makeText(context, "Permisos de Bluetooth concedidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permisos de Bluetooth requeridos para imprimir", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(settings) {
        shopName = settings.shopName
        shopSlogan = settings.shopSlogan
        sinpePhone = settings.sinpePhone
        contactPhone = settings.contactPhone
        instagram = settings.instagram
        facebookOrWeb = settings.facebookOrWeb
        address = settings.address
        wifiInfo = settings.wifiInfo
        defaultRetentionStr = settings.defaultRetention.toInt().toString()
        currencySymbol = settings.currencySymbol
        headerGreeting = settings.headerGreeting
        ticketFooter = settings.ticketFooter
        customPolicy = settings.customPolicy

        showSlogan = settings.showSlogan
        showSinpe = settings.showSinpe
        showPhone = settings.showPhone
        showInstagram = settings.showInstagram
        showFacebookOrWeb = settings.showFacebookOrWeb
        showAddress = settings.showAddress
        showWifi = settings.showWifi
        showCustomPolicy = settings.showCustomPolicy
        showBarberName = settings.showBarberName
        showDateTime = settings.showDateTime
        showTicketNumber = settings.showTicketNumber
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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SophisticatedSurface,
        unfocusedContainerColor = SophisticatedSurface,
        focusedBorderColor = SophisticatedPrimary,
        unfocusedBorderColor = SophisticatedBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SophisticatedPrimary,
        unfocusedLabelColor = SophisticatedTextSecondary
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedBg)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "ADMINISTRACIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    color = SophisticatedPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ajustes del Sistema",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Personalización de tickets, impresora, barberos y catálogo",
                    style = MaterialTheme.typography.bodySmall,
                    color = SophisticatedTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Navigation Tabs (Local, Ticket, Impresora, Barberos, Servicios)
        item {
            Surface(
                color = SophisticatedSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SophisticatedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = SophisticatedPrimary,
                    edgePadding = 8.dp,
                    indicator = {},
                    divider = {}
                ) {
                    val tabs = listOf(
                        "Local",
                        "🎨 Ticket",
                        "Impresora",
                        "Barberos (${allBarbers.size})",
                        "Servicios (${allServices.size})"
                    )

                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SophisticatedPrimary else SophisticatedTextSecondary
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ==========================================
        // TAB 0: Configuración General del Local
        // ==========================================
        if (selectedTab == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Datos Generales del Negocio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Nombre de la Barbería
                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            label = { Text("Nombre del Negocio") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_shop_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Retención por Defecto
                        OutlinedTextField(
                            value = defaultRetentionStr,
                            onValueChange = { defaultRetentionStr = it },
                            label = { Text("Retención por Defecto del Local ($currencySymbol)") },
                            supportingText = {
                                Text(
                                    "Monto fijo retenido por servicio que no tenga comisión personalizada",
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_retention_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Símbolo de Moneda
                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = { Text("Símbolo de Moneda") },
                            placeholder = { Text("Ej. ₡, $, Q, €", color = SophisticatedTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        // Botón Guardar Local
                        Button(
                            onClick = {
                                val retention = defaultRetentionStr.toDoubleOrNull() ?: 2000.0
                                viewModel.saveFullAppSettings(
                                    settings.copy(
                                        shopName = shopName.trim(),
                                        defaultRetention = retention,
                                        currencySymbol = currencySymbol.trim().ifBlank { "₡" }
                                    )
                                )
                                Toast.makeText(context, "Ajustes del local guardados con éxito", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_shop_settings_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedPrimary,
                                contentColor = SophisticatedOnPrimary
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Datos del Negocio", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 1: Personalización Completa del Ticket (Instagram, Contacto, Slogan, etc.)
        // ==========================================
        if (selectedTab == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SophisticatedPrimaryHero),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = SophisticatedPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Diseño & Contenido del Ticket",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Personaliza toda la información que sale en la impresora",
                                    fontSize = 11.sp,
                                    color = SophisticatedTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. Slogan / Subtítulo
                        TicketFieldWithSwitch(
                            label = "Lema / Subtítulo del Local",
                            value = shopSlogan,
                            onValueChange = { shopSlogan = it },
                            placeholder = "Ej. Estilo, Corte & Tradición",
                            isChecked = showSlogan,
                            onCheckedChange = { showSlogan = it },
                            textFieldColors = textFieldColors
                        )

                        // 2. Instagram
                        TicketFieldWithSwitch(
                            label = "📸 Instagram de la Barbería",
                            value = instagram,
                            onValueChange = { instagram = it },
                            placeholder = "Ej. @barberia_costarica",
                            isChecked = showInstagram,
                            onCheckedChange = { showInstagram = it },
                            textFieldColors = textFieldColors
                        )

                        // 3. Teléfono / WhatsApp de Contacto
                        TicketFieldWithSwitch(
                            label = "📞 Teléfono / WhatsApp de Contacto",
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            placeholder = "Ej. 2222-3344 / 8888-9999",
                            keyboardType = KeyboardType.Phone,
                            isChecked = showPhone,
                            onCheckedChange = { showPhone = it },
                            textFieldColors = textFieldColors
                        )

                        // 4. Teléfono SINPE Móvil
                        TicketFieldWithSwitch(
                            label = "📱 Número para SINPE Móvil",
                            value = sinpePhone,
                            onValueChange = { sinpePhone = it },
                            placeholder = "Ej. 8888-2424",
                            keyboardType = KeyboardType.Phone,
                            isChecked = showSinpe,
                            onCheckedChange = { showSinpe = it },
                            textFieldColors = textFieldColors
                        )

                        // 5. Dirección Física
                        TicketFieldWithSwitch(
                            label = "📍 Dirección / Local",
                            value = address,
                            onValueChange = { address = it },
                            placeholder = "Ej. Centro Comercial Los Arcos, Local #5",
                            isChecked = showAddress,
                            onCheckedChange = { showAddress = it },
                            textFieldColors = textFieldColors
                        )

                        // 6. Información de WiFi para Clientes
                        TicketFieldWithSwitch(
                            label = "📶 Red / Clave WiFi para Clientes",
                            value = wifiInfo,
                            onValueChange = { wifiInfo = it },
                            placeholder = "Ej. WiFi: BarberiaVIP | Clave: Pass2026",
                            isChecked = showWifi,
                            onCheckedChange = { showWifi = it },
                            textFieldColors = textFieldColors
                        )

                        // 7. Web o Red Social adicional
                        TicketFieldWithSwitch(
                            label = "🌐 Web / TikTok / Facebook (Opcional)",
                            value = facebookOrWeb,
                            onValueChange = { facebookOrWeb = it },
                            placeholder = "Ej. tiktok: @barberia_cr",
                            isChecked = showFacebookOrWeb,
                            onCheckedChange = { showFacebookOrWeb = it },
                            textFieldColors = textFieldColors
                        )

                        // 8. Saludo / Encabezado
                        OutlinedTextField(
                            value = headerGreeting,
                            onValueChange = { headerGreeting = it },
                            label = { Text("Texto de Saludo Superior") },
                            placeholder = { Text("Ej. ★ COMPROBANTE DE PAGO ★", color = SophisticatedTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // 9. Mensaje de Despedida / Pie de página
                        OutlinedTextField(
                            value = ticketFooter,
                            onValueChange = { ticketFooter = it },
                            label = { Text("Mensaje de Pie de Ticket (Despedida)") },
                            placeholder = { Text("Ej. ¡Gracias por su preferencia! Calidad y estilo.", color = SophisticatedTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // 10. Políticas o Términos
                        TicketFieldWithSwitch(
                            label = "📋 Política / Nota Legal",
                            value = customPolicy,
                            onValueChange = { customPolicy = it },
                            placeholder = "Ej. Conserve su comprobante para cualquier consulta.",
                            isChecked = showCustomPolicy,
                            onCheckedChange = { showCustomPolicy = it },
                            textFieldColors = textFieldColors
                        )

                        // Interruptores Adicionales
                        Text(
                            text = "OPCIONES ADICIONALES DE IMPRESIÓN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = SophisticatedPrimary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
                        )

                        TicketOptionToggle(
                            title = "Mostrar Nombre del Barbero",
                            subtitle = "Imprime 'Atendido por: [Nombre]'",
                            checked = showBarberName,
                            onCheckedChange = { showBarberName = it }
                        )

                        TicketOptionToggle(
                            title = "Mostrar Fecha y Hora de Atención",
                            subtitle = "Imprime 'dd/MM/yyyy hh:mm a'",
                            checked = showDateTime,
                            onCheckedChange = { showDateTime = it }
                        )

                        TicketOptionToggle(
                            title = "Mostrar Número Consecutivo de Ticket",
                            subtitle = "Imprime 'TICKET #[Número]'",
                            checked = showTicketNumber,
                            onCheckedChange = { showTicketNumber = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Guardar Cambios del Ticket
                        Button(
                            onClick = {
                                viewModel.saveFullAppSettings(
                                    settings.copy(
                                        shopSlogan = shopSlogan.trim(),
                                        sinpePhone = sinpePhone.trim(),
                                        contactPhone = contactPhone.trim(),
                                        instagram = instagram.trim(),
                                        facebookOrWeb = facebookOrWeb.trim(),
                                        address = address.trim(),
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
                                )
                                Toast.makeText(context, "Diseño del ticket actualizado correctamente", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_ticket_customization_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedPrimary,
                                contentColor = SophisticatedOnPrimary
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Diseño del Ticket", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // VISTA PREVIA EN VIVO DEL TICKET TÉRMICO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedPrimary.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "VISTA PREVIA EN TIEMPO REAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = SophisticatedPrimary
                            )
                            Surface(
                                color = SophisticatedPrimaryHero,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = settings.printerPaperWidth,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simulation Thermal Receipt Paper
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Shop Name
                                Text(
                                    text = shopName.uppercase().ifBlank { "MI BARBERÍA" },
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center
                                )

                                if (showSlogan && shopSlogan.isNotBlank()) {
                                    Text(
                                        text = shopSlogan,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF475569),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showAddress && address.isNotBlank()) {
                                    Text(
                                        text = address,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF475569),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showPhone && contactPhone.isNotBlank()) {
                                    Text(
                                        text = "Tel: $contactPhone",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF1E293B),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showInstagram && instagram.isNotBlank()) {
                                    Text(
                                        text = "IG: $instagram",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showFacebookOrWeb && facebookOrWeb.isNotBlank()) {
                                    Text(
                                        text = "Web: $facebookOrWeb",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF475569),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showSinpe && sinpePhone.isNotBlank()) {
                                    Text(
                                        text = "SINPE: $sinpePhone",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0F172A),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showWifi && wifiInfo.isNotBlank()) {
                                    Text(
                                        text = wifiInfo,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                                if (headerGreeting.isNotBlank()) {
                                    Text(
                                        text = headerGreeting,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (showTicketNumber) {
                                    Text(
                                        text = "TICKET #1042",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                if (showDateTime) {
                                    Text(
                                        text = "18/08/2026 10:30 AM",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                                if (showBarberName) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Barbero:", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF475569))
                                        Text("Carlos Morales", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF0F172A))
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Corte Clásico", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF0F172A))
                                    Text("${currencySymbol}6,000", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF0F172A))
                                }

                                DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("TOTAL:", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    Text("${currencySymbol}6,000", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pago:", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF475569))
                                    Text("SINPE MÓVIL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SinpeGreen)
                                }

                                DashedDivider(modifier = Modifier.padding(vertical = 6.dp))

                                if (showCustomPolicy && customPolicy.isNotBlank()) {
                                    Text(
                                        text = customPolicy,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }

                                if (ticketFooter.isNotBlank()) {
                                    Text(
                                        text = ticketFooter,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0F172A),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 2: Impresora Térmica & Permisos Bluetooth
        // ==========================================
        if (selectedTab == 2) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = null,
                                        tint = SophisticatedPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Impresora Térmica Bluetooth",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (settings.printerMacAddress.isNotBlank()) "Vinculada: ${settings.printerName.ifBlank { settings.printerMacAddress }}" else "Sin impresora vinculada",
                                        fontSize = 12.sp,
                                        color = if (settings.printerMacAddress.isNotBlank()) SinpeGreen else SophisticatedTextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.openBluetoothPrinterDialog() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Configurar",
                                    tint = SophisticatedPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Permissions Status Card
                        Surface(
                            color = if (hasBluetoothPerms) SophisticatedSurfaceContainer else Color(0xFFF59E0B).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (hasBluetoothPerms) SophisticatedBorder else Color(0xFFF59E0B).copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (hasBluetoothPerms) Icons.Default.CheckCircle else Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (hasBluetoothPerms) SinpeGreen else Color(0xFFF59E0B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (hasBluetoothPerms) "Permisos Bluetooth Habilitados" else "Permisos Bluetooth Pendientes",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (hasBluetoothPerms) "Listo para comunicarse con impresoras ESC/POS" else "Se requiere permiso para buscar y conectar",
                                            fontSize = 11.sp,
                                            color = SophisticatedTextSecondary
                                        )
                                    }
                                }

                                if (!hasBluetoothPerms) {
                                    Button(
                                        onClick = { showPermissionRationale = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SophisticatedPrimary,
                                            contentColor = SophisticatedOnPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Conceder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Paper Width quick switch
                        Text(
                            text = "Formato de Papel Térmico:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = settings.printerPaperWidth == "58mm",
                                onClick = {
                                    viewModel.updatePrinterSettings(settings.printerMacAddress, settings.printerName, "58mm")
                                },
                                label = { Text("58 mm (Portátil)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SophisticatedPrimaryHero,
                                    selectedLabelColor = SophisticatedPrimary,
                                    containerColor = SophisticatedSurfaceContainer,
                                    labelColor = SophisticatedTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = settings.printerPaperWidth == "58mm",
                                    borderColor = SophisticatedBorder,
                                    selectedBorderColor = SophisticatedPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = settings.printerPaperWidth == "80mm",
                                onClick = {
                                    viewModel.updatePrinterSettings(settings.printerMacAddress, settings.printerName, "80mm")
                                },
                                label = { Text("80 mm (Grande)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SophisticatedPrimaryHero,
                                    selectedLabelColor = SophisticatedPrimary,
                                    containerColor = SophisticatedSurfaceContainer,
                                    labelColor = SophisticatedTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = settings.printerPaperWidth == "80mm",
                                    borderColor = SophisticatedBorder,
                                    selectedBorderColor = SophisticatedPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Configurar / Imprimir Prueba
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.openBluetoothPrinterDialog() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(16.dp), tint = SophisticatedPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Vincular / Cambiar", fontSize = 12.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    if (!BluetoothPermissionHelper.hasAllBluetoothPermissions(context)) {
                                        showPermissionRationale = true
                                        return@Button
                                    }
                                    if (settings.printerMacAddress.isBlank()) {
                                        viewModel.openBluetoothPrinterDialog()
                                        return@Button
                                    }

                                    isTestingPrinter = true
                                    printerStatusMsg = null
                                    coroutineScope.launch {
                                        val bytes = BluetoothThermalPrinter.buildTestTicketBytes(settings)
                                        val res = BluetoothThermalPrinter.printToBluetoothDevice(settings.printerMacAddress, bytes)
                                        isTestingPrinter = false
                                        when (res) {
                                            is PrintResult.Success -> {
                                                isPrinterSuccess = true
                                                printerStatusMsg = "¡Ticket de prueba impreso con éxito!"
                                            }
                                            is PrintResult.Error -> {
                                                isPrinterSuccess = false
                                                printerStatusMsg = res.message
                                            }
                                        }
                                    }
                                },
                                enabled = !isTestingPrinter,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SophisticatedPrimary,
                                    contentColor = SophisticatedOnPrimary
                                )
                            ) {
                                if (isTestingPrinter) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = SophisticatedOnPrimary
                                    )
                                } else {
                                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Probar Impresión", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (printerStatusMsg != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = if (isPrinterSuccess) SinpeGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = printerStatusMsg!!,
                                    fontSize = 11.sp,
                                    color = if (isPrinterSuccess) SinpeGreen else Color(0xFFFCA5A5),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 3: Gestión de Barberos (Empleados)
        // ==========================================
        if (selectedTab == 3) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lista de Barberos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${allBarbers.size} barberos registrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = SophisticatedTextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            editingBarber = null
                            showBarberDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_barber_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Agregar", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nuevo Barbero", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (allBarbers.isEmpty()) {
                item {
                    EmptySettingsCard(
                        title = "No hay barberos registrados",
                        description = "Agrega a tu equipo de trabajo para asignarles los servicios y comisiones."
                    )
                }
            } else {
                items(allBarbers) { barber ->
                    BarberItemRow(
                        barber = barber,
                        onEdit = {
                            editingBarber = barber
                            showBarberDialog = true
                        },
                        onDelete = {
                            barberToDelete = barber
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // ==========================================
        // TAB 4: Gestión de Servicios
        // ==========================================
        if (selectedTab == 4) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Catálogo de Servicios",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${allServices.size} servicios disponibles",
                            style = MaterialTheme.typography.bodySmall,
                            color = SophisticatedTextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            editingService = null
                            showServiceDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_service_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nuevo Servicio", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (allServices.isEmpty()) {
                item {
                    EmptySettingsCard(
                        title = "No hay servicios registrados",
                        description = "Registra los cortes, combos y tratamientos con sus precios de venta."
                    )
                }
            } else {
                items(allServices) { service ->
                    ServiceItemRow(
                        service = service,
                        currencySymbol = settings.currencySymbol,
                        defaultRetention = settings.defaultRetention,
                        onEdit = {
                            editingService = service
                            showServiceDialog = true
                        },
                        onDelete = {
                            serviceToDelete = service
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Modal Crear / Editar Barbero
    if (showBarberDialog) {
        BarberEditDialog(
            barber = editingBarber,
            onDismiss = { showBarberDialog = false },
            onSave = { name, phone ->
                viewModel.saveBarber(name, phone, editingBarber)
                Toast.makeText(
                    context,
                    if (editingBarber != null) "Barbero actualizado" else "Barbero registrado",
                    Toast.LENGTH_SHORT
                ).show()
                showBarberDialog = false
            }
        )
    }

    // Modal Confirmar Eliminar Barbero
    if (barberToDelete != null) {
        AlertDialog(
            onDismissRequest = { barberToDelete = null },
            title = { Text("Eliminar Barbero", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar a '${barberToDelete?.name}'? Esta acción no se puede deshacer.",
                    color = SophisticatedTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        barberToDelete?.let { viewModel.deleteBarber(it) }
                        barberToDelete = null
                        Toast.makeText(context, "Barbero eliminado", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { barberToDelete = null }) {
                    Text("Cancelar", color = SophisticatedTextSecondary)
                }
            },
            containerColor = SophisticatedSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Modal Crear / Editar Servicio
    if (showServiceDialog) {
        ServiceEditDialog(
            service = editingService,
            currencySymbol = settings.currencySymbol,
            onDismiss = { showServiceDialog = false },
            onSave = { name, price, customRetention, category ->
                viewModel.saveService(name, price, customRetention, category, editingService)
                Toast.makeText(
                    context,
                    if (editingService != null) "Servicio actualizado" else "Servicio registrado",
                    Toast.LENGTH_SHORT
                ).show()
                showServiceDialog = false
            }
        )
    }

    // Modal Confirmar Eliminar Servicio
    if (serviceToDelete != null) {
        AlertDialog(
            onDismissRequest = { serviceToDelete = null },
            title = { Text("Eliminar Servicio", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar el servicio '${serviceToDelete?.name}'?",
                    color = SophisticatedTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        serviceToDelete?.let { viewModel.deleteService(it) }
                        serviceToDelete = null
                        Toast.makeText(context, "Servicio eliminado", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { serviceToDelete = null }) {
                    Text("Cancelar", color = SophisticatedTextSecondary)
                }
            },
            containerColor = SophisticatedSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// -------------------------------------------------------------
// Componentes Reutilizables de Configuración y Formularios
// -------------------------------------------------------------

@Composable
fun TicketFieldWithSwitch(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textFieldColors: androidx.compose.material3.TextFieldColors
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isChecked) "Visible" else "Oculto",
                    fontSize = 11.sp,
                    color = if (isChecked) SinpeGreen else SophisticatedTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SophisticatedPrimary,
                        checkedTrackColor = SophisticatedPrimaryHero,
                        uncheckedThumbColor = SophisticatedTextSecondary,
                        uncheckedTrackColor = SophisticatedSurfaceContainer
                    ),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = SophisticatedTextMuted, fontSize = 12.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = textFieldColors
        )
    }
}

@Composable
fun TicketOptionToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = SophisticatedSurfaceContainer,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SophisticatedBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.White)
                Text(subtitle, fontSize = 10.sp, color = SophisticatedTextSecondary)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SophisticatedPrimary,
                    checkedTrackColor = SophisticatedPrimaryHero,
                    uncheckedThumbColor = SophisticatedTextSecondary,
                    uncheckedTrackColor = SophisticatedSurface
                )
            )
        }
    }
}

@Composable
fun EmptySettingsCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = SophisticatedTextSecondary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SophisticatedTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BarberItemRow(
    barber: Barber,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("barber_row_${barber.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SophisticatedPrimaryHero),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = barber.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SophisticatedPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = barber.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (barber.phone.isNotBlank()) {
                        Text(
                            text = "Tel: ${barber.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SophisticatedTextSecondary
                        )
                    } else {
                        Text(
                            text = "Sin teléfono registrado",
                            style = MaterialTheme.typography.bodySmall,
                            color = SophisticatedTextMuted
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = SophisticatedPrimary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = ErrorRed.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceItemRow(
    service: ServiceItem,
    currencySymbol: String,
    defaultRetention: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val retention = service.customRetention ?: defaultRetention
    val commission = (service.price - retention).coerceAtLeast(0.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_row_${service.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SophisticatedPrimaryHero),
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
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SophisticatedSurfaceContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = service.category,
                                fontSize = 10.sp,
                                color = SophisticatedTextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Barbero: ${ThermalReceiptUtils.formatCurrency(commission, currencySymbol)}",
                            fontSize = 11.sp,
                            color = SophisticatedPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = ThermalReceiptUtils.formatCurrency(service.price, currencySymbol),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = SophisticatedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = ErrorRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Modales de Edición
// -------------------------------------------------------------

@Composable
fun BarberEditDialog(
    barber: Barber?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf(barber?.name ?: "") }
    var phone by remember { mutableStateOf(barber?.phone ?: "") }
    var isError by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SophisticatedSurface,
        unfocusedContainerColor = SophisticatedSurface,
        focusedBorderColor = SophisticatedPrimary,
        unfocusedBorderColor = SophisticatedBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SophisticatedPrimary,
        unfocusedLabelColor = SophisticatedTextSecondary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (barber == null) "Nuevo Barbero" else "Editar Barbero",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isError = false
                    },
                    label = { Text("Nombre Completo *") },
                    isError = isError,
                    supportingText = {
                        if (isError) Text("El nombre es obligatorio", color = ErrorRed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("barber_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono de Contacto (Opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("barber_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        isError = true
                    } else {
                        onSave(name.trim(), phone.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SophisticatedPrimary,
                    contentColor = SophisticatedOnPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_barber_dialog_button")
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SophisticatedTextSecondary)
            }
        },
        containerColor = SophisticatedSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ServiceEditDialog(
    service: ServiceItem?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double, customRetention: Double?, category: String) -> Unit
) {
    var name by remember { mutableStateOf(service?.name ?: "") }
    var priceStr by remember { mutableStateOf(service?.price?.toInt()?.toString() ?: "") }
    var customRetentionStr by remember { mutableStateOf(service?.customRetention?.toInt()?.toString() ?: "") }
    var category by remember { mutableStateOf(service?.category ?: "Corte") }
    var isError by remember { mutableStateOf(false) }

    val categories = listOf("Corte", "Barba", "Combos", "Tratamientos", "Color", "Detalles", "Otros")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SophisticatedSurface,
        unfocusedContainerColor = SophisticatedSurface,
        focusedBorderColor = SophisticatedPrimary,
        unfocusedBorderColor = SophisticatedBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SophisticatedPrimary,
        unfocusedLabelColor = SophisticatedTextSecondary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (service == null) "Nuevo Servicio" else "Editar Servicio",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isError = false
                    },
                    label = { Text("Nombre del Servicio *") },
                    isError = isError,
                    supportingText = {
                        if (isError) Text("El nombre y precio son obligatorios", color = ErrorRed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Precio de Venta ($currencySymbol) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_price_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customRetentionStr,
                    onValueChange = { customRetentionStr = it },
                    label = { Text("Retención Personalizada ($currencySymbol)") },
                    supportingText = {
                        Text(
                            "Opcional. Si se deja vacío, usa la retención global del local",
                            fontSize = 11.sp,
                            color = SophisticatedTextSecondary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_custom_retention_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Categoría", fontSize = 12.sp, color = SophisticatedTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SophisticatedPrimaryHero,
                                selectedLabelColor = SophisticatedPrimary,
                                containerColor = SophisticatedSurfaceContainer,
                                labelColor = SophisticatedTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = category == cat,
                                borderColor = SophisticatedBorder,
                                selectedBorderColor = SophisticatedPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull()
                    val customRetention = customRetentionStr.toDoubleOrNull()
                    if (name.isBlank() || price == null || price <= 0) {
                        isError = true
                    } else {
                        onSave(name.trim(), price, customRetention, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SophisticatedPrimary,
                    contentColor = SophisticatedOnPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_service_dialog_button")
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SophisticatedTextSecondary)
            }
        },
        containerColor = SophisticatedSurface,
        shape = RoundedCornerShape(20.dp)
    )
}
