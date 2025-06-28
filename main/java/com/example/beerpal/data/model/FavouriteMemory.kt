package com.example.beerpal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_memory")
data class FavouriteMemory(
    @PrimaryKey val name: String
)
