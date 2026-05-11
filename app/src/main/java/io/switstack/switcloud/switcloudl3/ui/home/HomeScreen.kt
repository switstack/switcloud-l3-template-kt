package io.switstack.switcloud.switcloudl3.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.switstack.switcloud.switcloudclt.data.OutcomeParameterSet
import io.switstack.switcloud.switcloudclt.data.UserInterfaceRequestData
import io.switstack.switcloud.switcloudl3.R
import io.switstack.switcloud.switcloudl3.Routes
import io.switstack.switcloud.switcloudl3.data.UserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController?,
    homeViewModel: HomeViewModel = viewModel()
) {
    val isConnected by homeViewModel.isConnected.collectAsStateWithLifecycle()
    val isConnecting by homeViewModel.isConnecting.collectAsStateWithLifecycle()

    val paymentProcessMessage by homeViewModel.paymentProcessMessage.collectAsStateWithLifecycle()

    HomeScreenContent(
        navController,
        paymentProcessMessage,
        isConnected,
        isConnecting,
        homeViewModel::authenticate,
        homeViewModel::startPayment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    navController: NavController?,
    paymentProcessMessage: UserInfo.UserMessage?,
    isConnected: Boolean,
    isConnecting: Boolean,
    authenticate: () -> Unit,
    startPayment: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        navController?.navigate(Routes.ABOUT)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.about)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillHeight,
                painter = painterResource(
                    if (isLandscape) {
                        R.drawable.bg_image_land
                    } else {
                        R.drawable.bg_image_port
                    }
                ),
                contentDescription = "Image background"
            )
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(48.dp, 48.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(32.dp)
                                .clip(shape = RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row {
                                    Text(
                                        text = "UIRD",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Row(Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = "Message",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = paymentProcessMessage?.message ?: "",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(4.dp)
                                        .alpha(
                                            if (isConnecting) {
                                                1.0f
                                            } else {
                                                0f
                                            }
                                        )
                                )
                                Button(
                                    onClick = { authenticate() },
                                    enabled = !isConnecting && !isConnected
                                ) {
                                    Text("Authenticate")
                                }
                                Button(
                                    onClick = { startPayment() },
                                    enabled = !isConnecting && isConnected
                                ) {
                                    Text("Start payment")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Preview(device = TABLET)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        navController = null,
        paymentProcessMessage = UserInfo.UserMessage(
            status = UserInterfaceRequestData.Status.CARD_READ_SUCCESSFULLY.run { "$name (${value.toHexString()})" },
            message = UserInterfaceRequestData.MessageIdentifier.AUTHORIZING_PLEASE_WAIT.run { "$name (${value.toHexString()})" },
            ops = OutcomeParameterSet.Status.TRY_ANOTHER_INTERFACE.run { "$name (${value.toHexString()})" }
        ),
        authenticate = {},
        isConnected = false,
        isConnecting = true,
        startPayment = {}
    )
}
