package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserMediaEntry
import com.example.data.model.UserWatchStatus
import com.example.ui.theme.AniCoralSecondary

@Composable
fun LibraryItemCard(
    entry: UserMediaEntry,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onIncrementProgress: () -> Unit,
    onDecrementProgress: () -> Unit = {},
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = if (entry.type == "MANGA") entry.totalChapters else entry.totalEpisodes
    val isManga = entry.type == "MANGA"
    val progressUnit = if (isManga) "Ch" else "Ep"

    val progressFraction = if (total != null && total > 0) {
        (entry.progress.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "progress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onCardClick)
            .testTag("library_item_${entry.mediaId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(65.dp)
                    .aspectRatio(0.7f)
            ) {
                AsyncImage(
                    model = entry.coverImage,
                    contentDescription = entry.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        status = entry.watchStatus,
                        isManga = isManga
                    )

                    if (entry.score > 0f) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Score",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (entry.score % 1f == 0f) "${entry.score.toInt()}" else String.format("%.1f", entry.score),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar & Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (total != null && total > 0) {
                            "$progressUnit ${entry.progress} / $total"
                        } else {
                            "$progressUnit ${entry.progress}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    if (entry.repeatCount > 0) {
                        Text(
                            text = "Rewatched: ${entry.repeatCount}x",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (total != null && total > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (animatedProgress >= 1f) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                if (entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Note: ${entry.notes}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Stepper Row: Minus and Plus buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Quick -1 Stepper Button to remove accidental clicks on plus
                    FilledTonalIconButton(
                        onClick = onDecrementProgress,
                        enabled = entry.progress > 0,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("decrement_progress_button_${entry.mediaId}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrement Progress",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quick +1 Stepper Button
                    val isCompleted = total != null && total > 0 && entry.progress >= total
                    FilledTonalIconButton(
                        onClick = onIncrementProgress,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("increment_progress_button_${entry.mediaId}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isCompleted) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Increment Progress",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Favorite Toggle Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("favorite_button_${entry.mediaId}")
                ) {
                    Icon(
                        imageVector = if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (entry.isFavorite) AniCoralSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
