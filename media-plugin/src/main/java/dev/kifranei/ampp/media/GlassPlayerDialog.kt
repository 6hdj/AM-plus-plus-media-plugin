package dev.kifranei.ampp.media

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

/**
 * Material copied from AM++'s liquid-glass bottom bar (glass/LiquidBottomTabs.kt `glassPanelMaterial`
 * + GlassNavigation `materialModifier`): vibrancy() + blur(GlassPolicy.PANEL_BLUR_DP = 4dp),
 * lens(24dp, 24dp) (GlassNavigation clamps it to 0.375 * min dimension), surface = containerColor
 * (dark theme 0xFF121212 @ 0.4), host-default Highlight (rim) and Shadow (the compat layer leaves them to the host).
 */
internal object BottomBarGlass {
    val blurDp = 4.dp
    val lensDp = 24.dp
    val container = Color(0xFF121212).copy(alpha = .4f)
}

internal fun com.kyant.backdrop.BackdropEffectScope.bottomBarGlassEffects() {
    vibrancy()
    blur(BottomBarGlass.blurDp.toPx())
    val edge = minOf(BottomBarGlass.lensDp.toPx(), size.minDimension * 0.375f)
    lens(edge, edge)
}

/** Same material, radius and buttons as the audio-quality disclosure. */
@Composable
internal fun GlassPlayerDialogMaterial(backdrop: Backdrop, content: @Composable ColumnScope.() -> Unit = {}) {
    val shape = RoundedCornerShape(32.dp)
    Column(Modifier.fillMaxSize().compatDrawBackdrop(backdrop, shape = { shape },
        effects = { bottomBarGlassEffects() },
        onDrawSurface = { drawRect(BottomBarGlass.container) }), content = content)
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
