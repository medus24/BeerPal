package com.example.beerpal.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.beerpal.data.model.OrderList
import com.example.beerpal.viewmodel.BeerPalViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    context: Context,
    viewModel: BeerPalViewModel,
    onResumeList: () -> Unit
) {
    var lists by remember { mutableStateOf<List<OrderList>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        lists = viewModel.listDao.getAll().sortedByDescending { it.createdAt }
    }

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
                title = {
                    Text(
                        text = "Tab Book",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { onResumeList() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0), // Foam cream background
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "🍺 BeerPal",
                    color = Color(0xFFFFC107),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Brown background
                .padding(padding)
                .padding(16.dp)
        ) {
            items(lists) { list ->
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(list.createdAt))
                var listToDelete by remember { mutableStateOf<OrderList?>(null) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    backgroundColor = Color(0xFFFFF8E1), // Pale beer tint
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    viewModel.loadListFromLibrary(list.id)
                                    onResumeList()
                                }
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            list.name?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    color = Color(0xFF4E342E),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Text(
                                "Tab Started: $date",
                                color = Color.Black,
                            )
                            list.closedAt?.let {
                                val closed = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(it))
                                Text(
                                    "Closed: $closed",
                                    color = Color.DarkGray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        IconButton(onClick = { listToDelete = list }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFD32F2F) // Red ale
                            )
                        }
                    }
                }

                // Confirmation Dialog
                listToDelete?.let {
                    AlertDialog(
                        onDismissRequest = { listToDelete = null },
                        title = { Text("Delete List?", color = Color.Black) },
                        text = { Text("This will permanently delete the list.", color = Color.Black) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.deleteLibraryList(it)
                                lists = lists.filterNot { l -> l.id == it.id }
                                Toast.makeText(context, "List deleted", Toast.LENGTH_SHORT).show()
                                listToDelete = null
                            }) {
                                Text("Delete", color = Color(0xFFD32F2F))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { listToDelete = null }) {
                                Text("Cancel", color = Color.Gray)
                            }
                        },
                        backgroundColor = Color(0xFFFFF8E1)
                    )
                }
            }
        }

    }
}
