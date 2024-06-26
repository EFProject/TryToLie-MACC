package com.example.trytolie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trytolie.R

@Composable
fun DiceRender(diceValues: List<Int>) {
    Spacer(modifier = Modifier.width(8.dp))
    Row {
        diceValues.forEach { diceValue ->
            val drawableResource = when (diceValue) {
                1 -> R.drawable.dice_1
                2 -> R.drawable.dice_2
                3 -> R.drawable.dice_3
                4 -> R.drawable.dice_4
                5 -> R.drawable.dice_5
                6 -> R.drawable.dice_6
                else -> R.drawable.dice_1 // handle other values
            }

            Image(
                painter = painterResource(id = drawableResource),
                contentDescription = "Dice value: $diceValue",
                modifier = Modifier.size(60.dp)
            )
        }
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
fun DiceRenderDeclaration(declarationValues: List<Int>) {
    Spacer(modifier = Modifier.width(8.dp))
    Row {
        val occurrences = declarationValues[0]
        val value = declarationValues[1]

        repeat(occurrences) {
            val drawableResource = when (value) {
                1 -> R.drawable.dice_1
                2 -> R.drawable.dice_2
                3 -> R.drawable.dice_3
                4 -> R.drawable.dice_4
                5 -> R.drawable.dice_5
                6 -> R.drawable.dice_6
                else -> R.drawable.dice_1 // handle other values
            }

            Image(
                painter = painterResource(id = drawableResource),
                contentDescription = "Dice value: $value",
                modifier = Modifier.size(60.dp)
            )
        }
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Preview
@Composable
fun DiceRenderPreview() {
    DiceRender(listOf(1, 2, 3, 4, 5, 6))
}

@Preview
@Composable
fun DiceRenderDeclarationPreview() {
    DiceRenderDeclaration(listOf(3,4))
}