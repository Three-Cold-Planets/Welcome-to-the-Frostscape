package main.world.blocks.environment;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;

public class GlowingFloor extends ParticleFloor {
    public TextureRegion glowRegion;
    public float secondaryLightRadius;
    public Color secondaryLightColor, glowColor;

    public GlowingFloor(String name) {
        super(name);
        variants = 0;
        lightRadius = 37;
        secondaryLightRadius = 14;
        emitLight = true;
        glowColor = secondaryLightColor = Color.white.cpy().lerp(Color.black, 0.15f).a(0.35f);
    }

    @Override
    public void load() {
        super.load();
        glowRegion = Core.atlas.find(name + "-glow");
    }

    @Override
    public void drawEnvironmentLight(Tile tile) {
        float alpha = Mathf.clamp(Mathf.clamp(1 - Mathf.clamp(Mathf.sin(Time.time + Mathf.randomSeed(Point2.pack(tile.x, tile.y), 0, 96) + 1.9f, 12, 1))) * 2) - Math.abs(Mathf.sin(6, 0.05f));
        Draw.blend(Blending.additive);
        Drawf.light(tile.worldx(), tile.worldy(), this.lightRadius, this.lightColor, this.lightColor.a * alpha);
        Drawf.light(tile.worldx(), tile.worldy(), this.secondaryLightRadius, this.secondaryLightColor, this.secondaryLightColor.a * alpha);
        Drawf.light(tile.worldx(), tile.worldy(), glowRegion, 0, glowColor, glowColor.a * alpha);
        Draw.z(Layer.floor);
        Draw.color(glowColor);
        Draw.alpha(alpha);
        Draw.rect(glowRegion, tile.worldx(), tile.worldy());
        Draw.blend();
    }
}
