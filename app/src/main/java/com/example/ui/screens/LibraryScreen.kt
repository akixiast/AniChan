package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LibraryStats
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.UserMediaEntry
import com.example.data.model.UserWatchStatus
import com.example.ui.components.LibraryItemCard
import com.example.ui.components.TrackMediaBottomSheet
import com.example.ui.theme.AniCoralSecondary
import com.example.ui.viewmodel.LibrarySort
import com.example.ui.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.filteredEntries.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val filter by viewModel.filter.collectAsState()

    var isStatsExpanded by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<UserMediaEntry?>(null) }

    val statusTabs = listOf(
        "ALL" to "All",
        "WATCHING" to "Watching / Reading",
        "COMPLETED" to "Completed",
        "PLANNING" to "Planning",
        "PAUSED" to "Paused",
        "DROPPED" to "Dropped",
        "REWATCHING" to "Rewatching",
        "FAVORITES" to "Favorites"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "My Library",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isStatsExpanded = !isStatsExpanded },
                        modifier = Modifier.testTag("toggle_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Stats",
                            tint = if (isStatsExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("library_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Stats Panel (Expandable)
            AnimatedVisibility(visible = isStatsExpanded) {
                LibraryStatsCard(
                    stats = stats,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Media Type Selector (All, Anime, Manga)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All Media", "ANIME" to "Anime", "MANGA" to "Manga").forEach { (type, label) ->
                    val selected = filter.mediaType == type
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setMediaTypeFilter(type) },
                        label = { Text(label, fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("library_type_${type.lowercase()}")
                    )
                }
            }

            // Status Filter Tabs
            PrimaryScrollableTabRow(
                selectedTabIndex = statusTabs.indexOfFirst { it.first == filter.status }.coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                divider = {}
            ) {
                statusTabs.forEach { (statusKey, label) ->
                    val isSelected = filter.status == statusKey
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setStatusFilter(statusKey) },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("tab_status_${statusKey.lowercase()}")
                    )
                }
            }

            // Library Search & Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filter.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Filter your list...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (filter.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Dropdown button
                val nextSort = when (filter.sort) {
                    LibrarySort.UPDATED -> LibrarySort.SCORE
                    LibrarySort.SCORE -> LibrarySort.TITLE
                    LibrarySort.TITLE -> LibrarySort.PROGRESS
                    LibrarySort.PROGRESS -> LibrarySort.UPDATED
                }
                FilterChip(
                    selected = false,
                    onClick = { viewModel.setSort(nextSort) },
                    label = { Text(filter.sort.displayName, fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Library Content List
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No shows in this section",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add anime or manga to your watchlist to start tracking progress!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateToExplore,
                            modifier = Modifier.testTag("explore_anime_button")
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Explore Trending Anime")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries, key = { it.mediaId }) { entry ->
                        LibraryItemCard(
                            entry = entry,
                            onCardClick = { onNavigateToDetail(entry.mediaId) },
                            onEditClick = { entryToEdit = entry },
                            onIncrementProgress = { viewModel.incrementProgress(entry) },
                            onToggleFavorite = { viewModel.toggleFavorite(entry) }
                        )
                    }
                }
            }
        }

        // Edit Entry Bottom Sheet
        val currentEdit = entryToEdit
        if (currentEdit != null) {
            val dummyMedia = MediaItem(
                id = currentEdit.mediaId,
                titleRomaji = currentEdit.title,
                titleEnglish = currentEdit.title,
                type = currentEdit.mediaType,
                format = currentEdit.format,
                episodes = currentEdit.totalEpisodes,
                chapters = currentEdit.totalChapters,
                coverImageLarge = currentEdit.coverImage,
                bannerImage = currentEdit.bannerImage
            )
            TrackMediaBottomSheet(
                media = dummyMedia,
                existingEntry = currentEdit,
                onDismiss = { entryToEdit = null },
                onSave = { updated ->
                    viewModel.saveEntry(updated)
                    entryToEdit = null
                },
                onDelete = { mediaId ->
                    viewModel.deleteEntry(mediaId)
                    entryToEdit = null
                }
            )
        }
    }
}

@Composable
fun LibraryStatsCard(
    stats: LibraryStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Tracking Statistics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(title = "Anime", value = "${stats.totalAnime}")
                StatItem(title = "Episodes", value = "${stats.totalEpisodesWatched}")
                StatItem(title = "Days Watched", value = String.format("%.1f", stats.estimatedDaysWatched))
                StatItem(title = "Mean Score", value = if (stats.meanScore > 0f) String.format("%.1f", stats.meanScore) else "N/A")
            }

            if (stats.totalManga > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(title = "Manga", value = "${stats.totalManga}")
                    StatItem(title = "Chapters", value = "${stats.totalChaptersRead}")
                    StatItem(title = "Watching", value = "${stats.animeWatching}")
                    StatItem(title = "Completed", value = "${stats.animeCompleted}")
                }
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}
