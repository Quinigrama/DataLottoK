package com.example.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameConfig
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.tr

@Composable
fun AppSidebar(
    currentGame: GameConfig,
    currentRoute: String?,
    onSelectGame: (GameConfig) -> Unit,
    onNavigateToSavedTickets: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    savedTicketsCount: Int,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    locale: String = "es",
    onToggleLocale: () -> Unit = {}
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp),
        drawerContainerColor = BrandDark,
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(BrandDark)
        ) {
            // Sidebar Header (.sidebar-header)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Text(
                    text = tr("🎲 DataLotto Menu", "🎲 DataLotto Menu"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            // Scrollable Links
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp)
            ) {
                // Section 1: JUEGOS
                SidebarSectionHeader(title = tr("🎮 JUEGOS", "🎮 GAMES"))

                GameConfig.AvailableGames.forEach { game ->
                    val isActive = currentRoute == NavRoutes.GENERATOR && currentGame.id == game.id
                    SidebarLinkItem(
                        text = "${game.flagEmoji} ${game.name}",
                        isActive = isActive,
                        onClick = {
                            onSelectGame(game)
                            onCloseDrawer()
                        },
                        testTag = "drawer_game_${game.id}"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: HERRAMIENTAS
                SidebarSectionHeader(title = tr("🛠️ HERRAMIENTAS", "🛠️ TOOLS"))

                // Functional: Boletos Guardados
                val isTicketsActive = currentRoute == NavRoutes.SAVED_TICKETS
                SidebarLinkItem(
                    text = tr("📂 Boletos Guardados", "📂 Saved Tickets"),
                    isActive = isTicketsActive,
                    badgeCount = savedTicketsCount,
                    onClick = {
                        onNavigateToSavedTickets()
                        onCloseDrawer()
                    },
                    testTag = "drawer_saved_tickets"
                )

                // Functional: Visualización de Datos / Estadísticas
                val isStatsActive = currentRoute == NavRoutes.STATISTICS
                SidebarLinkItem(
                    text = tr("📊 Visualización de Datos", "📊 Data Visualization"),
                    isActive = isStatsActive,
                    onClick = {
                        onNavigateToStatistics()
                        onCloseDrawer()
                    },
                    testTag = "drawer_statistics"
                )

                // Disabled tools
                DisabledSidebarLinkItem(text = tr("🧠 Big Data Intelligence", "🧠 Big Data Intelligence"))
                DisabledSidebarLinkItem(text = tr("🔬 Backtesting Avanzado", "🔬 Advanced Backtesting"))
                DisabledSidebarLinkItem(text = tr("🧮 Calculadora", "🧮 Calculator"))
                DisabledSidebarLinkItem(text = tr("📅 Sorteos Oficiales", "📅 Official Draws"))
                DisabledSidebarLinkItem(text = tr("📈 Rendimiento de Boletos", "📈 Ticket Performance"))
                DisabledSidebarLinkItem(text = tr("🔔 Alerta de Botes", "🔔 Jackpot Alerts"))

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: CONFIGURACIÓN
                SidebarSectionHeader(title = tr("⚙️ CONFIGURACIÓN", "⚙️ SETTINGS"))

                DisabledSidebarLinkItem(text = tr("🗓️ Recordatorios de Sorteos", "🗓️ Draw Reminders"))
                DarkModeToggleItem(
                    isDarkMode = isDarkMode,
                    onToggle = onToggleDarkMode
                )
                SidebarLinkItem(
                    text = if (locale == "en") "🌐 Language (EN)" else "🌐 Idioma (ES)",
                    isActive = false,
                    onClick = onToggleLocale,
                    testTag = "drawer_language_toggle"
                )
                DisabledSidebarLinkItem(text = tr("🔗 Enlaces a URLs", "🔗 External Links"))
                DisabledSidebarLinkItem(text = tr("✉️ Contacto", "✉️ Contact"))

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SidebarSectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.45f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SidebarLinkItem(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    testTag: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(if (isActive) Color.White.copy(alpha = 0.05f) else Color.Transparent)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left border active indicator (4dp solid BrandIndigo)
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(if (isActive) BrandIndigo else Color.Transparent)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            color = if (isActive) Color.White else Color.White.copy(alpha = 0.7f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        if (badgeCount > 0) {
            Badge(
                containerColor = BrandIndigo,
                contentColor = Color.White,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = badgeCount.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DisabledSidebarLinkItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(start = 20.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DarkModeToggleItem(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val darkMsg = tr("Modo oscuro activado", "Dark mode enabled")
    val lightMsg = tr("Modo claro activado", "Light mode enabled")
    Row(
        modifier = modifier
            .testTag("dark_mode_toggle")
            .fillMaxWidth()
            .height(46.dp)
            .clickable {
                onToggle()
                val message = if (!isDarkMode) darkMsg else lightMsg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tr("🌙 Modo Oscuro", "🌙 Dark Mode"),
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isDarkMode) Color(0xFF10B981) else BrandIndigo.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isDarkMode) "ON" else "OFF",
                color = if (isDarkMode) Color.White else BrandIndigo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

