package com.example.beerpal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var quantity: Int = 1,
    var price: Double? = null,
    var color: String? = null,
    var iconPath: String? = null,
    var isFavourite: Boolean = false,
    var listId: String = "active" // default active list
)

