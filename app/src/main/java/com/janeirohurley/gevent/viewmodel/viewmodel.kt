package com.janeirohurley.gevent.viewmodel;

data class EventUiModel(
    val id: String,
    val title: String,
    val date: String,
    val imageRes: Int,
    val isFavorite: Boolean = false,
    val creatorImageRes: Int,
    val creatorName: String,
    val joinedAvatars: List<Int>,
    val isFree: Boolean = true,
    val price: String? = null
)

