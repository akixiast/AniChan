package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.MediaGridCard
import com.example.ui.components.TrackMediaBottomSheet
import com.example.ui.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToSearch: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedMediaForTracking by remember { mutableStateOf<MediaItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AniChan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigateToSearch(null) },
                        modifier = Modifier.testTag("explore_search_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(
                        onClick = { viewModel.loadData() },
                        modifier = Modifier.testTag("explore_refresh_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("explore_screen")
    ) { innerPadding ->
        if (uiState.isLoading && uiState.trendingAnime.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("explore_loading_indicator")
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Media Type Switcher Tabs (Anime vs Manga)
                item {
                    TabRow(
                        selectedTabIndex = if (uiState.selectedMediaType == MediaType.ANIME) 0 else 1,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            val index = if (uiState.selectedMediaType == MediaType.ANIME) 0 else 1
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Tab(
                            selected = uiState.selectedMediaType == MediaType.ANIME,
                            onClick = { viewModel.setMediaType(MediaType.ANIME) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Anime", fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("tab_anime")
                        )
                        Tab(
                            selected = uiState.selectedMediaType == MediaType.MANGA,
                            onClick = { viewModel.setMediaType(MediaType.MANGA) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Manga", fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("tab_manga")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Hero Carousel (Top trending shows)
                if (uiState.heroMedia.isNotEmpty() && uiState.selectedMediaType == MediaType.ANIME) {
                    item {
                        val pagerState = rememberPagerState(pageCount = { uiState.heroMedia.take(5).size })
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                val media = uiState.heroMedia[page]
                                HeroBannerCard(
                                    media = media,
                                    onClick = { onNavigateToDetail(media.id) },
                                    onTrackClick = { selectedMediaForTracking = media }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Pager indicator dots
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pagerState.pageCount) { index ->
                                    val isSelected = pagerState.currentPage == index
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .size(if (isSelected) 16.dp else 6.dp, 6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                if (uiState.selectedMediaType == MediaType.ANIME) {
                    // Section 1: Trending Now
                    item {
                        SectionHeader(
                            title = "Trending Now",
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = Color(0xFFFF5722),
                            onSeeAllClick = { onNavigateToSearch("TRENDING_DESC") }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(uiState.trendingAnime, key = { it.id }) { media ->
                                Box(modifier = Modifier.width(135.dp)) {
                                    MediaGridCard(
                                        media = media,
                                        onClick = { onNavigateToDetail(media.id) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Section 2: Popular This Season
                    item {
                        SectionHeader(
                            title = "Popular This Season",
                            subtitle = "${uiState.currentSeasonName.lowercase().replaceFirstChar { it.uppercase() }} ${uiState.currentYear}",
                            icon = Icons.Default.WbSunny,
                            iconTint = Color(0xFFF59E0B),
                            onSeeAllClick = { onNavigateToSearch("POPULARITY_DESC") }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(uiState.seasonalAnime, key = { it.id }) { media ->
                                Box(modifier = Modifier.width(135.dp)) {
                                    MediaGridCard(
                                        media = media,
                                        onClick = { onNavigateToDetail(media.id) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Section 3: All-Time Top Rated
                    item {
                        SectionHeader(
                            title = "All Time Top Rated",
                            icon = Icons.Default.Star,
                            iconTint = Color(0xFF10B981),
                            onSeeAllClick = { onNavigateToSearch("SCORE_DESC") }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(uiState.topRatedAnime, key = { it.id }) { media ->
                                Box(modifier = Modifier.width(135.dp)) {
                                    MediaGridCard(
                                        media = media,
                                        onClick = { onNavigateToDetail(media.id) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Manga View Sections
                    item {
                        SectionHeader(
                            title = "Trending Manga",
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = Color(0xFFFF5722),
                            onSeeAllClick = { onNavigateToSearch("TRENDING_DESC") }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(uiState.trendingManga, key = { it.id }) { media ->
                                Box(modifier = Modifier.width(135.dp)) {
                                    MediaGridCard(
                                        media = media,
                                        onClick = { onNavigateToDetail(media.id) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        SectionHeader(
                            title = "Top Rated Manga",
                            icon = Icons.Default.AutoAwesome,
                            iconTint = Color(0xFF9D4EDD),
                            onSeeAllClick = { onNavigateToSearch("SCORE_DESC") }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(uiState.trendingManga.reversed(), key = { it.id }) { media ->
                                Box(modifier = Modifier.width(135.dp)) {
                                    MediaGridCard(
                                        media = media,
                                        onClick = { onNavigateToDetail(media.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tracking Sheet Modal
        val mediaToTrack = selectedMediaForTracking
        if (mediaToTrack != null) {
            TrackMediaBottomSheet(
                media = mediaToTrack,
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
fun SectionHeader(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (onSeeAllClick != null) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(4.dp)
            )
        }
    }
}
