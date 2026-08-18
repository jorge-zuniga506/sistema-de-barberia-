package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.model.AppSettings
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionRecord
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ThermalReceiptUtils {

    fun formatCurrency(amount: Double, symbol: String = "₡"): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        return "$symbol${formatter.format(amount)}"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun generateHtmlTicket(transaction: TransactionRecord, settings: AppSettings): String {
        val formattedDate = formatDate(transaction.timestamp)
        val formattedTotal = formatCurrency(transaction.totalAmount, settings.currencySymbol)
        val paymentBadge = if (transaction.paymentMethod == PaymentMethod.SINPE) "SINPE MÓVIL" else "EFECTIVO"

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Ticket #${transaction.ticketNumber}</title>
                <style>
                    body {
                        font-family: 'Courier New', Courier, monospace;
                        font-size: 13px;
                        line-height: 1.3;
                        width: 280px;
                        margin: 0 auto;
                        padding: 10px;
                        color: #000;
                        background: #fff;
                    }
                    .center { text-align: center; }
                    .bold { font-weight: bold; }
                    .shop-name { font-size: 18px; font-weight: bold; margin-bottom: 2px; text-transform: uppercase; }
                    .slogan { font-size: 11px; font-style: italic; margin-bottom: 4px; }
                    .sub-header { font-size: 11px; margin-bottom: 3px; }
                    .divider { border-top: 1px dashed #000; margin: 8px 0; }
                    .double-divider { border-top: 2px solid #000; margin: 8px 0; }
                    .row { display: flex; justify-content: space-between; margin: 3px 0; }
                    .item-title { font-weight: bold; }
                    .total-row { font-size: 16px; font-weight: bold; margin-top: 6px; }
                    .badge {
                        display: inline-block;
                        border: 1px solid #000;
                        padding: 2px 6px;
                        font-weight: bold;
                        border-radius: 3px;
                        font-size: 11px;
                        margin-top: 4px;
                    }
                    .policy { font-size: 10px; color: #444; margin-top: 8px; text-align: center; }
                    .footer { font-size: 11px; margin-top: 8px; text-align: center; font-weight: bold; }
                    .ticket-num { font-size: 14px; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="center">
                    <div class="shop-name">${settings.shopName}</div>
                    ${if (settings.showSlogan && settings.shopSlogan.isNotBlank()) "<div class='slogan'>${settings.shopSlogan}</div>" else ""}
                    ${if (settings.showAddress && settings.address.isNotBlank()) "<div class='sub-header'>${settings.address}</div>" else ""}
                    ${if (settings.showPhone && settings.contactPhone.isNotBlank()) "<div class='sub-header'>Tel: ${settings.contactPhone}</div>" else ""}
                    ${if (settings.showInstagram && settings.instagram.isNotBlank()) "<div class='sub-header'><b>IG:</b> ${settings.instagram}</div>" else ""}
                    ${if (settings.showFacebookOrWeb && settings.facebookOrWeb.isNotBlank()) "<div class='sub-header'>Web: ${settings.facebookOrWeb}</div>" else ""}
                    ${if (settings.showSinpe && settings.sinpePhone.isNotBlank()) "<div class='sub-header'>SINPE: ${settings.sinpePhone}</div>" else ""}
                    ${if (settings.showWifi && settings.wifiInfo.isNotBlank()) "<div class='sub-header'>${settings.wifiInfo}</div>" else ""}
                    
                    <div class="divider"></div>
                    ${if (settings.headerGreeting.isNotBlank()) "<div style='font-size: 11px; font-weight: bold;'>${settings.headerGreeting}</div>" else ""}
                    ${if (settings.showTicketNumber) "<div class='ticket-num'>TICKET #${transaction.ticketNumber}</div>" else ""}
                    ${if (settings.showDateTime) "<div class='sub-header'>$formattedDate</div>" else ""}
                </div>

                <div class="divider"></div>

                ${if (settings.showBarberName) """
                <div class="row">
                    <span>Atendido por:</span>
                    <span class="bold">${transaction.barberName}</span>
                </div>
                """ else ""}

                <div class="row">
                    <span class="item-title">${transaction.serviceName}</span>
                    <span class="bold">${formattedTotal}</span>
                </div>
                ${if (transaction.notes.isNotBlank()) "<div style='font-size: 10px; color: #555; font-style: italic;'>Nota: ${transaction.notes}</div>" else ""}

                <div class="double-divider"></div>

                <div class="row total-row">
                    <span>TOTAL:</span>
                    <span>${formattedTotal}</span>
                </div>

                <div class="row" style="margin-top: 6px;">
                    <span>Método de Pago:</span>
                    <span class="bold">${paymentBadge}</span>
                </div>

                <div class="divider"></div>

                ${if (settings.showCustomPolicy && settings.customPolicy.isNotBlank()) "<div class='policy'>${settings.customPolicy}</div>" else ""}
                ${if (settings.ticketFooter.isNotBlank()) "<div class='footer'>${settings.ticketFooter}</div>" else ""}
                <div style="font-size: 9px; margin-top: 8px; text-align: center; color: #777;">*** COMPROBANTE DIGITAL ***</div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generatePlainTextReceipt(transaction: TransactionRecord, settings: AppSettings): String {
        val date = formatDate(transaction.timestamp)
        val total = formatCurrency(transaction.totalAmount, settings.currencySymbol)
        val payment = if (transaction.paymentMethod == PaymentMethod.SINPE) "SINPE Móvil" else "Efectivo"

        return buildString {
            appendLine("💈 *${settings.shopName.uppercase()}* 💈")
            if (settings.showSlogan && settings.shopSlogan.isNotBlank()) appendLine(settings.shopSlogan)
            if (settings.showAddress && settings.address.isNotBlank()) appendLine("📍 ${settings.address}")
            if (settings.showPhone && settings.contactPhone.isNotBlank()) appendLine("📞 Tel: ${settings.contactPhone}")
            if (settings.showInstagram && settings.instagram.isNotBlank()) appendLine("📸 Instagram: ${settings.instagram}")
            if (settings.showSinpe && settings.sinpePhone.isNotBlank()) appendLine("📱 SINPE: ${settings.sinpePhone}")
            if (settings.showWifi && settings.wifiInfo.isNotBlank()) appendLine("📶 ${settings.wifiInfo}")
            appendLine("--------------------------------")
            if (settings.headerGreeting.isNotBlank()) appendLine(settings.headerGreeting)
            if (settings.showTicketNumber) appendLine("🧾 *TICKET #${transaction.ticketNumber}*")
            if (settings.showDateTime) appendLine("📅 Fecha: $date")
            if (settings.showBarberName) appendLine("✂️ Barbero: *${transaction.barberName}*")
            appendLine("--------------------------------")
            appendLine("Servicio: *${transaction.serviceName}*")
            if (transaction.notes.isNotBlank()) appendLine("Nota: ${transaction.notes}")
            appendLine("--------------------------------")
            appendLine("💰 *TOTAL: $total*")
            appendLine("💳 Pago: *$payment*")
            appendLine("--------------------------------")
            if (settings.showCustomPolicy && settings.customPolicy.isNotBlank()) appendLine(settings.customPolicy)
            if (settings.ticketFooter.isNotBlank()) appendLine(settings.ticketFooter)
        }
    }

    fun printReceipt(context: Context, transaction: TransactionRecord, settings: AppSettings) {
        val html = generateHtmlTicket(transaction, settings)
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                try {
                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                    if (printManager != null) {
                        val printAdapter = webView.createPrintDocumentAdapter("Ticket_${transaction.ticketNumber}")
                        val jobName = "${settings.shopName} - Ticket #${transaction.ticketNumber}"
                        val printAttributes = PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT)
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                            .build()
                        printManager.print(jobName, printAdapter, printAttributes)
                    } else {
                        Toast.makeText(context, "Servicio de impresión no disponible", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al imprimir: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    fun shareReceipt(context: Context, transaction: TransactionRecord, settings: AppSettings) {
        val text = generatePlainTextReceipt(transaction, settings)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "Compartir Comprobante")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
    }
}
