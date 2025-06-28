package com.example.beerpal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class OrderList(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null,
    var currency: String? = null,
    val name: String? = null
)

