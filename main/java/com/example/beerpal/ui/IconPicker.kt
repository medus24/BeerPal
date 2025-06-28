package com.example.beerpal.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.runtime.*

@Composable
fun IconPicker(onIconSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onIconSelected(uri)
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Choose Icon")
    }
}
