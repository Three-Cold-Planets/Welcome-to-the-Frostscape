package frostscape.content;

import arc.graphics.Blending;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.entities.effect.LifetimeEffect;
import frostscape.math.Interps;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Tex;

import static frostscape.util.WeatherUtils.windDirection;
import static mindustry.content.Fx.rand;

public class Fxf {
    public static Effect
    chargeExplode = new Effect(200, e -> {
        Fill.circle(e.x, e.y, e.fslope() * e.fslope() * 5 + e.finpow() * 20);

        rand.setSeed(e.id);
        Lines.stroke(e.fin(Interp.slowFast) * 2);
        for(int i = 0; i < 8; i++){
            float scaling = Time.time/80 *(e.fin() + 1);
            float fin = (rand.random(1) + scaling + i * 0.15f) % 1, fout = 1 - fin;
            float angle = rand.random(360) + Mathf.floor(scaling) * 80;
            float len = 100 * Interp.pow2Out.apply(fout);
            Lines.lineAngle(e.x + Angles.trnsx(angle, len), e.y + Angles.trnsy(angle, len), angle, 12 * fin);
        }
        if(Vars.state.isPlaying() && Mathf.chance(e.fin() * 0.65f * Time.delta)) Effect.shake(e.fin() * 4.85f, 15, e.x, e.y);
    });

    public static Effect

    glowEffect = new Effect(0, e -> {
        Building b = Vars.world.buildWorld(e.x, e.y);
        if(b != null) return;
        TextureRegion region = (TextureRegion) e.data;
        Draw.alpha(e.fslope() * e.fslope());
        Draw.rect(region, e.x, e.y, e.rotation);
        Draw.blend(Blending.additive);
        Draw.rect(region, e.x, e.y, e.rotation);
        Draw.blend();
    });

    public static Effect steamEffect(float lifetime, float radius, Interp interp){
        return new Effect(lifetime, e -> {
            float a = interp.apply(e.fin());
            Fill.circle(e.x + Tmp.v1.set(windDirection()).x * a, e.y + Tmp.v1.y * a, radius * 1 - a);
        });
    };


    public static Effect steamEffect(float lifetime, float radius){
        return new Effect(lifetime, e -> {
            float a = e.fin();
            Fill.circle(e.x + Tmp.v1.set(windDirection()).x * a, e.y + Tmp.v1.y * a, radius * 1 - a);
        });
    };
}
