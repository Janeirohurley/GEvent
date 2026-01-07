package com.janeirohurley.gevent.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Ticket : Screen("ticket")
    data object Favorites : Screen("favorites")
    data object Setting : Screen("setting")
    data object Profile : Screen("profile")

    data object CancelBooking : Screen("cancel_booking")
    data object EventDetails : Screen("event_details")
    data object Order : Screen("order_events")
    data object viewTicket : Screen("view_ticket")
}
