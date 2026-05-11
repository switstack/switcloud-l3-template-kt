package io.switstack.switcloud.switcloudl3.ui.about

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.switstack.switcloud.switcloudclt.domain.SwitcloudClt
import io.switstack.switcloud.switcloudl3.BuildConfig
import io.switstack.switcloud.switcloudl3.R
import io.switstack.switcloud.switcloudl3.common.AndroidVersion
import io.switstack.switcloud.switcloudl3.common.AppVersion
import io.switstack.switcloud.switcloudl3.data.KernelEnum
import java.util.UUID

typealias KernelInfo = Triple<Int, String, String>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val sysInfo = SwitcloudClt.getSystemInfo()

    val context = LocalContext.current
    val switcloudL3Version = remember(context) {
        AppVersion.fromContext(context)?.let { "${it.versionName} (${it.versionCode})" } ?: "Unknown version"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.about)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.about)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AboutContent(
            innerPadding,
            BuildConfig.SWITCLOUD_L2_VERSION,
            sysInfo.firmware,
            AndroidVersion(Build.VERSION.SDK_INT, Build.VERSION.RELEASE),
            AndroidVersion.fromSdkInt(BuildConfig.MIN_SDK_VERSION),
            switcloudL3Version,
            sysInfo.kernels.map {
                Triple(
                    it.id,
                    it.version,
                    it.checksum.takeIf { !it.all { it == '0' || it == ' ' } }
                        ?: stringResource(R.string.unknown_value)
                )
            }
        )
    }
}

@Composable
fun AboutContent(
    innerPadding: PaddingValues,
    l2Version: String,
    mokaVersion: String,
    currentAndroidVersion: AndroidVersion,
    minAndroidVersion: AndroidVersion,
    switcloudL3Version: String,
    entryPointAndKernelsArray: List<KernelInfo>
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    val entryPoint = entryPointAndKernelsArray.find { it.first == KernelEnum.KERNEL_POINT.id }
    val kernels = entryPointAndKernelsArray.filterNot { it.first == KernelEnum.KERNEL_POINT.id }

    Surface {
        Image(
            modifier = Modifier.fillMaxSize(),
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
                    .fillMaxHeight(
                        if (isLandscape) {
                            0.27f
                        } else {
                            0.32f
                        }
                    )
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
                    Modifier
                        .verticalScroll(scrollState)
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = innerPadding.calculateBottomPadding()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElementCard(stringResource(R.string.app_name)) {
                        ElementCardContentText(
                            stringResource(
                                R.string.switcloud_l3_version,
                                switcloudL3Version
                            )
                        )
                    }

                    ElementCard(stringResource(R.string.build_firmware)) {
                        ElementCardContentText(stringResource(R.string.build_version, l2Version))
                        ElementCardContentText(stringResource(R.string.firmware_version, mokaVersion))
                    }

                    ElementCard(stringResource(R.string.android_os)) {
                        Row {
                            ElementCardContentText(stringResource(R.string.current_os_version))
                            Spacer(Modifier.weight(1f))
                            ElementCardContentText(stringResource(R.string.android_version, currentAndroidVersion.release, currentAndroidVersion.sdkInt))
                        }
                        Row {
                            ElementCardContentText(stringResource(R.string.min_supported_version))
                            Spacer(Modifier.weight(1f))
                            ElementCardContentText(stringResource(R.string.android_version, minAndroidVersion.release, minAndroidVersion.sdkInt))
                        }
                    }

                    entryPoint?.let {
                        ElementCard(stringResource(R.string.entry_point)) {
                            ElementCardContentText(stringResource(R.string.kernel_version, it.second))
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                ElementCardContentText(stringResource(R.string.kernel_checksum))
                                Text(
                                    maxLines = 1,
                                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 14.sp),
                                    text = it.third
                                )
                            }
                        }
                    }

                    kernels.forEach { kernelInfo ->
                        KernelEnum.fromId(kernelInfo.first)?.let {
                            ElementCard(it.kernelName) {
                                ElementCardContentText(stringResource(R.string.kernel_version, kernelInfo.second))
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    ElementCardContentText(stringResource(R.string.kernel_checksum))
                                    Text(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .align(Alignment.CenterVertically),
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium,
                                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 14.sp),
                                        text = kernelInfo.third,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ElementCardContentText(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text = text,
    modifier = modifier,
    style = MaterialTheme.typography.bodyMedium
)

@Composable
fun ElementCard(title: String, content: @Composable (ColumnScope.() -> Unit)) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center,
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            content()
        }
    }
}

@Preview
@Preview(device = TABLET)
@Composable
fun AboutScreenPreview() {
    AboutContent(
        PaddingValues(0.dp),
        BuildConfig.SWITCLOUD_L2_VERSION,
        "4.7.0",
        AndroidVersion(Build.VERSION.SDK_INT, Build.VERSION.RELEASE),
        AndroidVersion.fromSdkInt(BuildConfig.MIN_SDK_VERSION),
        "0.0.0 (0)",
        listOf(
            KernelInfo(KernelEnum.KERNEL_MASTERCARD.id, "1.0.0", UUID.randomUUID().toString()),
            KernelInfo(KernelEnum.KERNEL_AMEX.id, "1.0.0", "0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 OOO 0000 0000 0000"),
            KernelInfo(KernelEnum.KERNEL_RUPAY.id, "1.0.0", "000"),
            KernelInfo(KernelEnum.KERNEL_VISA.id, "1.0.0", UUID.randomUUID().toString()),
            KernelInfo(KernelEnum.KERNEL_POINT.id, "1.0.0", UUID.randomUUID().toString())
        )
    )
}
