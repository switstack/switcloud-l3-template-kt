package io.switstack.switcloud.switcloudl3.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.switstack.switcloud.switcloudl3.R

@Composable
fun PinEntryDialog(
    onDismiss: (String?) -> Unit
) {
    var pin by rememberSaveable { mutableStateOf("") }
    var pinValue by rememberSaveable { mutableStateOf("") }

    fun addDigit(digit: String) {
        if (pin.length < 12) {
            pin += "*"
            pinValue += digit
        }
    }

    fun removeDigit() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            pinValue = pinValue.dropLast(1)
        }
    }

    PinEntryDialogContent(
        pin,
        { digit -> addDigit(digit) },
        { removeDigit() },
        {
            onDismiss(null)
        },
        {
            onDismiss(pinValue)
        }
    )
}

@Composable
fun PinEntryDialogContent(
    pin: String,
    onPinButtonClicked: (String) -> Unit,
    onBackSpaceClicked: () -> Unit,
    onCancelClick: () -> Unit,
    onPinValidationClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(shape = RoundedCornerShape(48.dp, 48.dp, 48.dp, 48.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Overriding the local ripple configuration to null disables ripples for all children
                CompositionLocalProvider(LocalRippleConfiguration provides null) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.enter_your_pin),
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.headlineMedium.fontSize)
                    )
                    Text(
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp),
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.headlineMedium.fontSize),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        text = pin.ifEmpty { "\u200B" }
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .padding(32.dp)
                            .widthIn(max = 500.dp)
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(15) { index ->
                            when (index) {
                                in 0..8, 10 -> {
                                    val number = (index + 1) % 11 // modulo to get only unit part
                                    PinButton(
                                        onClick = { onPinButtonClicked("$number") },
                                        content = { Text("$number", style = MaterialTheme.typography.titleLarge) }
                                    )
                                }

                                12 -> OperationButton(
                                    onClick = onCancelClick,
                                    enableCondition = { true },
                                    enabledButtonColor = Color(0xFFE6572A),
                                    content = {
                                        Icon(
                                            modifier = Modifier.size(32.dp),
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Backspace",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                )

                                13 -> OperationButton(
                                    onClick = onBackSpaceClicked,
                                    enableCondition = { pin.isNotEmpty() },
                                    enabledButtonColor = Color(0xFFFFB441),
                                    content = {
                                        Icon(
                                            modifier = Modifier.size(32.dp),
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Backspace",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                )

                                14 -> OperationButton(
                                    onClick = onPinValidationClick,
                                    enableCondition = { pin.length >= 4 },
                                    enabledButtonColor = Color(0xFF0AA3AA),
                                    content = {
                                        Icon(
                                            modifier = Modifier.size(36.dp),
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Checkmark",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinButton(onClick: () -> Unit, buttonColors: ButtonColors? = null, enabled: Boolean = true, content: @Composable (RowScope.() -> Unit)) =
    FilledTonalButton(
        modifier = Modifier.height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = buttonColors ?: ButtonDefaults.filledTonalButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        enabled = enabled,
        onClick = onClick,
        content = content
    )

@Composable
fun OperationButton(onClick: () -> Unit, enabledButtonColor: Color, enableCondition: () -> Boolean, content: @Composable (RowScope.() -> Unit)) =
    Button(
        modifier = Modifier.height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonColors(
            enabledButtonColor,
            MaterialTheme.colorScheme.onPrimary,
            enabledButtonColor.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.onPrimary
        ),
        enabled = enableCondition(),
        onClick = onClick,
        content = content
    )