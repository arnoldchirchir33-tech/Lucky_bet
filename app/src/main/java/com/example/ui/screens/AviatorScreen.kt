package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AviatorRoundEntity
import com.example.ui.theme.*
import com.example.viewmodel.AviatorPhase
import com.example.viewmodel.AviatorSlotState
import com.example.viewmodel.LivePlayerEntry
import java.util.Locale
import kotlin.math.ln

@Composable
fun AviatorScreen(
    phase: AviatorPhase,
    currentMultiplier: Double,
    countdownSeconds: Float,
    history: List<AviatorRoundEntity>,
    slot1: AviatorSlotState,
    slot2: AviatorSlotState,
    liveLobby: List<LivePlayerEntry>,
    onUpdateSlotStake: (Int, String) -> Unit,
    onToggleAutoCashout: (Int, Boolean) -> Unit,
    onUpdateAutoTarget: (Int, String) -> Unit,
    onSlotAction: (Int) -> Unit,
    onBackToSports: () -> Unit
) {
    BackHandler { onBackToSports() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .testTag("aviator_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Recent Multiplier History Pills Row
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = "Aviator",
                            tint = LuckyCrimsonLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AVIATOR • PROVABLY FAIR",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        color = LuckyGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "RTP 97% • SHA-256",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyGreenBright,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aviator_history_row")
                ) {
                    items(history, key = { it.id }) { item ->
                        val pillColor = when {
                            item.multiplier >= 10.0 -> Color(0xFFC026D3) // Pink/Magenta 10x+
                            item.multiplier >= 2.0 -> LuckyPurple       // Purple 2x+
                            else -> Color(0xFF0284C7)                   // Blue <2x
                        }
                        Surface(
                            color = pillColor.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.dp, pillColor)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.2fx", item.multiplier),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main Aviator Radar Flight Canvas
        item {
            AviatorRadarCanvas(
                phase = phase,
                currentMultiplier = currentMultiplier,
                countdownSeconds = countdownSeconds
            )
        }

        // Dual Bet Slots (Slot 1 & Slot 2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AviatorBetControlCard(
                    slotIndex = 1,
                    state = slot1,
                    phase = phase,
                    currentMultiplier = currentMultiplier,
                    onStakeChange = { onUpdateSlotStake(1, it) },
                    onToggleAuto = { onToggleAutoCashout(1, it) },
                    onTargetChange = { onUpdateAutoTarget(1, it) },
                    onActionClick = { onSlotAction(1) }
                )

                AviatorBetControlCard(
                    slotIndex = 2,
                    state = slot2,
                    phase = phase,
                    currentMultiplier = currentMultiplier,
                    onStakeChange = { onUpdateSlotStake(2, it) },
                    onToggleAuto = { onToggleAutoCashout(2, it) },
                    onTargetChange = { onUpdateAutoTarget(2, it) },
                    onActionClick = { onSlotAction(2) }
                )
            }
        }

        // Live Kenyan Players Cashout Feed
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LuckyGreenBright)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE BETS IN THIS ROUND (${liveLobby.size + (if (slot1.isActiveInRound) 1 else 0) + (if (slot2.isActiveInRound) 1 else 0)})",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "luckybet.com",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = LuckyBorder)
                    Spacer(modifier = Modifier.height(6.dp))

                    liveLobby.forEach { entry ->
                        val cashed = entry.cashedOutAt
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.username,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuckyTextSecondary
                            )
                            Text(
                                text = "KES ${entry.stake.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyTextPrimary
                            )
                            if (cashed != null) {
                                Surface(
                                    color = LuckyGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.2fx", cashed)} • +KES ${(entry.stake * cashed).toInt()}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LuckyGreenBright,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (phase == AviatorPhase.CRASHED) {
                                Text(
                                    text = "Crashed",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyCrimsonLight
                                )
                            } else {
                                Text(
                                    text = "In Flight...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyGoldBright
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AviatorRadarCanvas(
    phase: AviatorPhase,
    currentMultiplier: Double,
    countdownSeconds: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_pulse")
    val propPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "prop_pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
            .testTag("aviator_canvas_card"),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = when (phase) {
                AviatorPhase.FLYING -> LuckyCrimson
                AviatorPhase.CRASHED -> LuckyCrimsonLight.copy(alpha = 0.5f)
                AviatorPhase.WAITING -> LuckyBorder
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (phase == AviatorPhase.FLYING) Color(0xFF3B071E) else Color(0xFF15203B),
                            Color(0xFF080C17)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val padLeft = 28f
                val padBottom = 28f
                val plotW = (w - padLeft - 24f).coerceAtLeast(10f)
                val plotH = (h - padBottom - 28f).coerceAtLeast(10f)

                // Draw Radar Grid Lines
                val gridLines = 5
                for (i in 0..gridLines) {
                    val y = 20f + (plotH / gridLines) * i
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(padLeft, y),
                        end = Offset(w - 16f, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    val x = padLeft + (plotW / gridLines) * i
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(x, 20f),
                        end = Offset(x, h - padBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (phase == AviatorPhase.FLYING || phase == AviatorPhase.CRASHED) {
                    // Normalized progress [0.05 .. 0.92] from multiplier
                    val norm = (ln(currentMultiplier.coerceAtLeast(1.0)) / ln(25.0))
                        .toFloat()
                        .coerceIn(0.06f, 0.94f)

                    val startX = padLeft
                    val startY = h - padBottom
                    val endX = padLeft + plotW * norm
                    val endY = (h - padBottom) - (plotH * norm * 0.85f)

                    val curvePath = Path().apply {
                        moveTo(startX, startY)
                        quadraticTo(
                            x1 = startX + (endX - startX) * 0.62f,
                            y1 = startY,
                            x2 = endX,
                            y2 = endY
                        )
                    }

                    val fillPath = Path().apply {
                        addPath(curvePath)
                        lineTo(endX, startY)
                        lineTo(startX, startY)
                        close()
                    }

                    val trailColor = if (phase == AviatorPhase.CRASHED) {
                        Color(0xFF64748B)
                    } else {
                        LuckyCrimson
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                trailColor.copy(alpha = 0.42f),
                                trailColor.copy(alpha = 0.03f)
                            )
                        )
                    )

                    drawPath(
                        path = curvePath,
                        color = if (phase == AviatorPhase.CRASHED) Color(0xFF94A3B8) else LuckyCrimsonLight,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    if (phase == AviatorPhase.FLYING) {
                        // Glowing Red Jet Plane at the tip of the curve
                        drawCircle(
                            color = LuckyGoldBright.copy(alpha = 0.35f),
                            radius = 18.dp.toPx() * propPulse,
                            center = Offset(endX, endY)
                        )
                        val planePath = Path().apply {
                            moveTo(endX + 14.dp.toPx(), endY - 8.dp.toPx())
                            lineTo(endX - 12.dp.toPx(), endY - 2.dp.toPx())
                            lineTo(endX - 6.dp.toPx(), endY + 4.dp.toPx())
                            lineTo(endX - 14.dp.toPx(), endY + 11.dp.toPx())
                            lineTo(endX + 4.dp.toPx(), endY + 5.dp.toPx())
                            close()
                        }
                        drawPath(path = planePath, color = LuckyCrimsonLight)
                    }
                }
            }

            // Center Multiplier Overlay and Countdown
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (phase) {
                    AviatorPhase.WAITING -> {
                        com.example.ui.components.AviatorRoundTimer(
                            countdownSeconds = countdownSeconds
                        )
                    }

                    AviatorPhase.FLYING, AviatorPhase.CRASHED -> {
                        com.example.ui.components.AviatorMultiplierDisplay(
                            phase = phase,
                            currentMultiplier = currentMultiplier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AviatorBetControlCard(
    slotIndex: Int,
    state: AviatorSlotState,
    phase: AviatorPhase,
    currentMultiplier: Double,
    onStakeChange: (String) -> Unit,
    onToggleAuto: (Boolean) -> Unit,
    onTargetChange: (String) -> Unit,
    onActionClick: () -> Unit
) {
    val quickChips = listOf(50, 100, 200, 500, 1000)
    val canCashOutNow = phase == AviatorPhase.FLYING && state.isActiveInRound && state.cashedOutMultiplier == null
    val liveWinValue = state.activeStake * currentMultiplier

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = when {
                canCashOutNow -> LuckyGoldBright
                state.isQueuedForNext -> LuckyCrimson
                state.cashedOutMultiplier != null -> LuckyGreen
                else -> LuckyBorder
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("aviator_slot_card_$slotIndex")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top bar: Slot title + Auto Cashout toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BET PANEL #$slotIndex",
                        style = MaterialTheme.typography.labelLarge,
                        color = LuckyTextPrimary
                    )
                    if (state.cashedOutMultiplier != null && state.winAmount != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = LuckyGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "WON KES ${String.format(Locale.US, "%,.0f", state.winAmount)} @ ${String.format(Locale.US, "%.2fx", state.cashedOutMultiplier)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyGreenBright,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Auto Cashout",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = state.autoCashoutEnabled,
                        onCheckedChange = onToggleAuto,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LuckyGreen
                        ),
                        modifier = Modifier.testTag("aviator_auto_switch_$slotIndex")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stake Input + Action Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.1f)) {
                    OutlinedTextField(
                        value = state.stakeText,
                        onValueChange = onStakeChange,
                        label = { Text("Stake (KES)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuckyGreen,
                            unfocusedBorderColor = LuckyBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aviator_stake_input_$slotIndex")
                    )

                    if (state.autoCashoutEnabled) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = state.autoCashoutTarget,
                            onValueChange = onTargetChange,
                            label = { Text("Auto Target (x)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuckyGoldBright,
                                unfocusedBorderColor = LuckyBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("aviator_auto_target_$slotIndex")
                        )
                    }
                }

                // Giant Bet / Cancel / Cash Out Button
                val btnColor = when {
                    canCashOutNow -> LuckyGold
                    state.isQueuedForNext -> LuckyCrimson
                    else -> LuckyGreen
                }
                val btnTextColor = when {
                    canCashOutNow -> Color(0xFF1E1B18)
                    state.isQueuedForNext -> Color.White
                    else -> Color(0xFF042F2E)
                }

                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(if (state.autoCashoutEnabled) 110.dp else 64.dp)
                        .testTag("aviator_action_btn_$slotIndex")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        when {
                            canCashOutNow -> {
                                Text(
                                    text = "CASH OUT",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = btnTextColor,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "KES ${String.format(Locale.US, "%,.0f", liveWinValue)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = btnTextColor
                                )
                            }
                            state.isQueuedForNext -> {
                                Text(
                                    text = "CANCEL",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = btnTextColor,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Waiting for next round",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = btnTextColor.copy(alpha = 0.9f)
                                )
                            }
                            else -> {
                                Text(
                                    text = "BET",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = btnTextColor,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "KES ${state.stakeText.ifBlank { "0" }}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = btnTextColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick KES Stake Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickChips.forEach { amt ->
                    Surface(
                        color = LuckySurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStakeChange(amt.toString()) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "+$amt",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
