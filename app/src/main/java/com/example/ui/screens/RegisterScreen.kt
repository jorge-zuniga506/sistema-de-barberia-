package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Barber
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceItem
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
import com.example.ui.theme.SophisticatedPrimaryHeroDark
import com.example.ui.theme.SophisticatedPrimaryHeroLight
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.viewmodel.BarberViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    viewModel: BarberViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()
    val activeBarbers by viewModel.activeBarbers.collectAsStateWithLifecycle()
    val activeServices by viewModel.activeServices.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val selectedService = state.selectedService
    val totalAmount = selectedService?.price ?: 0.0
    val retentionAmount = if (selectedService != null) {
        selectedService.customRetention ?: settings.defaultRetention
    } else 0.0
    val commissionAmount = (totalAmount - retentionAmount).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedBg)
            .testTag("register_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NUEVA VENTA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp,
                        color = SophisticatedPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Registro y Cobro",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { viewModel.resetRegisterForm() },
                    modifier = Modifier.testTag("reset_register_form_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Limpiar formulario",
                        tint = SophisticatedPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error message if any
        if (state.errorMessage != null) {
            item {
                Surface(
                    color = ErrorRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.errorMessage ?: "",
                            color = ErrorRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // SECTION 1: Seleccionar Barbero
        item {
            Text(
                text = "1. Seleccionar Barbero",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (activeBarbers.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Text(
                        text = "No hay barberos activos. Agrega uno en la pestaña Ajustes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SophisticatedTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activeBarbers, key = { it.id }) { barber ->
                        val isSelected = state.selectedBarber?.id == barber.id
                        BarberChipCard(
                            barber = barber,
                            isSelected = isSelected,
                            onSelect = { viewModel.selectBarber(barber) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // SECTION 2: Seleccionar Servicio
        item {
            Text(
                text = "2. Seleccionar Servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (activeServices.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Text(
                        text = "No hay servicios activos. Agrega uno en la pestaña Ajustes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SophisticatedTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        if (activeServices.isNotEmpty()) {
            items(activeServices, key = { it.id }) { service ->
                val isSelected = state.selectedService?.id == service.id
                ServiceSelectionCard(
                    service = service,
                    currencySymbol = settings.currencySymbol,
                    isSelected = isSelected,
                    onSelect = { viewModel.selectService(service) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // SECTION 3: Método de Pago
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "3. Método de Pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Efectivo Button
                PaymentMethodButton(
                    method = PaymentMethod.EFECTIVO,
                    isSelected = state.paymentMethod == PaymentMethod.EFECTIVO,
                    icon = Icons.Default.LocalAtm,
                    activeColor = CashBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setPaymentMethod(PaymentMethod.EFECTIVO) }
                )

                // SINPE Button
                PaymentMethodButton(
                    method = PaymentMethod.SINPE,
                    isSelected = state.paymentMethod == PaymentMethod.SINPE,
                    icon = Icons.Default.PhoneAndroid,
                    activeColor = SinpeGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setPaymentMethod(PaymentMethod.SINPE) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // SECTION 4: Notas Opcionales
        item {
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.setNotes(it) },
                label = { Text("Nota / Referencia SINPE (Opcional)", color = SophisticatedTextSecondary) },
                placeholder = { Text("Ej. Teléfono cliente o comprobante #", color = SophisticatedTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = SophisticatedPrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_notes_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SophisticatedSurface,
                    unfocusedContainerColor = SophisticatedSurface,
                    focusedBorderColor = SophisticatedPrimary,
                    unfocusedBorderColor = SophisticatedBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        // SECTION 5: Breakdown & Automatic Calculation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calculation_breakdown_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Desglose de Liquidación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Precio Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Precio Total Cobrado:",
                            fontSize = 14.sp,
                            color = SophisticatedTextSecondary
                        )
                        Text(
                            text = ThermalReceiptUtils.formatCurrency(totalAmount, settings.currencySymbol),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Retención Local
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Retención Local (Ganancia):",
                            fontSize = 13.sp,
                            color = RetentionOrange
                        )
                        Text(
                            text = "- " + ThermalReceiptUtils.formatCurrency(retentionAmount, settings.currencySymbol),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RetentionOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Comisión Barbero
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Comisión Barbero (${state.selectedBarber?.name ?: "Barbero"}):",
                            fontSize = 13.sp,
                            color = SophisticatedPrimary
                        )
                        Text(
                            text = "= " + ThermalReceiptUtils.formatCurrency(commissionAmount, settings.currencySymbol),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedPrimary
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = SophisticatedBorder
                    )

                    // Final Big Action Button
                    Button(
                        onClick = { viewModel.registerCurrentSale() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("submit_sale_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary,
                            disabledContainerColor = SophisticatedSurfaceContainer,
                            disabledContentColor = SophisticatedTextSecondary
                        ),
                        enabled = state.selectedBarber != null && state.selectedService != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COBRAR Y GENERAR TICKET",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarberChipCard(
    barber: Barber,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onSelect() }
            .testTag("barber_chip_${barber.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SophisticatedPrimaryHero else SophisticatedSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) SophisticatedPrimary else SophisticatedBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SophisticatedPrimary else SophisticatedSurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = barber.name.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) SophisticatedOnPrimary else Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = barber.name,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White
                )
                if (barber.phone.isNotBlank()) {
                    Text(
                        text = barber.phone,
                        fontSize = 11.sp,
                        color = if (isSelected) SophisticatedPrimaryHeroLight else SophisticatedTextSecondary
                    )
                }
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SophisticatedPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ServiceSelectionCard(
    service: ServiceItem,
    currencySymbol: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SophisticatedSurfaceContainer else SophisticatedSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) SophisticatedPrimary else SophisticatedBorder
        )
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SophisticatedPrimaryHero else SophisticatedSurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = if (isSelected) SophisticatedPrimary else SophisticatedTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = SophisticatedSurfaceContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = service.category,
                            fontSize = 10.sp,
                            color = SophisticatedTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = ThermalReceiptUtils.formatCurrency(service.price, currencySymbol),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isSelected) SophisticatedPrimary else Color.White
                )
                if (service.customRetention != null) {
                    Text(
                        text = "Ret: ${ThermalReceiptUtils.formatCurrency(service.customRetention, currencySymbol)}",
                        fontSize = 10.sp,
                        color = RetentionOrange
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodButton(
    method: PaymentMethod,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() }
            .testTag("payment_${method.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) activeColor.copy(alpha = 0.15f) else SophisticatedSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) activeColor else SophisticatedBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else SophisticatedTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = method.displayName,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                color = if (isSelected) activeColor else Color.White
            )
        }
    }
}

