package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AiringScheduleItem
import com.example.data.model.MediaItem
import com.example.data.model.UserMediaEntry
import com.example.data.notification.EpisodeNotificationManager
import com.example.ui.components.AnimeScoreBadge
import com.example.ui.components.FormatBadge
import com.example.ui.components.TrackMediaBottomSheet
import com.example.ui.viewmodel.AiringItemWithUserData
import com.example.ui.viewmodel.AiringViewModel
import com.example.ui.viewmodel.CalendarDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiringScreen(
    viewModel: AiringViewModel,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val displayItems by viewModel.displayItems.collectAsState()
    val context = LocalContext.current

    val notificationManager = remember { EpisodeNotificationManager.getInstance(context) }
    var hasNotifPermission by remember { mutableStateOf(notificationManager.hasNotificationPermission()) }
    var isNotifEnabled by remember { mutableStateOf(notificationManager.isEpisodeNotificationsEnabled()) }

    var mediaToTrack by remember { mutableStateOf<MediaItem?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotifPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notifications enabled for upcoming airing anime!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Airing Schedule",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.currentDateFormatted.isNotBlank()) {
                                Text(
                                    text = uiState.currentDateFormatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Notification toggle action
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                val next = !isNotifEnabled
                                isNotifEnabled = next
                                notificationManager.setEpisodeNotificationsEnabled(next)
                                Toast.makeText(
                                    context,
                                    if (next) "Airing alerts active" else "Airing alerts paused",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.testTag("airing_notification_toggle")
                    ) {
                        Icon(
                            imageVector = if (isNotifEnabled && hasNotifPermission) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Airing Alerts",
                            tint = if (isNotifEnabled && hasNotifPermission) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Search schedule toggle
                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier.testTag("airing_search_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Schedule",
                            tint = if (isSearchExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("airing_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Day selector tabs
            val days = CalendarDay.entries.toTypedArray()
            PrimaryScrollableTabRow(
                selectedTabIndex = days.indexOf(uiState.selectedDay).coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                divider = {}
            ) {
                days.forEach { day ->
                    val isSelected = uiState.selectedDay == day
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectDay(day) },
                        text = {
                            Text(
                                text = day.shortName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("day_tab_${day.shortName.lowercase()}")
                    )
                }
            }

            // Optional Search Input
            AnimatedVisibility(visible = isSearchExpanded) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Filter titles on this day...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }

            // Filter Chips Bar (All Shows vs In My Watchlist)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !uiState.filterWatchlistOnly,
                        onClick = { if (uiState.filterWatchlistOnly) viewModel.toggleWatchlistOnly() },
                        label = { Text("All Releases (${uiState.rawScheduleItems.size})", fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("airing_filter_all")
                    )

                    val watchlistCount = uiState.rawScheduleItems.count { schedule ->
                        displayItems.any { it.schedule.id == schedule.id && it.userEntry != null }
                    }

                    FilterChip(
                        selected = uiState.filterWatchlistOnly,
                        onClick = { viewModel.toggleWatchlistOnly() },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("In My Watchlist", fontSize = 12.sp)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("airing_filter_watchlist")
                    )
                }

                Text(
                    text = "${displayItems.size} shows",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            // Main Airing Timeline Content
            if (uiState.isLoading && uiState.rawScheduleItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("airing_loading_indicator")
                    )
                }
            } else if (displayItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (uiState.filterWatchlistOnly) Icons.Default.Bookmark else Icons.Default.LiveTv,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.filterWatchlistOnly) "No shows from your watchlist air on this day." else "No scheduled anime found for this day.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (uiState.filterWatchlistOnly) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.toggleWatchlistOnly() }
                            ) {
                                Text("View All Airing Shows")
                            }
                        }
                    }
                }
            } else {
                // Group by Time Slot for a crystal clear, clean schedule
                val grouped = displayItems.groupBy { it.timeSlot }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    grouped.forEach { (slot, itemsInSlot) ->
                        item(key = "header_$slot") {
                            TimeSlotHeader(slot = slot, count = itemsInSlot.size)
                        }

                        items(itemsInSlot, key = { it.schedule.id }) { itemData ->
                            CleanAiringScheduleCard(
                                itemData = itemData,
                                onClick = { onNavigateToDetail(itemData.schedule.media.id) },
                                onTrackClick = { mediaToTrack = itemData.schedule.media }
                            )
                        }
                    }
                }
            }
        }

        val trackMedia = mediaToTrack
        if (trackMedia != null) {
            TrackMediaBottomSheet(
                media = trackMedia,
                existingEntry = displayItems.find { it.schedule.media.id == trackMedia.id }?.userEntry,
                onDismiss = { mediaToTrack = null },
                onSave = { entry ->
                    viewModel.saveUserEntry(entry)
                    mediaToTrack = null
                }
            )
        }
    }
}

@Composable
fun TimeSlotHeader(slot: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val indicatorColor = when (slot) {
                "Airing Soon" -> Color(0xFFEF4444) // Red alert
                "Morning" -> Color(0xFFF59E0B) // Amber
                "Afternoon" -> Color(0xFF3B82F6) // Blue
                "Evening & Night" -> Color(0xFF8B5CF6) // Purple
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = slot,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Text(
                text = "$count shows",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun CleanAiringScheduleCard(
    itemData: AiringItemWithUserData,
    onClick: () -> Unit,
    onTrackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule = itemData.schedule
    val userEntry = itemData.userEntry
    val media = schedule.media
    val airingTimeStr = formatAiringTime(schedule.airingAt)
    val countdownStr = formatCountdown(schedule.timeUntilAiring)
    val isLiveSoon = schedule.timeUntilAiring in 0..7200

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("airing_card_${schedule.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (userEntry != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(68.dp)
                    .aspectRatio(0.7f)
            ) {
                AsyncImage(
                    model = media.coverImageLarge,
                    contentDescription = media.displayTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Time & Episode Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isLiveSoon) Color(0xFFEF4444).copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isLiveSoon) Color(0xFFEF4444) else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = airingTimeStr,
                                fontWeight = FontWeight.Bold,
                                color = if (isLiveSoon) Color(0xFFEF4444) else MaterialTheme.colorScheme.secondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Countdown text
                    Text(
                        text = countdownStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiveSoon) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isLiveSoon) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = media.displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Episode & User Watch Status Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Episode ${schedule.episode}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }

                    if (userEntry != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "${userEntry.watchStatus.displayName} (${userEntry.progress} ep)",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                if (media.genres.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = media.genres.take(2).joinToString(" • "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            FilledTonalIconButton(
                onClick = onTrackClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("airing_track_button_${schedule.id}")
            ) {
                Icon(
                    imageVector = if (userEntry != null) Icons.Default.Bookmark else Icons.Default.BookmarkAdd,
                    contentDescription = "Track Show",
                    tint = if (userEntry != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatAiringTime(epochSeconds: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(epochSeconds * 1000))
}

private fun formatCountdown(seconds: Long): String {
    if (seconds <= 0) return "Aired"
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours >= 24 -> "in ${hours / 24}d ${hours % 24}h"
        hours > 0 -> "in ${hours}h ${minutes}m"
        else -> "in ${minutes}m"
    }
}
