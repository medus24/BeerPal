package com.example.beerpal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.beerpal.data.model.OrderItem
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as AndroidColor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.Canvas
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import kotlin.math.*





@Composable
fun AddEditDialog(
    item: OrderItem,
    onDismiss: () -> Unit,
    onConfirm: (OrderItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var price by remember { mutableStateOf(item.price?.toString() ?: "") }
    var color by remember { mutableStateOf(item.color ?: "#FFC107") }
    var isFavourite by remember { mutableStateOf(item.isFavourite) }
    var showColorDialog by remember { mutableStateOf(false) }
    var currency by remember { mutableStateOf(item.iconPath ?: "") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (item.id == 0) "Add Item" else "Edit Item",
                color = Color(0xFFFFC107)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = Color.White) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if(name.trim().isNotEmpty()) {
                                val updatedItem = item.copy(
                                    name = name.trim(),
                                    price = price.toDoubleOrNull(),
                                    color = color,
                                    isFavourite = isFavourite,
                                    iconPath = currency
                                )
                                onConfirm(updatedItem)
                            }
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
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (optional)", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if(name.trim().isNotEmpty()) {
                                val updatedItem = item.copy(
                                    name = name.trim(),
                                    price = price.toDoubleOrNull(),
                                    color = color,
                                    isFavourite = isFavourite,
                                    iconPath = currency
                                )
                                onConfirm(updatedItem)
                            }
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

                var showColorDialog by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(40.dp)
                        .background(
                            try { Color(android.graphics.Color.parseColor(color)) }
                            catch (_: Exception) { Color(0xFFFFF8E1) },
                            CircleShape
                        )
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { showColorDialog = true }
                )

                if (showColorDialog) {
                    ColorWedgeWheelDialog(
                        initialColor = try {
                            Color(android.graphics.Color.parseColor(color))
                        } catch (_: Exception) {
                            Color(0xFFFFF8E1)
                        },
                        onDismiss = { showColorDialog = false },
                        onColorSelected = {
                            color = "#%06X".format(0xFFFFFF and it.toArgb())
                        }
                    )
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(
                        checked = isFavourite,
                        onCheckedChange = { isFavourite = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFC107),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text("Favourite", color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        val updatedItem = item.copy(
                            name = name.trim(),
                            price = price.toDoubleOrNull(),
                            color = color,
                            isFavourite = isFavourite,
                            iconPath = currency
                        )
                        onConfirm(updatedItem)
                    }
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
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        backgroundColor = Color(0xFF120A08), // super dark brown background
        contentColor = Color.White
    )
}

@Composable
fun ColorWedgeWheelDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var selectedHue by remember { mutableStateOf(hueFromColor(initialColor)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Color", color = Color.White) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // now square and full-width
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ColorWedgeWheel(
                    selectedHue = selectedHue,
                    onHueSelected = { selectedHue = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onColorSelected(Color.hsv(selectedHue))
                onDismiss()
            }) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        backgroundColor = Color(0xFF1B0F0A),
        contentColor = Color.White
    )
}

@Composable
fun ColorWedgeWheel(
    selectedHue: Float,
    onHueSelected: (Float) -> Unit,
    hueSteps: Int = 24
) {
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        val distance = hypot(dx, dy)

                        val outerRadius = canvasSize.minDimension / 2.1f
                        val innerRadius = outerRadius / 2.2f // 🔁 Thicker ring

                        if (distance in innerRadius..outerRadius) {
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                            if (angle < 0) angle += 360.0
                            val hue = angle.toFloat()
                            onHueSelected(hue)
                        }
                    }
                }
        ) {
            canvasSize = size

            val center = Offset(size.width / 2, size.height / 2)
            val outerRadius = size.minDimension / 2.1f
            val innerRadius = outerRadius / 2.2f
            val wedgeAngle = 360f / hueSteps

            for (i in 0 until hueSteps) {
                val hue = i * wedgeAngle
                val isSelected = abs(hue - selectedHue) < wedgeAngle / 2f

                val angleCenter = Math.toRadians(hue.toDouble())
                val angle1 = Math.toRadians((hue - wedgeAngle / 2).toDouble())
                val angle2 = Math.toRadians((hue + wedgeAngle / 2).toDouble())

                val offsetOut = if (isSelected) 35f else 0f // 🔁 More "pull out"

                val centerShifted = Offset(
                    (center.x + cos(angleCenter).toFloat() * offsetOut),
                    (center.y + sin(angleCenter).toFloat() * offsetOut)
                )

                val outerPoint1 = Offset(
                    centerShifted.x + cos(angle1).toFloat() * outerRadius,
                    centerShifted.y + sin(angle1).toFloat() * outerRadius
                )
                val outerPoint2 = Offset(
                    centerShifted.x + cos(angle2).toFloat() * outerRadius,
                    centerShifted.y + sin(angle2).toFloat() * outerRadius
                )
                val innerPoint1 = Offset(
                    centerShifted.x + cos(angle1).toFloat() * innerRadius,
                    centerShifted.y + sin(angle1).toFloat() * innerRadius
                )
                val innerPoint2 = Offset(
                    centerShifted.x + cos(angle2).toFloat() * innerRadius,
                    centerShifted.y + sin(angle2).toFloat() * innerRadius
                )

                val path = Path().apply {
                    moveTo(innerPoint1.x, innerPoint1.y)
                    lineTo(outerPoint1.x, outerPoint1.y)
                    lineTo(outerPoint2.x, outerPoint2.y)
                    lineTo(innerPoint2.x, innerPoint2.y)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.hsv(hue, 0.96f, 0.96f),
                    style = Fill
                )

                if (isSelected) {
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(width = 10f)
                    )
                }
            }
        }
    }
}





fun Color.Companion.hsv(hue: Float): Color {
    return Color(AndroidColor.HSVToColor(floatArrayOf(hue, 0.96f, 0.96f)))
}

fun hueFromColor(color: Color): Float {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(color.toArgb(), hsv)
    return hsv[0]
}




