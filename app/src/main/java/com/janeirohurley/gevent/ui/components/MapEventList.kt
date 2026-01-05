package com.janeirohurley.gevent.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.janeirohurley.gevent.viewmodel.EventUiModel

enum class MapEventListOrientation {
    VERTICAL,
    HORIZONTAL
}

@Composable
fun MapEventList(
    events: List<EventUiModel>,
    modifier: Modifier = Modifier,
    orientation: MapEventListOrientation = MapEventListOrientation.VERTICAL,
    onEventClick: (EventUiModel) -> Unit,
    onFavoriteClick: (EventUiModel) -> Unit
) {
    when (orientation) {
        MapEventListOrientation.VERTICAL -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = events,
                    key = { it.id }
                ) { event ->
                    EventCard(
                        title = event.title,
                        date = event.date,
                        imageRes = event.imageRes,
                        isFavorite = event.isFavorite,
                        onClick = { onEventClick(event) },
                        onFavoriteClick = { onFavoriteClick(event) },
                        creatorImageRes = event.creatorImageRes,
                        creatorName = event.creatorName,
                        joinedAvatars = event.joinedAvatars,
                        isFree = event.isFree,
                        price = event.price
                    )
                }
            }
        }
        MapEventListOrientation.HORIZONTAL -> {
            LazyRow(
                modifier = modifier,
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = events,
                    key = { it.id }
                ) { event ->
                    EventCard(
                        title = event.title,
                        date = event.date,
                        imageRes = event.imageRes,
                        isFavorite = event.isFavorite,
                        onClick = { onEventClick(event) },
                        onFavoriteClick = { onFavoriteClick(event) },
                        creatorImageRes = event.creatorImageRes,
                        creatorName = event.creatorName,
                        joinedAvatars = event.joinedAvatars,
                        isFree = event.isFree,
                        price = event.price,
                        modifier = Modifier.width(300.dp)
                    )
                }
            }
        }
    }
}
