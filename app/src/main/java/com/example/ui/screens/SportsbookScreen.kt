package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BetSelection
import com.example.data.MatchEntity
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun SportsbookScreen(
    matches: List<MatchEntity>,
    selectedSport: String,
    searchQuery: String,
    promoBannerText: String,
    betSlip: List<BetSelection>,
    onSelectSport: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleSelection: (BetSelection) -> Unit,
    onLaunchAviator: () -> Unit,
    onOpenBetSlip: () -> Unit
) {
    val categories = listOf("All", "LIVE", "Football", "Basketball", "Jackpot")

    val filteredMatches = remember(matches, selectedSport, searchQuery) {
        matches.filter { match ->
            val matchesCategory = when (selectedSport) {
                "All" -> true
                "LIVE" -> match.isLive
                else -> match.sport.equals(selectedSport, ignoreCase = true)
            }
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                match.homeTeam.contains(searchQuery, ignoreCase = true) ||
                    match.awayTeam.contains(searchQuery, ignoreCase = true) ||
                    match.league.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("sportsbook_list"),
            contentPadding = PaddingValues(bottom = if (betSlip.isNotEmpty()) 96.dp else 24.dp)
        ) {
            // Promo Ticker
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF064E3B), Color(0xFF131C31), Color(0xFF4C0519))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Promo",
                        tint = LuckyGoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = promoBannerText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Hero Banner (Aviator + Jackpot CTA)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .height(168.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onLaunchAviator() }
                        .testTag("hero_aviator_banner"),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_aviator_1791620877833),
                            contentDescription = "Play Aviator on LuckyBet",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xEE0A0F1D),
                                            Color(0xBB0A0F1D),
                                            Color(0x330A0F1D)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = LuckyCrimson,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "HOT • AVIATOR",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Surface(
                                    color = LuckyGold.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "1000X MULTIPLIERS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LuckyGoldBright,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "FLY HIGH & CASH OUT INSTANTLY!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Play Kenya's #1 Crash Game on luckybet.com • Instant M-Pesa Payouts",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuckyTextSecondary
                                )
                            }

                            Button(
                                onClick = onLaunchAviator,
                                colors = ButtonDefaults.buttonColors(containerColor = LuckyCrimson),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("play_aviator_cta_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = "Fly",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PLAY AVIATOR NOW",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips & Search Bar
            item {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedSport == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectSport(category) },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (category == "LIVE") {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(LuckyCrimson)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = category,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LuckyGreen,
                                    selectedLabelColor = Color(0xFF042F2E),
                                    containerColor = LuckySurfaceElevated,
                                    labelColor = LuckyTextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = LuckyBorder
                                ),
                                modifier = Modifier.testTag("sport_chip_${category.lowercase(Locale.ROOT)}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                "Search Gor Mahia, Arsenal, Premier League...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuckyTextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search matches",
                                tint = LuckyTextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuckyGreen,
                            unfocusedBorderColor = LuckyBorder,
                            focusedContainerColor = LuckySurfaceDark,
                            unfocusedContainerColor = LuckySurfaceDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_search_input")
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = null,
                            tint = LuckyGreenBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HIGHLIGHTS & LIVE MARKETS (${filteredMatches.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "1X2 • O/U 2.5 • GG",
                        style = MaterialTheme.typography.labelMedium,
                        color = LuckyGoldBright
                    )
                }
            }

            if (filteredMatches.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No matches",
                            tint = LuckyTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No matches found matching your filter.",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary
                        )
                    }
                }
            } else {
                items(filteredMatches, key = { it.id }) { match ->
                    val activeSelection = betSlip.find { it.matchId == match.id }
                    MatchOddsCard(
                        match = match,
                        selectedPickCode = activeSelection?.pickCode,
                        onSelectOdd = { pickCode, marketLabel, oddValue ->
                            onToggleSelection(
                                BetSelection(
                                    matchId = match.id,
                                    matchTitle = "${match.homeTeam} vs ${match.awayTeam}",
                                    league = match.league,
                                    marketName = marketLabel,
                                    pickCode = pickCode,
                                    odd = oddValue
                                )
                            )
                        }
                    )
                }
            }
        }

        // Floating Betika-style Bottom Bet Slip Summary Bar
        AnimatedVisibility(
            visible = betSlip.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {
            val totalOdds = betSlip.fold(1.0) { acc, item -> acc * item.odd }
            Surface(
                color = LuckyGreen,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBetSlip() }
                    .testTag("floating_betslip_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF042F2E),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${betSlip.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = LuckyGreenBright,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "BET SLIP READY",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF042F2E),
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Total Odds: ${String.format(Locale.US, "%.2f", totalOdds)}x",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF064E3B)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "OPEN SLIP",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF042F2E)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open Bet Slip",
                            tint = Color(0xFF042F2E)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchOddsCard(
    match: MatchEntity,
    selectedPickCode: String?,
    onSelectOdd: (pickCode: String, marketLabel: String, oddValue: Double) -> Unit
) {
    var showMoreMarkets by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // League & Live status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.league,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LuckyTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (match.isLive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(LuckyCrimson.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LuckyCrimsonLight)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE ${match.liveMinute}",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyCrimsonLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = match.startTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = LuckyGoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teams & Live Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = match.homeTeam,
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (match.isLive) {
                            Text(
                                text = "${match.homeScore}",
                                style = MaterialTheme.typography.labelLarge,
                                color = LuckyGreenBright
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = match.awayTeam,
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (match.isLive) {
                            Text(
                                text = "${match.awayScore}",
                                style = MaterialTheme.typography.labelLarge,
                                color = LuckyGreenBright
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary 1X2 Odds Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OddButton(
                    label = "1",
                    odd = match.oddHome,
                    isSelected = selectedPickCode == "1",
                    onClick = { onSelectOdd("1", "1X2 • Home (${match.homeTeam})", match.oddHome) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("odd_btn_${match.id}_1")
                )
                OddButton(
                    label = "X",
                    odd = match.oddDraw,
                    isSelected = selectedPickCode == "X",
                    onClick = { onSelectOdd("X", "1X2 • Draw", match.oddDraw) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("odd_btn_${match.id}_X")
                )
                OddButton(
                    label = "2",
                    odd = match.oddAway,
                    isSelected = selectedPickCode == "2",
                    onClick = { onSelectOdd("2", "1X2 • Away (${match.awayTeam})", match.oddAway) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("odd_btn_${match.id}_2")
                )

                // Expand more markets button (+3)
                Surface(
                    color = if (showMoreMarkets) LuckyGold.copy(alpha = 0.25f) else LuckySurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .clickable { showMoreMarkets = !showMoreMarkets }
                        .testTag("more_markets_${match.id}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = if (showMoreMarkets) "▲" else "+3",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyGoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Secondary O/U 2.5 & GG Markets
            AnimatedVisibility(visible = showMoreMarkets) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = LuckyBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OddButton(
                            label = "OV 2.5",
                            odd = match.oddOver25,
                            isSelected = selectedPickCode == "OV2.5",
                            onClick = { onSelectOdd("OV2.5", "Goals • Over 2.5", match.oddOver25) },
                            modifier = Modifier.weight(1f)
                        )
                        OddButton(
                            label = "UN 2.5",
                            odd = match.oddUnder25,
                            isSelected = selectedPickCode == "UN2.5",
                            onClick = { onSelectOdd("UN2.5", "Goals • Under 2.5", match.oddUnder25) },
                            modifier = Modifier.weight(1f)
                        )
                        OddButton(
                            label = "GG (BTTS)",
                            odd = match.oddBttsYes,
                            isSelected = selectedPickCode == "GG",
                            onClick = { onSelectOdd("GG", "Both Teams To Score • Yes", match.oddBttsYes) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OddButton(
    label: String,
    odd: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) LuckyGreen else LuckySurfaceElevated
    val labelColor = if (isSelected) Color(0xFF042F2E) else LuckyTextSecondary
    val oddColor = if (isSelected) Color(0xFF042F2E) else LuckyGoldBright

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = String.format(Locale.US, "%.2f", odd),
                style = MaterialTheme.typography.labelLarge,
                color = oddColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
