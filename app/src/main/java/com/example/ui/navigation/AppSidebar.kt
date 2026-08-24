package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameConfig
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandIndigo

@Composable
fun AppSidebar(
    currentGame: GameConfig,
    currentRoute: String?,
    onSelectGame: (GameConfig) -> Unit,
    onNavigateToSavedTickets: () -> Unit,
    savedTicketsCount: Int,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
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
                    text = "🎲 DataLotto Menu",
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
                SidebarSectionHeader(title = "🎮 JUEGOS")

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
                SidebarSectionHeader(title = "🛠️ HERRAMIENTAS")

                // Functional: Boletos Guardados
                val isTicketsActive = currentRoute == NavRoutes.SAVED_TICKETS
                SidebarLinkItem(
                    text = "📂 Boletos Guardados",
                    isActive = isTicketsActive,
                    badgeCount = savedTicketsCount,
                    onClick = {
                        onNavigateToSavedTickets()
                        onCloseDrawer()
                    },
                    testTag = "drawer_saved_tickets"
                )

                // Disabled tools
                DisabledSidebarLinkItem(text = "📊 Visualización de Datos")
                DisabledSidebarLinkItem(text = "🧠 Big Data Intelligence")
                DisabledSidebarLinkItem(text = "🔬 Backtesting Avanzado")
                DisabledSidebarLinkItem(text = "🧮 Calculadora")
                DisabledSidebarLinkItem(text = "📅 Sorteos Oficiales")
                DisabledSidebarLinkItem(text = "📈 Rendimiento de Boletos")
                DisabledSidebarLinkItem(text = "🔔 Alerta de Botes")

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: CONFIGURACIÓN
                SidebarSectionHeader(title = "⚙️ CONFIGURACIÓN")

                DisabledSidebarLinkItem(text = "🗓️ Recordatorios de Sorteos")
                DisabledSidebarLinkItem(text = "🌙 Modo Oscuro")
                DisabledSidebarLinkItem(text = "🔗 Enlaces a URLs")
                DisabledSidebarLinkItem(text = "✉️ Contacto")

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
