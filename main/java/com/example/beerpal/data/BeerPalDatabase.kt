package com.example.beerpal.data

import android.content.Context
import androidx.room.*
import com.example.beerpal.data.model.OrderItem
import com.example.beerpal.data.model.OrderList
import com.example.beerpal.data.model.FavouriteMemory

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM OrderItem") suspend fun getAll(): List<OrderItem>
    @Insert suspend fun insert(item: OrderItem)
    @Update suspend fun update(item: OrderItem)
    @Delete suspend fun delete(item: OrderItem)
    @Query("SELECT * FROM OrderItem WHERE isFavourite = 1")
    suspend fun getFavourites(): List<OrderItem>
    @Query("SELECT * FROM OrderItem WHERE listId = :listId")
    suspend fun getByListId(listId: String): List<OrderItem>
    @Query("DELETE FROM OrderItem WHERE listId = :listId")
    suspend fun deleteByListId(listId: String)
    @Query("SELECT COUNT(*) FROM OrderItem WHERE LOWER(name) = LOWER(:name) AND listId = :listId")
    suspend fun countByNameInList(name: String, listId: String): Int
    @Query("UPDATE OrderItem SET isFavourite = 0 WHERE LOWER(name) = LOWER(:name)")
    suspend fun unfavouriteAllWithName(name: String)
}

@Dao
interface OrderListDao {
    @Query("SELECT * FROM OrderList ORDER BY createdAt ASC") suspend fun getAll(): List<OrderList>
    @Insert suspend fun insert(orderlist: OrderList)
    @Delete suspend fun delete(orderlist: OrderList)
    @Query("SELECT * FROM OrderList WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): OrderList?
}

@Dao
interface FavouriteMemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: FavouriteMemory)
    @Query("SELECT EXISTS(SELECT 1 FROM favourite_memory WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)))")
    suspend fun wasFavourited(name: String): Boolean
    @Query("DELETE FROM favourite_memory WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name))")
    suspend fun forgetFavourite(name: String)
}


@Database(entities = [OrderItem::class, OrderList::class, FavouriteMemory::class], version = 1)
abstract class BeerPalDatabase : RoomDatabase() {
    abstract fun itemDao(): OrderItemDao
    abstract fun listDao(): OrderListDao
    abstract fun favouriteMemoryDao(): FavouriteMemoryDao


    companion object {
        @Volatile private var INSTANCE: BeerPalDatabase? = null
        fun getDatabase(context: Context): BeerPalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    BeerPalDatabase::class.java,
                    "beerpal.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
