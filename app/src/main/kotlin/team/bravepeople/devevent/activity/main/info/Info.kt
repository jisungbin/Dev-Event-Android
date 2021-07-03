/*
 * DevEventAndroid © 2021 용감한 친구들. all rights reserved.
 * DevEventAndroid license is under the MIT.
 *
 * [Info.kt] created by Ji Sungbin on 21. 6. 24. 오전 12:35.
 *
 * Please see: https://github.com/brave-people/Dev-Event-Android/blob/master/LICENSE.
 */

package team.bravepeople.devevent.activity.main.info

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import team.bravepeople.devevent.BuildConfig
import team.bravepeople.devevent.R
import team.bravepeople.devevent.activity.main.event.database.EventDatabase
import team.bravepeople.devevent.service.ForegroundService
import team.bravepeople.devevent.theme.ColorOrange
import team.bravepeople.devevent.theme.colors
import team.bravepeople.devevent.ui.glideimage.GlideImage
import team.bravepeople.devevent.ui.licenser.License
import team.bravepeople.devevent.ui.licenser.Licenser
import team.bravepeople.devevent.ui.licenser.Project
import team.bravepeople.devevent.util.AlarmUtil
import team.bravepeople.devevent.util.Battery
import team.bravepeople.devevent.util.Data
import team.bravepeople.devevent.util.Web
import team.bravepeople.devevent.util.config.PathConfig
import team.bravepeople.devevent.util.extension.doDelay
import team.bravepeople.devevent.util.extension.noRippleClickable
import team.bravepeople.devevent.util.extension.noRippleLongClickable
import team.bravepeople.devevent.util.extension.toast


@Composable
fun ApplicationInfoDialog(isOpen: MutableState<Boolean>) {
    if (isOpen.value) {
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { isOpen.value = false },
            buttons = {},
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = "알려진 문제 및 버그 제보",
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "프로젝트 이슈에서 진행하실 수 있습니다.\n(클릭)",
                        modifier = Modifier
                            .clickable { Web.open(context, Web.Link.Issue) }
                            .padding(top = 4.dp),
                        fontSize = 13.sp
                    )
                    GlideImage( // todo: 이거 왜 이미지가 안 뜰까요?
                        modifier = Modifier.padding(top = 30.dp),
                        src = "https://img.shields.io/github/stars/brave-people/Dev-Event-Android?style=flat-square"
                    )
                    Text(
                        text = "앱 버전: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )
                    Text(
                        text = """
                            이 앱의 모든 이벤트 정보들은
                            '용감한 친구들' 팀의'Dev-Event' 프로젝트에서 가져옵니다.
                            
                            이 앱을 제작할 수 있게 '용감한 친구들' 팀에 초대해주시고,
                            이벤트 정보들 사용을 허락해 주신
                            Covenant님께 감사드립니다.
                            """.trimIndent(),
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = colors.primary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "이렇게 작은 앱에 약 50개의 클레스가 쓰였다는게 놀랍네요",
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = colors.secondary,
                        fontSize = 10.sp
                    )
                }
            }
        )
    }
}

@Composable
private fun OpenSourceDialog(isOpen: MutableState<Boolean>) {
    if (isOpen.value) {
        AlertDialog(
            onDismissRequest = { isOpen.value = false },
            buttons = {},
            title = {
                Text(
                    text = stringResource(R.string.info_opensource_license),
                    fontSize = 20.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Licenser(
                        listOf(
                            Project(
                                "Kotlin",
                                "https://github.com/JetBrains/kotlin",
                                License.Apache2
                            ),
                            Project("Gradle", "https://github.com/gradle/gradle", License.Apache2),
                            Project(
                                "Android Icons",
                                "https://www.apache.org/licenses/LICENSE-2.0.txt",
                                License.Apache2
                            ),
                            Project(
                                "kotlinx.coroutines",
                                "https://github.com/Kotlin/kotlinx.coroutines",
                                License.Apache2
                            ),
                            Project(
                                "CoreKtx",
                                "https://android.googlesource.com/platform/frameworks/support/",
                                License.Apache2
                            ),
                            Project(
                                "lottie",
                                "https://github.com/airbnb/lottie/blob/master/android-compose.md",
                                License.MIT
                            ),
                            Project("glide", "https://github.com/bumptech/glide", License.BSD),
                            Project(
                                "Browser",
                                "https://developer.android.com/jetpack/androidx/releases/browser",
                                License.Apache2
                            ),
                            Project(
                                "CrashReporter",
                                "https://github.com/MindorksOpenSource/CrashReporter",
                                License.Apache2
                            ),
                            Project("okhttp", "https://github.com/square/okhttp", License.Apache2),
                            Project(
                                "retrofit",
                                "https://github.com/square/retrofit",
                                License.Apache2
                            ),
                            Project(
                                "Room",
                                "https://developer.android.com/jetpack/androidx/releases/room",
                                License.Apache2
                            ),
                            Project("Hilt", "https://dagger.dev/hilt/", License.Apache2),
                            Project(
                                "Jetpack Compose",
                                "https://developer.android.com/jetpack/compose",
                                License.Apache2
                            ),
                            Project(
                                "leakcanary",
                                "https://github.com/square/leakcanary",
                                License.Apache2
                            ),
                            Project(
                                "바른나눔고딕",
                                "https://help.naver.com/support/contents/contents.help?serviceNo=1074&categoryNo=3497",
                                License.CUSTOM("SIL")
                            ),
                            Project(
                                "ConstraintLayout",
                                "https://developer.android.com/jetpack/compose/layouts/constraintlayout",
                                License.Apache2
                            )
                        )
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Info(database: EventDatabase, activity: Activity) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var lottieVisible by remember { mutableStateOf(true) }
    val logoUrl = "https://avatars.githubusercontent.com/u/68955947?s=200&v=4"

    var dbClearButtonLastClick = 0L
    val isOpensourceDialogOpen = remember { mutableStateOf(false) }
    val isApplicationInfoDialogOpen = remember { mutableStateOf(false) }
    var newEventsNotification by remember {
        mutableStateOf(
            Data.read(
                context,
                PathConfig.NewEventNotification,
                "false"
            ).toBoolean()
        )
    }
    var autoEventReload by remember {
        mutableStateOf(
            Data.read(
                context,
                PathConfig.AutoEventReload,
                "false"
            ).toBoolean()
        )
    }

    val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.confetti) }
    val animationState =
        rememberLottieAnimationState(autoPlay = false).apply { speed = .8f }

    OpenSourceDialog(isOpensourceDialogOpen)
    ApplicationInfoDialog(isApplicationInfoDialogOpen)
    doDelay(500) { animationState.toggleIsPlaying() }
    doDelay(3000) { lottieVisible = false }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (main, copyright) = createRefs()

        AnimatedVisibility(
            visible = lottieVisible,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(9999f),
            enter = EnterTransition.None,
            exit = fadeOut()
        ) {
            LottieAnimation(
                spec = animationSpec,
                animationState = animationState,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(main) {
                    top.linkTo(parent.top)
                    bottom.linkTo(copyright.top, 8.dp)
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(100.dp)
                        .noRippleClickable { Web.open(activity, Web.Link.Organization) }
                        .clip(RoundedCornerShape(10.dp)),
                    src = logoUrl
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.Black,
                        fontSize = 25.sp,
                        modifier = Modifier.noRippleClickable {
                            Web.open(
                                activity,
                                Web.Link.Project
                            )
                        }
                    )
                    Text(
                        text = stringResource(R.string.info_description_app),
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val clickedTime = System.currentTimeMillis()
                        if (clickedTime - dbClearButtonLastClick > 2000) {
                            toast(
                                context,
                                activity.getString(R.string.info_button_confirm_clear_db),
                                Toast.LENGTH_LONG
                            )
                            dbClearButtonLastClick = clickedTime
                        } else {
                            coroutineScope.launch(Dispatchers.IO) {
                                Data.clear(context)
                                database.clearAllTables()
                            }
                            toast(context, activity.getString(R.string.info_button_cleared_db))
                            activity.finish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = ColorOrange)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_round_warning_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.info_button_clear_all_db),
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Button(
                    onClick = { isApplicationInfoDialogOpen.value = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colors.secondary)
                ) {
                    Text(
                        text = stringResource(R.string.info_button_app_information),
                        color = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { isOpensourceDialogOpen.value = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_round_source_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.info_opensource_license),
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.info_event_auto_reload))
                Switch(
                    checked = autoEventReload,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.primary,
                        checkedTrackColor = colors.secondary
                    ),
                    onCheckedChange = {
                        if (it) {
                            toast(context, context.getString(R.string.info_toast_battery_life))
                            Battery.requestIgnoreOptimization(context)
                            context.startService(Intent(context, ForegroundService::class.java))
                            AlarmUtil.addReloadTask()
                        }
                        autoEventReload = it
                        Data.save(context, PathConfig.AutoEventReload, it.toString())
                    }
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.info_notification_new_event))
                Switch(
                    checked = newEventsNotification,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.primary,
                        checkedTrackColor = colors.secondary
                    ),
                    onCheckedChange = {
                        if (it && !autoEventReload) {
                            toast(
                                context,
                                context.getString(R.string.info_toast_must_on_event_auto_reload)
                            )
                        }
                        newEventsNotification = it
                        Data.save(context, PathConfig.NewEventNotification, it.toString())
                    }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(copyright) {
                    bottom.linkTo(parent.bottom, 16.dp)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.copyright),
                color = Color.Black,
                fontSize = 10.sp,
                modifier = Modifier.noRippleLongClickable { toast(context, "\uD83D\uDE1B") }
            )
        }
    }
}