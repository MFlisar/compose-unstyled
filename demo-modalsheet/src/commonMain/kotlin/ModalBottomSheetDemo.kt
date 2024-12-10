package com.composables.core.demo

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.*
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden

private val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.5f
}

@Composable
fun ModalBottomSheetDemo() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF800080), Color(0xFFDA70D6))))
    ) {

        val expandInitially = true
        val dismissAllowed = remember { mutableStateOf(true) }


        val shouldDismissOnBackPress by remember {
            derivedStateOf { dismissAllowed.value }
        }
        val shouldDismissOnClickOutside by remember {
            derivedStateOf {  dismissAllowed.value }
        }


        val modalSheetState = rememberModalBottomSheetState(
            initialDetent = if (expandInitially || Peek == null) SheetDetent.FullyExpanded else Peek,
            detents = listOf(Hidden, Peek, FullyExpanded),
            confirmDetentChange = {
                val confirm = if (it == SheetDetent.Hidden) {
                    true
                } else true
                println("TEST - confirmDetentChange = $confirm | ${it.identifier}")
                confirm
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues())
                .clip(RoundedCornerShape(6.dp))
                .clickable(role = Role.Button) { modalSheetState.currentDetent = Peek }
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            BasicText("Show Sheet", style = TextStyle.Default.copy(fontWeight = FontWeight(500)))
        }

        val isCompact = maxWidth < 600.dp

        LaunchedEffect(modalSheetState.isIdle) {

            println("TEST - isIdle = ${modalSheetState.isIdle} | target = ${modalSheetState.currentDetent.identifier}")
            if (modalSheetState.isIdle &&
                modalSheetState.currentDetent == SheetDetent.Hidden
            ) {
                println("TEST - show again")
                modalSheetState.currentDetent = SheetDetent.FullyExpanded
                //bottomSheetState.animateTo(SheetDetent.FullyExpanded)
            }
        }

        LaunchedEffect(modalSheetState.offset) {
            println("TEST - bottomSheetState.offset = ${modalSheetState.offset}")
        }
        LaunchedEffect(modalSheetState.currentDetent) {
            println("TEST - bottomSheetState.currentDetent = ${modalSheetState.currentDetent.identifier}")

        }
        LaunchedEffect(modalSheetState.progress) {
            println("TEST - bottomSheetState.progress = ${modalSheetState.progress}")
        }

        ModalBottomSheet(
            state = modalSheetState,
            properties = ModalSheetProperties(
                dismissOnBackPress = shouldDismissOnBackPress,
                dismissOnClickOutside = shouldDismissOnClickOutside
            ),
            onDismiss = {
                println("TEST - onDismiss callback...")
            }
        ) {
            Scrim(scrimColor = Color.Black.copy(0.3f), enter = fadeIn(), exit = fadeOut())

            Sheet(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .let { if (isCompact) it else it.padding(horizontal = 56.dp) }
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues())
                    .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .imePadding(),
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column {
                        DragIndication(
                            modifier = Modifier.padding(top = 22.dp)
                                .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                                .width(32.dp)
                                .height(4.dp)
                        )
                        BasicText("Content")
                    }
                }
            }
        }
    }
}
