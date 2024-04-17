package com.example.trytolie.game.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.trytolie.R

@Composable
fun GameDialog(
    onDismiss: () -> Unit,
    onExitGame: () -> Unit,
) {
    ClickableListItemsDialog(
        onDismiss = onDismiss,
        items =  listOf(
                    stringResource(R.string.game_exit) to onExitGame),
    )
}

@Preview
@Composable
private fun GameDialogContent() {
    GameDialog(
        onDismiss = {},
        onExitGame = {},
    )
}
