package dev.kifranei.ampp.media

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.catalog.components.LiquidButton

import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

internal data class GlassDialogAction(val label: String, val destructive: Boolean, val perform: () -> Unit)

/** Same material, radius and buttons as the audio-quality disclosure. */
@Composable
internal fun GlassPlayerDialogMaterial(backdrop: Backdrop, content: @Composable ColumnScope.() -> Unit = {}) {
    val shape = RoundedCornerShape(32.dp)
    Column(Modifier.fillMaxSize().compatDrawBackdrop(backdrop, shape = { shape }, effects = {
        vibrancy(); blur(20.dp.toPx()); lens(20.dp.toPx(), 32.dp.toPx())
    }, onDrawSurface = { drawRect(Color(0xFF101828).copy(alpha = .35f)) })
        .border(.6.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = .28f),
            Color.White.copy(alpha = .07f), Color.White.copy(alpha = .18f))), shape), content = content)
}

@Composable
internal fun GlassDeleteConfirmation(backdrop: Backdrop, title: String, message: String, actions: List<GlassDialogAction>) {
    GlassPlayerDialogMaterial(backdrop) {
        Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.Center) {
            if (title.isNotBlank()) {
                BasicText(title, Modifier.fillMaxWidth(), TextStyle(color = Color.White, fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Start))
                Spacer(Modifier.height(10.dp))
            }
            BasicText(message, Modifier.fillMaxWidth(), TextStyle(color = Color.White,
                fontSize = 18.sp, textAlign = TextAlign.Start))
            Spacer(Modifier.height(24.dp))
            @Composable fun button(action: GlassDialogAction, modifier: Modifier) {
                // Same buttons as the audio-quality card: white pill = primary (the destructive action),
                // translucent glass pill with white text = secondary.
                LiquidButton(action.perform, backdrop, modifier.height(48.dp),
                    surfaceColor = if (action.destructive) Color.White.copy(alpha = .95f) else Color.White.copy(alpha = .15f)) {
                    val label = if (action.destructive && action.label == "从资料库中删除") "从资料库删除" else action.label
                    BasicText(label, style = TextStyle(color = if (action.destructive) Color(0xFFFF3B30) else Color.White,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center))
                }
            }
            if (actions.size <= 2) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                actions.forEach { button(it, Modifier.weight(1f)) }
            } else Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                actions.forEach { button(it, Modifier.fillMaxWidth()) }
            }
        }
    }
}
