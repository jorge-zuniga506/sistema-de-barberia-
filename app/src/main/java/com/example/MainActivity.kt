package com.example
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BarberCommissionDetailDialog
import com.example.ui.components.BluetoothPrinterDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BarberGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BarberViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: BarberViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        BarberPosApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun BarberPosApp(viewModel: BarberViewModel) {
  var selectedTab by rememberSaveable { mutableIntStateOf(0) }
  val viewingReceipt by viewModel.viewingReceipt.collectAsStateWithLifecycle()
  val barberCommissionDetailReport by viewModel.barberCommissionDetailReport.collectAsStateWithLifecycle()
  val showBluetoothPrinterDialog by viewModel.showBluetoothPrinterDialog.collectAsStateWithLifecycle()
  val settings by viewModel.settings.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") },
          label = { Text("Inicio", fontSize = 11.sp, fontWeight = if (selectedTab == 0) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = com.example.ui.theme.SophisticatedPrimary,
            selectedTextColor = com.example.ui.theme.SophisticatedPrimary,
            unselectedIconColor = com.example.ui.theme.SophisticatedTextSecondary,
            unselectedTextColor = com.example.ui.theme.SophisticatedTextSecondary,
            indicatorColor = com.example.ui.theme.SophisticatedPrimaryHero
          ),
          modifier = Modifier.testTag("nav_tab_home")
        )
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Cobrar") },
          label = { Text("Cobrar", fontSize = 11.sp, fontWeight = if (selectedTab == 1) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = com.example.ui.theme.SophisticatedPrimary,
            selectedTextColor = com.example.ui.theme.SophisticatedPrimary,
            unselectedIconColor = com.example.ui.theme.SophisticatedTextSecondary,
            unselectedTextColor = com.example.ui.theme.SophisticatedTextSecondary,
            indicatorColor = com.example.ui.theme.SophisticatedPrimaryHero
          ),
          modifier = Modifier.testTag("nav_tab_register")
        )
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Default.Assessment, contentDescription = "Reportes") },
          label = { Text("Reportes", fontSize = 11.sp, fontWeight = if (selectedTab == 2) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = com.example.ui.theme.SophisticatedPrimary,
            selectedTextColor = com.example.ui.theme.SophisticatedPrimary,
            unselectedIconColor = com.example.ui.theme.SophisticatedTextSecondary,
            unselectedTextColor = com.example.ui.theme.SophisticatedTextSecondary,
            indicatorColor = com.example.ui.theme.SophisticatedPrimaryHero
          ),
          modifier = Modifier.testTag("nav_tab_reports")
        )
        NavigationBarItem(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
          label = { Text("Ajustes", fontSize = 11.sp, fontWeight = if (selectedTab == 3) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = com.example.ui.theme.SophisticatedPrimary,
            selectedTextColor = com.example.ui.theme.SophisticatedPrimary,
            unselectedIconColor = com.example.ui.theme.SophisticatedTextSecondary,
            unselectedTextColor = com.example.ui.theme.SophisticatedTextSecondary,
            indicatorColor = com.example.ui.theme.SophisticatedPrimaryHero
          ),
          modifier = Modifier.testTag("nav_tab_settings")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> HomeScreen(
          viewModel = viewModel,
          onNavigateToRegister = { selectedTab = 1 }
        )
        1 -> RegisterScreen(viewModel = viewModel)
        2 -> ReportsScreen(viewModel = viewModel)
        3 -> SettingsScreen(viewModel = viewModel)
      }

      // Thermal Receipt Preview Popup if any receipt is active
      if (viewingReceipt != null) {
        ReceiptDialog(
          transaction = viewingReceipt!!,
          settings = settings,
          onConfigurePrinter = { viewModel.openBluetoothPrinterDialog() },
          onDismiss = { viewModel.closeReceipt() }
        )
      }

      // Detailed Barber Commission History & Service Breakdown Modal
      if (barberCommissionDetailReport != null) {
        BarberCommissionDetailDialog(
          report = barberCommissionDetailReport!!,
          settings = settings,
          onPeriodSelected = { viewModel.setCommissionDetailPeriod(it) },
          onViewReceipt = { viewModel.showReceipt(it) },
          onConfigurePrinter = { viewModel.openBluetoothPrinterDialog() },
          onDismiss = { viewModel.closeBarberCommissionDetail() }
        )
      }

      // Bluetooth Printer Configuration Dialog
      if (showBluetoothPrinterDialog) {
        BluetoothPrinterDialog(
          currentSettings = settings,
          onSavePrinterSettings = { mac, name, paperWidth ->
            viewModel.updatePrinterSettings(mac, name, paperWidth)
          },
          onDismiss = { viewModel.closeBluetoothPrinterDialog() }
        )
      }
    }
  }
}

