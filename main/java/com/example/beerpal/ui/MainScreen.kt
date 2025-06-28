package com.example.beerpal.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beerpal.data.model.OrderItem
import com.example.beerpal.viewmodel.BeerPalViewModel
import androidx.compose.ui.text.input.ImeAction
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BeerPalViewModel,
    onOpenLibrary: () -> Unit,
    onCloseList: () -> Unit
) {
    val items by viewModel.items.observeAsState(emptyList())
    val context = LocalContext.current
    val hasActiveListStarted = remember {
        mutableStateOf(viewModel.hasActiveList())
    }
    var editingItem by remember { mutableStateOf<OrderItem?>(null) }
    val isEditingSavedList by viewModel.isEditingSavedList.observeAsState(false)
    var itemToDelete by remember { mutableStateOf<OrderItem?>(null) }
    val currency by viewModel.currency.observeAsState("czk")
    val totalPrice = viewModel.getTotal()
    val hasPricedItems = items.any { it.price != null }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var tempCurrency by remember { mutableStateOf(currency) }
    val listName by viewModel.listName.observeAsState()
    var showNameDialog by remember { mutableStateOf(false) }
    var tempListName by remember { mutableStateOf("") }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color.Black)
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .background(Color.Black)
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = {
                        viewModel.saveCurrentList()
                        hasActiveListStarted.value = false
                        Toast.makeText(context, "Tab finished", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(
                            text = "Finish",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (hasActiveListStarted.value || isEditingSavedList || !items.isEmpty()){
                        Text(
                            text = listName?.ifBlank { "Tab Name" } ?: "",
                            color = if (listName.isNullOrBlank()) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .clickable { showNameDialog = true }
                                .padding(horizontal = 8.dp)
                                .offset(x = (16).dp)

                        )}
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { onOpenLibrary() }) {
                        Icon(Icons.Default.List, contentDescription = "Library", tint = Color.Black) // Black icon
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0), // Light foam beige
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, bottom = 8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "🍺 BeerPal",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107) // Lager yellow
                    )
                )
            }
        },
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize()) {
                if (hasPricedItems) {
                    FloatingActionButton(
                        onClick = { showCurrencyDialog = true },
                        backgroundColor = Color(0xFFFFC107),
                        contentColor = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (48).dp)
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "${"%.2f".format(totalPrice)} $currency",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                    }
                    if (showCurrencyDialog) {
                        AlertDialog(
                            onDismissRequest = { showCurrencyDialog = false },
                            title = {
                                Text(
                                    text = "Change Currency",
                                    color = Color(0xFFFFC107)
                                )
                            },
                            text = {
                                OutlinedTextField(
                                    value = tempCurrency,
                                    onValueChange = { tempCurrency = it },
                                    label = { Text("Currency Symbol", color = Color.White) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            viewModel.updateCurrency(tempCurrency)
                                            showCurrencyDialog = false
                                        }),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        textColor = Color.White,
                                        backgroundColor = Color(0xFF1F1B18), // Dark beer brown
                                        focusedBorderColor = Color(0xFFFFC107),
                                        unfocusedBorderColor = Color.Gray,
                                        cursorColor = Color(0xFFFFC107)
                                    )
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.updateCurrency(tempCurrency)
                                        showCurrencyDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFFFFC107),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text("Save")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showCurrencyDialog = false }) {
                                    Text("Cancel", color = Color.Gray)
                                }
                            },
                            backgroundColor = Color(0xFF120A08), // Same dark background
                            contentColor = Color.White
                        )

                    }
                }
                if (hasActiveListStarted.value || isEditingSavedList || !items.isEmpty()) {
                    FloatingActionButton(
                        onClick = { editingItem = OrderItem(name = "") },
                        backgroundColor = Color(0xFFFFC107),
                        contentColor = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-12).dp)
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            "+",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }


    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Applies 0xFF3E2723
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            if (items.isEmpty() && !hasActiveListStarted.value && !isEditingSavedList) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        viewModel.createNewListFromFavourites()
                        hasActiveListStarted.value = true
                        Toast.makeText(context, "New tab started", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFFFC107),
                        contentColor = Color.Black         // Black text
                    ),
                    modifier = Modifier
                        .padding(top = 240.dp)
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Start New Tab",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items, key = { it.id }) { item ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToEnd) {
                                    itemToDelete = item
                                }
                                false // prevent auto-dismiss
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.StartToEnd),
                            background = {
                                val shape = RoundedCornerShape(24.dp)

                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth()
                                        .heightIn(min = 100.dp)
                                        .clip(shape)
                                        .background(MaterialTheme.colorScheme.error),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = MaterialTheme.colorScheme.onError,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 24.dp)
                                    )
                                }
                            },
                            dismissContent = {
                                ItemCard(
                                    item = item,
                                    viewModel = viewModel,
                                    currency = currency,
                                    onClick = { editingItem = it }
                                )
                            }
                        )
                    }
                }
            }

            editingItem?.let { item ->
                AddEditDialog(
                    item = item,
                    onDismiss = { editingItem = null },
                    onConfirm = {
                        if (item.id == 0) viewModel.addItem(it)
                        else viewModel.updateItem(it)
                        editingItem = null
                    }
                )
            }

            // Deletion Confirmation Dialog
            itemToDelete?.let { item ->
                AlertDialog(
                    onDismissRequest = { itemToDelete = null },
                    title = { Text("Delete Item?", color = Color(0xFFFFC107)) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteItem(item)
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                            itemToDelete = null
                        }) {
                            Text("Delete", color = Color(0xFFD32F2F)) // Red tone
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { itemToDelete = null }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    backgroundColor = Color(0xFF1B0F0A),
                )
            }
            if (showNameDialog) {
                AlertDialog(
                    onDismissRequest = { showNameDialog = false },
                    title = {
                        Text("Name This Tab", color = Color(0xFFFFC107))
                    },
                    text = {
                        OutlinedTextField(
                            value = tempListName,
                            onValueChange = { tempListName = it },
                            label = { Text("Tab Name", color = Color.White) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    viewModel.updateListName(tempListName.trim())
                                    showNameDialog = false
                                }
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color(0xFF1F1B18),
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFFFFC107)
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateListName(tempListName.trim())
                                showNameDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFFFC107),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNameDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    backgroundColor = Color(0xFF120A08),
                    contentColor = Color.White
                )
            }

        }
    }
}


