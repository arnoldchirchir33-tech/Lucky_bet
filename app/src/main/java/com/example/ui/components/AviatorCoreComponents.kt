package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AviatorPhase
import java.util.Locale

/**
 * 1. MULTIPLIER DISPLAY COMPONENT
 * Displays real-time multiplier growth with color grading (Blue <2x, Purple 2x-10x, Gold 10x+)
 * or distinct 'FLEW AWAY!' / countdown status.
 */
@Composable
fun AviatorMultiplierDisplay(
    phase: AviatorPhase,
    currentMultiplier: Double,
    modifier: Modifier = Modifier
) {
    val multiplierColor by animateColorAsState(
        targetValue = when {
            phase == AviatorPhase.CRASHED -> LuckyCrimsonLight
            currentMultiplier >= 10.0 -> LuckyGoldBright
            currentMultiplier >= 2.0 -> LuckyPurple
            else -> Color.White
        },
        animationSpec = tween(durationMillis = 200),
        label = "multiplier_color"
    )

    Column(
        modifier = modifier.testTag("aviator_multiplier_display"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
            label = "multiplier_phase_animation"
        ) { targetPhase ->
            when (targetPhase) {
                AviatorPhase.FLYING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.US, "%.2fx", currentMultiplier),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 56.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = multiplierColor,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.testTag("multiplier_text")
                        )
                        Surface(
                            color = LuckyGreen.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGreenBright.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(LuckyGreenBright)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE IN FLIGHT",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyGreenBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                AviatorPhase.CRASHED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = LuckyCrimson.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyCrimsonLight)
                        ) {
                            Text(
                                text = "FLEW AWAY!",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyCrimsonLight,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.2fx", currentMultiplier),
                            style = MaterialTheme.typography.displayMedium,
                            color = LuckyCrimsonLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("crashed_multiplier_text")
                        )
                    }
                }

                AviatorPhase.WAITING -> {
                    // Handled inside Round Timer
                    Spacer(modifier = Modifier.size(1.dp))
                }
            }
        }
    }
}

/**
 * 2. LIVE ROUND TIMER COMPONENT
 * Renders circular or linear animated countdown before the next round takeoff.
 */
@Composable
fun AviatorRoundTimer(
    countdownSeconds: Float,
    totalDurationSeconds: Float = 5.0f,
    modifier: Modifier = Modifier
) {
    val progress = (countdownSeconds / totalDurationDuration(totalDurationSeconds)).coerceIn(0f, 1f)

    Card(
        modifier = modifier.testTag("aviator_round_timer"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuckySurfaceElevated.copy(alpha = 0.85f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = "Next round timer",
                    tint = LuckyGoldBright,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "NEXT ROUND IN",
                    style = MaterialTheme.typography.labelLarge,
                    color = LuckyTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(Locale.US, "%.1fs", countdownSeconds),
                style = MaterialTheme.typography.headlineLarge,
                color = LuckyGoldBright,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.testTag("countdown_seconds_text")
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = LuckyCrimson,
                trackColor = LuckyBorder,
                modifier = Modifier
                    .width(180.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .testTag("countdown_progress_bar")
            )
        }
    }
}

private fun totalDurationDuration(duration: Float): Float = if (duration <= 0f) 5f else duration

/**
 * 3. AVIATOR BET / CANCEL / CASH OUT ACTION BUTTON
 * Dynamically switches state between 'BET', 'CANCEL', and 'CASH OUT' based on flight status.
 */
@Composable
fun AviatorActionButton(
    isQueued: Boolean,
    isActiveInRound: Boolean,
    phase: AviatorPhase,
    currentMultiplier: Double,
    stake: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canCashOut = phase == AviatorPhase.FLYING && isActiveInRound
    val liveCashoutAmount = stake * currentMultiplier

    val containerColor = when {
        canCashOut -> LuckyGold
        isQueued -> LuckyCrimson
        else -> LuckyGreen
    }

    val contentColor = when {
        canCashOut -> Color(0xFF1E1B18)
        isQueued -> Color.White
        else -> Color(0xFF042F2E)
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = modifier
            .height(68.dp)
            .testTag(
                when {
                    canCashOut -> "cashout_action_button"
                    isQueued -> "cancel_action_button"
                    else -> "bet_action_button"
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                canCashOut -> {
                    Text(
                        text = "CASH OUT",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = contentColor
                    )
                    Text(
                        text = "KES ${String.format(Locale.US, "%,.2f", liveCashoutAmount)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }

                isQueued -> {
                    Text(
                        text = "CANCEL",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = contentColor
                    )
                    Text(
                        text = "WAITING FOR ROUND",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor.copy(alpha = 0.9f)
                    )
                }

                else -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BET",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = contentColor
                        )
                    }
                    Text(
                        text = "KES ${String.format(Locale.US, "%,.0f", stake)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
