package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserWatchStatus
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreOrange
import com.example.ui.theme.ScoreRed
import com.example.ui.theme.ScoreYellow
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDropped
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusPlanning
import com.example.ui.theme.StatusRewatching
import com.example.ui.theme.StatusWatching

@Composable
fun AnimeScoreBadge(
    score: Int?,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (score == null || score <= 0) return

    val (bgColor, textColor) = when {
        score >= 80 -> ScoreGreen to Color.White
        score >= 70 -> ScoreYellow to Color.Black
        score >= 60 -> ScoreOrange to Color.White
        else -> ScoreRed to Color.White
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor.copy(alpha = 0.9f))
            .padding(horizontal = if (compact) 5.dp else 7.dp, vertical = if (compact) 2.dp else 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Score",
                tint = textColor,
                modifier = Modifier.size(if (compact) 10.dp else 12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$score%",
                color = textColor,
                fontSize = if (compact) 10.sp else 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FormatBadge(
    format: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = format,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )
    }
}

@Composable
fun StatusBadge(
    status: UserWatchStatus,
    isManga: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        UserWatchStatus.WATCHING -> StatusWatching to Color.White
        UserWatchStatus.COMPLETED -> StatusCompleted to Color.White
        UserWatchStatus.PLANNING -> StatusPlanning to Color.White
        UserWatchStatus.PAUSED -> StatusPaused to Color.Black
        UserWatchStatus.DROPPED -> StatusDropped to Color.White
        UserWatchStatus.REWATCHING -> StatusRewatching to Color.White
    }

    val label = if (isManga) status.getMangaName() else status.displayName

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor.copy(alpha = 0.9f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
