package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import coil.request.ImageRequest
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.AnimeScoreBadge
import com.example.ui.components.FormatBadge
import com.example.ui.components.MediaGridCard
import com.example.ui.components.TrackMediaBottomSheet
import com.example.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var selectedMediaForTracking by remember { mutableStateOf<MediaItem?>(null) }

    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()

    // Detect when user scrolls near the end to trigger infinite loading
    val shouldLoadMoreGrid by remember {
        derivedStateOf {
            val totalItems = uiState.results.size
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItem >= totalItems - 6
        }
    }

    val shouldLoadMoreList by remember {
        derivedStateOf {
            val totalItems = uiState.results.size
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItem >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMoreGrid) {
        if (shouldLoadMoreGrid && uiState.hasNextPage && !uiState.isLoading && !uiState.isLoadingMore) {
            viewModel.loadNextPage()
        }
    }

    LaunchedEffect(shouldLoadMoreList) {
        if (shouldLoadMoreList && uiState.hasNextPage && !uiState.isLoading && !uiState.isLoadingMore) {
            viewModel.loadNextPage()
        }
    }

    val genres = listOf(
        "Action", "Adventure", "Comedy", "Drama", "Fantasy", "Romance",
        "Sci-Fi", "Slice of Life", "Supernatural", "Mystery", "Psychological",
        "Horror", "Sports", "Mecha", "Music", "Thriller", "Isekai", "Mahou Shoujo"
    )

    val countries = listOf(
        null to "All Origins",
        "JP" to "🇯🇵 Japan (Anime/Manga)",
        "KR" to "🇰🇷 Korea (Manhwa/Webtoon)",
        "CN" to "🇨🇳 China (Donghua/Manhua)"
    )

    val sorts = listOf(
        "POPULARITY_DESC" to "Most Popular",
        "SCORE_DESC" to "Highest Rated",
        "TRENDING_DESC" to "Trending Now",
        "FAVOURITES_DESC" to "Most Favorites",
        "START_DATE_DESC" to "Newest Releases",
        "START_DATE" to "Oldest Classics (History)",
        "TITLE_ROMAJI" to "Title (A to Z)"
    )

    val formats = if (uiState.mediaType == MediaType.ANIME) {
        listOf("TV", "MOVIE", "OVA", "ONA", "SPECIAL", "TV_SHORT", "MUSIC")
    } else {
        listOf("MANGA", "NOVEL", "ONE_SHOT")
    }

    val eraDecades = listOf(
        null to "All Eras",
        2026 to "2026",
        2025 to "2025",
        2024 to "2024",
        2023 to "2023",
        2022 to "2022",
        2020 to "Early 2020s",
        2015 to "2010s Era",
        2005 to "2000s Era",
        1995 to "90s Classics",
        1985 to "80s Retro",
        1975 to "Vintage & Origins"
    )

    val seasons = listOf("WINTER", "SPRING", "SUMMER", "FALL")

    // Count active filters
    val activeFilterCount = listOfNotNull(
        uiState.selectedCountry,
        uiState.selectedGenre,
        uiState.selectedSeason,
        uiState.selectedYear,
        uiState.selectedFormat,
        uiState.selectedStatus,
        if (uiState.selectedSort != "POPULARITY_DESC") uiState.selectedSort else null
    ).size

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = if (uiState.mediaType == MediaType.ANIME) "Search any anime in history..." else "Search manga, manhwa, novels...",
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        if (onNavigateBack != null) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.testTag("search_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Explore"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onQueryChanged("") },
                                modifier = Modifier.testTag("search_clear_button")
                            ) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_field")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Primary Filter & Control Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Anime / Manga Type Toggle
                        FilterChip(
                            selected = uiState.mediaType == MediaType.ANIME,
                            onClick = { viewModel.setMediaType(MediaType.ANIME) },
                            label = { Text("Anime", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("filter_anime_chip")
                        )

                        FilterChip(
                            selected = uiState.mediaType == MediaType.MANGA,
                            onClick = { viewModel.setMediaType(MediaType.MANGA) },
                            label = { Text("Manga & Novels", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("filter_manga_chip")
                        )

                        // Filters Dialog Trigger with Active Badge
                        BadgedBox(
                            badge = {
                                if (activeFilterCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text("$activeFilterCount")
                                    }
                                }
                            }
                        ) {
                            FilterChip(
                                selected = activeFilterCount > 0,
                                onClick = { isFilterSheetOpen = true },
                                label = {
                                    Text(
                                        text = if (activeFilterCount > 0) "Filters ($activeFilterCount)" else "Filters",
                                        fontSize = 12.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("filter_button")
                            )
                        }
                    }

                    // Layout Mode Toggle
                    IconButton(
                        onClick = { viewModel.toggleLayoutMode() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_layout_mode")
                    ) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle View Mode",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Horizontal Quick Filters (Country + Genres)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Country Origin Chips
                    FilterChip(
                        selected = uiState.selectedCountry == "KR",
                        onClick = { viewModel.selectCountry(if (uiState.selectedCountry == "KR") null else "KR") },
                        label = { Text("🇰🇷 Manhwa", fontSize = 11.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )

                    FilterChip(
                        selected = uiState.selectedCountry == "CN",
                        onClick = { viewModel.selectCountry(if (uiState.selectedCountry == "CN") null else "CN") },
                        label = { Text("🇨🇳 Donghua/Manhua", fontSize = 11.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )

                    FilterChip(
                        selected = uiState.selectedCountry == "JP",
                        onClick = { viewModel.selectCountry(if (uiState.selectedCountry == "JP") null else "JP") },
                        label = { Text("🇯🇵 Japanese", fontSize = 11.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Top Genres
                    genres.forEach { genre ->
                        val selected = uiState.selectedGenre == genre
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.selectGenre(genre) },
                            label = { Text(genre, fontSize = 11.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("search_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && uiState.results.isEmpty()) {
                // Shimmer Skeleton Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(9) {
                        ShimmerMediaCard()
                    }
                }
            } else if (uiState.results.isEmpty()) {
                // Empty state with quick suggestions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (uiState.query.isNotBlank()) "No matches for \"${uiState.query}\"" else "Explore Any Anime or Manga",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "AniList database contains hundreds of thousands of anime, manga, manhwa & light novels across all history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Popular Explorations",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val suggestions = if (uiState.mediaType == MediaType.ANIME) {
                        listOf("Frieren", "Solo Leveling", "Demon Slayer", "Jujutsu Kaisen", "One Piece", "Attack on Titan", "Bleach")
                    } else {
                        listOf("Solo Leveling", "Berserk", "Omniscient Reader", "Chainsaw Man", "Tower of God", "Vagabond", "The Beginning After the End", "Monster")
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.forEach { suggestion ->
                            SuggestionChip(
                                onClick = { viewModel.onQueryChanged(suggestion) },
                                label = { Text(suggestion) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (activeFilterCount > 0) {
                        AssistChip(
                            onClick = { viewModel.resetFilters() },
                            label = { Text("Reset Filters") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            } else {
                if (uiState.isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = gridState,
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 90.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.results, key = { it.id }) { media ->
                            MediaGridCard(
                                media = media,
                                onClick = { onNavigateToDetail(media.id) },
                                onQuickTrackClick = { selectedMediaForTracking = media }
                            )
                        }

                        // Bottom Pagination Loader
                        if (uiState.isLoadingMore) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.results, key = { it.id }) { media ->
                            SearchListCard(
                                media = media,
                                onClick = { onNavigateToDetail(media.id) },
                                onTrackClick = { selectedMediaForTracking = media }
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Comprehensive Filters Bottom Sheet
        if (isFilterSheetOpen) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { isFilterSheetOpen = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AniList Filter & History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(onClick = { viewModel.resetFilters() }) {
                                Icon(Icons.Default.RestartAlt, contentDescription = "Reset All")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Country / Origin Filter
                    Text("Country of Origin", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        countries.forEach { (code, label) ->
                            FilterChip(
                                selected = uiState.selectedCountry == code,
                                onClick = { viewModel.selectCountry(code) },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sort By
                    Text("Sort By", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sorts.forEach { (key, label) ->
                            FilterChip(
                                selected = uiState.selectedSort == key,
                                onClick = { viewModel.selectSort(key) },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Era / Historical Year Filter
                    Text("Release Era / Year (All History)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        eraDecades.forEach { (yr, label) ->
                            FilterChip(
                                selected = uiState.selectedYear == yr,
                                onClick = { viewModel.selectYear(yr) },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Format Filter
                    Text("Format", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        formats.forEach { fmt ->
                            FilterChip(
                                selected = uiState.selectedFormat == fmt,
                                onClick = { viewModel.selectFormat(fmt) },
                                label = { Text(fmt, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Filter
                    Text("Airing / Publishing Status", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(null to "All", "RELEASING" to "Releasing", "FINISHED" to "Finished", "NOT_YET_RELEASED" to "Upcoming", "HIATUS" to "Hiatus", "CANCELLED" to "Cancelled").forEach { (st, label) ->
                            FilterChip(
                                selected = uiState.selectedStatus == st,
                                onClick = { viewModel.selectStatus(st) },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Season Filter (Anime only)
                    if (uiState.mediaType == MediaType.ANIME) {
                        Text("Broadcast Season", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            seasons.forEach { sn ->
                                FilterChip(
                                    selected = uiState.selectedSeason == sn,
                                    onClick = { viewModel.selectSeason(sn) },
                                    label = { Text(sn.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { isFilterSheetOpen = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Filters & View Results", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Track Bottom Sheet
        if (selectedMediaForTracking != null) {
            val media = selectedMediaForTracking!!
            TrackMediaBottomSheet(
                media = media,
                existingEntry = null,
                onDismiss = { selectedMediaForTracking = null },
                onSave = { entry ->
                    viewModel.saveUserEntry(entry)
                    selectedMediaForTracking = null
                }
            )
        }
    }
}

@Composable
fun ShimmerMediaCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_trans"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .background(shimmerBrush)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            }
        }
    }
}

@Composable
fun SearchListCard(
    media: MediaItem,
    onClick: () -> Unit,
    onTrackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("search_list_card_${media.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(72.dp)
                    .aspectRatio(0.7f)
            ) {
                AsyncImage(
                    model = media.coverImageLarge.ifBlank { media.coverImageExtraLarge },
                    contentDescription = media.displayTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FormatBadge(format = media.format)
                        if (media.seasonYear != null) {
                            Text(
                                text = "${media.seasonYear}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                    AnimeScoreBadge(score = media.averageScore, compact = true)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = media.displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val info = buildString {
                    if (media.episodes != null) append("${media.episodes} eps • ")
                    if (media.chapters != null) append("${media.chapters} chs • ")
                    if (media.studios.isNotEmpty()) append(media.studios.first())
                }.trimEnd(' ', '•')

                if (info.isNotBlank()) {
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = media.genres.take(3).joinToString(", "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (onTrackClick != null) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onTrackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Track",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
