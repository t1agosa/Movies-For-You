package com.tiago.kmpauthflows.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tiago.kmpauthflows.presentation.common.MovieCardUiState

@Composable
fun MovieCard(
    uiState: MovieCardUiState,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onWatchedToggle: () -> Unit,
    onWatchlistToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = uiState.movie.posterUrl,
                contentDescription = uiState.movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)
            )

            Text(
                text = uiState.movie.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            uiState.movie.releaseDate?.take(4)?.let { year ->
                Text(
                    text = year,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Row(modifier = Modifier.padding(4.dp)) {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorita"
                    )
                }
                IconButton(onClick = onWatchedToggle) {
                    Icon(
                        imageVector = if (uiState.isWatched) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Vista"
                    )
                }
                IconButton(onClick = onWatchlistToggle) {
                    Icon(
                        imageVector = if (uiState.isInWatchlist) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Por ver"
                    )
                }
            }
        }
    }
}