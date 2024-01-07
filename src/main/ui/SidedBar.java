package main.ui;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.ScissorStack;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.util.pooling.Pools;
import mindustry.gen.Tex;
import mindustry.ui.Fonts;

public class SidedBar extends BaseBar{
    @Override
    public void draw() {
        if (this.fraction != null) {
            float computed = Mathf.clamp(this.fraction.get());
            if (this.lastValue > computed) {
                this.blink = 1.0F;
                this.lastValue = computed;
            }

            if (Float.isNaN(this.lastValue)) {
                this.lastValue = 0.0F;
            }

            if (Float.isInfinite(this.lastValue)) {
                this.lastValue = 1.0F;
            }

            if (Float.isNaN(this.value)) {
                this.value = 0.0F;
            }

            if (Float.isInfinite(this.value)) {
                this.value = 1.0F;
            }

            if (Float.isNaN(computed)) {
                computed = 0.0F;
            }

            if (Float.isInfinite(computed)) {
                computed = 1.0F;
            }

            this.blink = Mathf.lerpDelta(this.blink, 0.0F, 0.2F);
            this.value = Mathf.lerpDelta(this.value, computed, 0.15F);
            Drawable bar = Tex.bar;
            if (this.outlineRadius > 0.0F) {
                Draw.color(this.outlineColor);
                bar.draw(this.x - this.outlineRadius, this.y - this.outlineRadius, this.width + this.outlineRadius * 2.0F, this.height + this.outlineRadius * 2.0F);
            }

            Draw.colorl(0.1F);
            Draw.alpha(this.parentAlpha);
            bar.draw(this.x, this.y, this.width, this.height);
            Draw.color(this.color, this.blinkColor, this.blink);
            Draw.alpha(this.parentAlpha);
            Drawable top = Tex.barTop;
            float topWidth = this.width * this.value;
            if (topWidth > (float) Core.atlas.find("bar-top").width) {
                top.draw(this.x, this.y, topWidth, this.height);
            } else if (ScissorStack.push(scissor.set(this.x, this.y, topWidth, this.height))) {
                top.draw(this.x, this.y, (float)Core.atlas.find("bar-top").width, this.height);
                ScissorStack.pop();
            }

            Draw.color();
            Font font = Fonts.outline;
            GlyphLayout lay = (GlyphLayout) Pools.obtain(GlyphLayout.class, GlyphLayout::new);
            lay.setText(font, this.name);
            font.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            font.getCache().clear();
            font.getCache().addText(this.name, this.x + this.width / 2.0F - lay.width / 2.0F, this.y + this.height / 2.0F + lay.height / 2.0F + 1.0F);
            font.getCache().draw(this.parentAlpha);
            Pools.free(lay);
        }
    }
}
