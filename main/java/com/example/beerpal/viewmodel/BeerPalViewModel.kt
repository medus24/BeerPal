package com.example.beerpal.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.beerpal.data.BeerPalDatabase
import com.example.beerpal.data.model.OrderItem
import com.example.beerpal.data.model.OrderList
import com.example.beerpal.data.model.FavouriteMemory
import kotlinx.coroutines.launch
import java.util.*
import android.content.SharedPreferences
import java.util.UUID





class BeerPalViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = BeerPalDatabase.getDatabase(app).itemDao()
    val listDao = BeerPalDatabase.getDatabase(app).listDao()
    private val favMemDao = BeerPalDatabase.getDatabase(app).favouriteMemoryDao()
    private val _items = MutableLiveData<List<OrderItem>>(emptyList())
    val items: LiveData<List<OrderItem>> = _items
    val isEditingSavedList = MutableLiveData(false)
    var LoadedUUID = ""
    var LoadedTime = System.currentTimeMillis()
    private val _currency = MutableLiveData<String>("")
    val currency: LiveData<String> = _currency
    private val _listName = MutableLiveData<String?>("")
    val listName: LiveData<String?> = _listName
    val prefs = getApplication<Application>().getSharedPreferences("BeerPalPrefs", Context.MODE_PRIVATE)
    private val LAST_ACTIVE_LIST_KEY = "last_active_list_id"





    init {
        loadItems()
        tryLoadLastActiveList()
    }

    fun hasActiveList(): Boolean {
        return items.value?.any { it.listId == "active" } == true
    }

    fun loadItems() {
        viewModelScope.launch {
            _items.value = dao.getAll().filter { it.listId == "active" }
        }
    }

    fun clearItems() {
        _items.value = emptyList()
    }

    fun addItem(item: OrderItem) {
        viewModelScope.launch {
            val trimmedName = item.name.trim()

            // If the item already exists in this list, do not add
            val exists = dao.countByNameInList(trimmedName, "active") > 0
            if (exists) return@launch

            val wasFavourited = favMemDao.wasFavourited(trimmedName)
            val shouldBeFavourite = item.isFavourite || wasFavourited

            // Always insert into active list
            val activeItem = item.copy(
                listId = "active",
                isFavourite = shouldBeFavourite,
                name = trimmedName
            )
            dao.insert(activeItem)

            // If marked as favourite, also insert into favourites list and memory
            if (shouldBeFavourite) {
                val favExists = dao.countByNameInList(trimmedName, "favourite") == 0
                if (favExists) {
                    val favItem = item.copy(
                        listId = "favourite",
                        isFavourite = true,
                        name = trimmedName
                    )
                    dao.insert(favItem)
                }

                favMemDao.insert(FavouriteMemory(trimmedName))
            }

            loadItems()
            saveListInPlace()
        }
    }

    fun updateItem(item: OrderItem) {
        viewModelScope.launch {
            val trimmedName = item.name.trim()

            // Update current item
            dao.update(item)

            if (!item.isFavourite) {
                dao.unfavouriteAllWithName(trimmedName)
                val favourites = dao.getByListId("favourite")
                favourites.filter {
                    it.name.trim().equals(trimmedName, ignoreCase = true)
                }.forEach { dao.delete(it) }
                favMemDao.forgetFavourite(trimmedName)
            } else {
                favMemDao.insert(FavouriteMemory(trimmedName))
            }

            loadItems()
            saveListInPlace()
        }
    }

    fun deleteItem(item: OrderItem) {
        viewModelScope.launch {
            dao.delete(item)
            loadItems()
        }
        saveListInPlace()
    }

    fun getTotal(): Double {
        return items.value?.sumOf { (it.price ?: 0.0) * it.quantity } ?: 0.0
    }

    fun createNewListFromFavourites() {
        _listName.value = ""
        _currency.value = ""
        viewModelScope.launch {
            dao.deleteByListId("active")
            clearItems()
            val favs = dao.getFavourites()
                .distinctBy { it.name.trim().lowercase() }

            val activeNames = dao.getByListId("active")
                .map { it.name.trim().lowercase() }

            if (favs.isNotEmpty()) {
                val newItems = favs
                    .filter { it.name.trim().lowercase() !in activeNames }
                    .map {
                    it.copy(
                        id = 0,
                        quantity = 0,
                        price = null,
                        listId = "active"
                    )
                }
                newItems.forEach { dao.insert(it) }
            }

            loadItems()
        }
    }




    fun saveCurrentList() {
        viewModelScope.launch {
            val itemsToSave = dao.getByListId("active")
            val nameToSave = listName.value
            val currencyToSave = currency.value
            var listId = LoadedUUID

            if(isEditingSavedList.value == true){
                listDao.delete(OrderList(id = listId))
                dao.deleteByListId(listId)
                listDao.insert(OrderList(id = listId, createdAt = LoadedTime, name = nameToSave, currency = currencyToSave))
                itemsToSave.forEach {
                    dao.insert(it.copy(id = 0, listId = listId))
                }
            } else {
                if (itemsToSave.isEmpty()) {
                    return@launch
                }
                listId = UUID.randomUUID().toString()
            listDao.insert(OrderList(id = listId, createdAt = System.currentTimeMillis(), name = nameToSave, currency = currencyToSave))
                itemsToSave.forEach {
                    dao.insert(it.copy(id = 0, listId = listId))
                }
            }
            prefs.edit().remove("last_active_list_id").apply()
            isEditingSavedList.value = false
            _listName.value = ""
            dao.deleteByListId("active")
            loadItems()
        }
    }

    fun tryLoadLastActiveList() {
        viewModelScope.launch {
            val lastId = prefs.getString("last_active_list_id", null)

            if (!lastId.isNullOrBlank()) {
                loadListFromLibrary(lastId)
            }
        }
    }


    fun saveListInPlace() {
        viewModelScope.launch {
            val itemsToSave = dao.getByListId("active")
            val nameToSave = listName.value
            val currencyToSave = currency.value
            var listId = LoadedUUID

            if (isEditingSavedList.value == true) {
                // Update existing saved list
                listDao.delete(OrderList(id = listId))
                dao.deleteByListId(listId)
                listDao.insert(
                    OrderList(
                        id = listId,
                        createdAt = LoadedTime,
                        name = nameToSave,
                        currency = currencyToSave
                    )
                )
                itemsToSave.forEach {
                    dao.insert(it.copy(id = 0, listId = listId))
                }
            } else {
                if (itemsToSave.isEmpty()) return@launch
                // Save as new named list
                listId = UUID.randomUUID().toString()
                listDao.insert(
                    OrderList(
                        id = listId,
                        createdAt = System.currentTimeMillis(),
                        name = nameToSave,
                        currency = currencyToSave
                    )
                )
                itemsToSave.forEach {
                    dao.insert(it.copy(id = 0, listId = listId))
                }
                prefs.edit().putString(LAST_ACTIVE_LIST_KEY, listId).apply()
                LoadedUUID = listId
                LoadedTime = System.currentTimeMillis()
                isEditingSavedList.value = true
            }
        }
    }



    fun loadListFromLibrary(listId: String) {
        viewModelScope.launch {
            isEditingSavedList.value = true
            LoadedUUID = listId
            LoadedTime = listDao.getById(listId)?.createdAt ?: System.currentTimeMillis()
            _listName.value = listDao.getById(listId)?.name ?: ""
            _currency.value = listDao.getById(listId)?.currency ?: ""
            val items = dao.getByListId(listId)
            dao.deleteByListId("active")
            clearItems()
            items.forEach {
                dao.insert(it.copy(id = 0, listId = "active"))
            }
            loadItems()
            prefs.edit().putString(LAST_ACTIVE_LIST_KEY, listId).apply()
        }
    }

    fun deleteLibraryList(list: OrderList) {
        viewModelScope.launch {
            listDao.delete(list)
            dao.deleteByListId(list.id)
        }
    }

    fun updateCurrency(newCurrency: String) {
        _currency.value = newCurrency
        saveListInPlace()
    }

    fun updateListName(name: String) {
        _listName.value = name
        saveListInPlace()
    }

}
