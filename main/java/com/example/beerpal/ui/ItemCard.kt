package com.example.beerpal.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beerpal.data.model.OrderItem
import com.example.beerpal.viewmodel.BeerPalViewModel
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp


@Composable
fun ItemCard(
    item: OrderItem,
    viewModel: BeerPalViewModel,
    currency: String,
    onClick: (OrderItem) -> Unit
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(item.color ?: "#FFF8E1"))
    } catch (_: Exception) {
        Color(0xFFFFF8E1)
    }

    val outerPadding = 16.dp
    val boxGap = 8.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(outerPadding)
        ) {
            // ✅ Use maxWidth from BoxWithConstraints scope here
            val nameBoxWidth = this.maxWidth * 0.63f
            val quantityBoxWidth = this.maxWidth * 0.35f
            var nameLineCount by remember { mutableStateOf(1) }

            // Quantity Box
            Box(
                modifier = Modifier
                    .width(quantityBoxWidth)
                    .height(55.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // - Button (Fixed min, but flexible)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.updateItem(item.copy(quantity = (item.quantity - 1).coerceAtLeast(0)))
                            },
                            modifier = Modifier
                                .sizeIn(minWidth = 28.dp, minHeight = 28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFFFC107), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Quantity (Fixed)
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.quantity.toString(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }

                    // + Button (Same as -)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.updateItem(item.copy(quantity = (item.quantity + 1).coerceAtMost(200)))
                            },
                            modifier = Modifier
                                .sizeIn(minWidth = 28.dp, minHeight = 28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFFFC107), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }



            // Name & Price
            Column(
                modifier = Modifier
                    .width(nameBoxWidth)
                    .align(Alignment.CenterStart)
                    .clickable { onClick(item) },
                verticalArrangement = Arrangement.spacedBy(boxGap)
            ) {
                // Name Box
                Box(
                    modifier = Modifier
                        .height(if (nameLineCount <= 1) 55.dp else Dp.Unspecified)
                        .fillMaxWidth()
                        .border(
                            width = if (item.isFavourite) 5.dp else 0.dp,
                            color = if (item.isFavourite) Color(0xFFFFD700) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = item.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = Int.MAX_VALUE,
                        onTextLayout = { result ->
                            nameLineCount = result.lineCount
                        },
                        lineHeight = 30.sp
                    )
                }

                // Price Box
                item.price?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                            .height(28.dp)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "$it $currency",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}









