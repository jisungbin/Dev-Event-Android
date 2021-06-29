/*
 * DevEventAndroid © 2021 용감한 친구들. all rights reserved.
 * DevEventAndroid license is under the MIT.
 *
 * [LazyTag.kt] created by Ji Sungbin on 21. 6. 29. 오전 4:43.
 *
 * Please see: https://github.com/brave-people/Dev-Event-Android/blob/master/LICENSE.
 */

package team.bravepeople.devevent.ui.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import team.bravepeople.devevent.ui.flowrow.FlowRow
import team.bravepeople.devevent.util.ColorUtil


@Composable
private fun Tag(name: String) {
    val shape = RoundedCornerShape(5.dp)
    val color = ColorUtil.getRandom()
    val isDarkColor = ColorUtils.calculateLuminance(color) < 0.5

    Surface(shape = shape, color = Color(color), elevation = 1.dp) {
        Text(
            text = name,
            color = if (isDarkColor) Color.White else Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
fun FlowTag(modifier: Modifier, tags: List<String>) {
    FlowRow(modifier = modifier, verticalGap = 4.dp, horizontalGap = 4.dp) {
        for (tag in tags) {
            Tag(tag)
        }
    }
}

@Composable
fun LazyTag(modifier: Modifier, tags: List<String>) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tagName ->
            Tag(tagName)
        }
    }
}