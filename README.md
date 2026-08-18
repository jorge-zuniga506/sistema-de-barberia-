# 💈 Barbería POS & Gestión de Comisiones

Aplicación moderna para Android diseñada para la administración integral de barberías, peluquerías y salones de belleza. Permite registrar ventas rápidamente, calcular automáticamente comisiones y retenciones por barbero, gestionar pagos por **SINPE Móvil** o **Efectivo**, y conectar impresoras térmicas **Bluetooth (ESC/POS)** con tickets totalmente personalizables.

---

## ✨ Características Principales

### 1. ✂️ Registro Rápido de Servicios y Ventas
- **Selección intuitiva:** Escoge el barbero, servicio o combo, y método de pago (Efectivo / SINPE Móvil).
- **Cálculo automático:** Desglose en tiempo real de la comisión del barbero y la retención del local.
- **Notas y descuentos:** Campo para notas adicionales del cliente o corte.

### 2. 🎨 Personalización Total del Ticket Impreso
El administrador tiene control absoluto sobre el diseño del ticket desde **Ajustes > 🎨 Ticket**:
- **Instagram de la Barbería:** Agrega `@tu_barberia` para ganar seguidores en cada corte.
- **Teléfono y WhatsApp:** Datos de contacto y citas.
- **Teléfono SINPE Móvil:** Número de cuenta para transferencias.
- **Lema o Subtítulo del Negocio:** Frase publicitaria (ej. *"Estilo & Tradición"*).
- **Dirección Física / Sucursal:** Ubicación del local.
- **Red y Clave WiFi para Clientes:** Cortesía visible en el ticket.
- **Red Social / Web adicional:** TikTok, Facebook o enlace web.
- **Encabezado y Mensaje de Despedida:** Personaliza saludos y agradecimientos.
- **Políticas o Nota Legal:** Condiciones de garantía o comprobante.
- **Interruptores de Visibilidad (Switches):** Activa u oculta cualquier campo según tus necesidades.
- **Vista Previa en Tiempo Real:** Visualiza cómo quedará impreso en papel térmico antes de guardar.

### 3. 🖨️ Conectividad con Impresoras Térmicas Bluetooth (ESC/POS)
- **Compatibilidad universal:** Compatible con impresoras térmicas Bluetooth de **58 mm** (portátiles) y **80 mm** (punto de venta).
- **Gestión de Permisos:** Manejo automático y seguro de permisos Bluetooth en Android 12+ (`BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`) y versiones anteriores.
- **Impresión con 1 toque:** Generación de comandos ESC/POS nativos para tickets de cobro y liquidaciones.
- **Opciones de respaldo:** Impresión mediante el sistema Android / PDF y botón para compartir por WhatsApp.

### 4. 📊 Liquidación y Reportes de Comisiones por Barbero
- **Filtros por período:** Hoy, Esta Semana, Este Mes y Total Histórico.
- **Desglose por barbero:** Total de cortes realizados, facturación bruta, comisiones ganadas y retención del local.
- **Impresión de Liquidación:** Emite un comprobante térmico con el desglose para pagarle a cada barbero.

### 5. ⚙️ Administración de Empleados y Catálogo
- **Barberos:** Alta, edición, activación/desactivación y eliminación de barberos.
- **Servicios y Precios:** Catálogo categorizado con precios de venta y opción de definir una retención fija personalizada por servicio.

---

## 📱 Guía de Vinculación de Impresora Bluetooth

1. **Encender la impresora:** Asegúrate de que la impresora térmica tenga batería y papel térmico colocado.
2. **Vincular en Android:** 
   - Ve a **Ajustes de Bluetooth** en tu teléfono o tableta.
   - Busca el dispositivo (nombres comunes: `MTP-2`, `POS-58`, `RPP02N`, `InnerPrinter`).
   - Introduce el código PIN de vinculación (usualmente `0000` o `1234`).
3. **Seleccionar en la App:**
   - Abre la app y dirígete a **Ajustes > Impresora** (o toca el icono de impresora en cualquier ticket).
   - Concede los permisos de Bluetooth cuando la app lo solicite.
   - Selecciona tu impresora de la lista y el ancho de papel (**58 mm** u **80 mm**).
   - Presiona **"Probar Impresión"** para verificar la conexión.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose con Material Design 3 (Tema *Sophisticated Dark*)
- **Base de Datos Local:** Room Database (SQLite) con persistencia local 100% offline
- **Arquitectura:** MVVM (Model-View-ViewModel) con Kotlin Coroutines y StateFlow
- **Impresión:** Comandos ESC/POS directos vía Bluetooth Sockets y Android Print Framework para PDF

---

## 📄 Licencia y Uso
Desarrollado para la gestión profesional de puntos de venta y barberías.
