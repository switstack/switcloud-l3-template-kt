package io.switstack.switcloud.switcloudl3.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.switstack.switcloud.switcloudl3.R


@Composable
fun ReceiptDialog(
    receipt: Map<Int, String>,
    onDismissRequest: () -> Unit
) {
    val ticketTextStyle = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f), // Limit height so it doesn't take the whole screen
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                        .shadow(16.dp, clip = false)
                        .widthIn(max = 500.dp)
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.payment_receipt),
                            textAlign = TextAlign.Center,
                            style = ticketTextStyle
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        receipt.forEach { (key, value) ->
                            Row(
                                modifier = if (key == R.string.signature) {
                                    Modifier.height(64.dp)
                                } else {
                                    Modifier
                                }
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = stringResource(key),
                                    maxLines = 1,
                                    style = ticketTextStyle,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .weight(1f)
                                )
                                Text(
                                    textAlign = TextAlign.End,
                                    text = value,
                                    maxLines = 1,
                                    style = ticketTextStyle
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, bottom = 16.dp, end = 32.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(onClick = onDismissRequest) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}