package frostscape.content;

import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.*;
import arc.util.Time;
import frostscape.math.Interps;
import mindustry.Vars;
import mindustry.entities.Effect;

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
}
