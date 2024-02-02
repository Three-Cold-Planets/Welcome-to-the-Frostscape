package main.content;

import arc.func.Floatp;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import main.graphics.Layers;
import main.graphics.ModPal;
import main.math.MultiInterp;
import main.util.DrawUtils;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static main.util.WeatherUtils.windDirection;
import static mindustry.content.Fx.rand;

public class Fxf {
    public static MultiInterp
            smokeFade = new MultiInterp(new float[]{0, 0.8f}, new Interp[]{Interp.pow2, Interp.pow2Out});
    private static float percent = 0;

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
    }),

    emberTrail = new Effect(40f, e -> {
        color(Liquids.slag.color, Color.white, e.fout() / 5f + Mathf.randomSeedRange(e.id, 0.12f));
        randLenVectors(e.id, 2, 1f + e.fin() * 3f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, .2f + e.fout() * 1.2f);
        });
    }),

    emberTrailHeight = new Effect(40f, e -> {
        color(Liquids.slag.color, Color.white, e.fout() / 5f + Mathf.randomSeedRange(e.id, 0.12f));
        float height = (float) e.data;
        DrawUtils.speckOffset(e.x, e.y, height, e.fin() * 40, DrawUtils.smokeWeight, Tmp.v1);
        randLenVectors(e.id, 2, 1f + e.fin() * 3f, (x, y) -> {
            Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, .2f + e.fout() * 1.2f);
        });
    });

    public static Effect

    glowEffect = new Effect(0, e -> {
        Draw.z(Layer.floor);
        Tile t = Vars.world.tileWorld(e.x, e.y);
        if(t.block() != Blocks.air) return;
        TextureRegion region = (TextureRegion) e.data;
        Draw.alpha(e.fslope() * e.fslope());
        Draw.rect(region, e.x, e.y, e.rotation);
        Draw.blend(Blending.additive);
        Draw.rect(region, e.x, e.y, e.rotation);
        Draw.blend();
    }),

    powerSpark = new Effect(16, e -> {
        Draw.color(Color.white, Pal.powerLight, e.fin());
        Lines.stroke(e.fout() * 2 + 0.2f);
        Angles.randLenVectors(e.id, 3, 3 + 16 * e.fin(), e.rotation, 180, (x,y) -> {
            Tmp.v1.trns(Mathf.angle(x,y), e.fslope() * 3.6f + 0.7f).add(e.x + x, e.y + y);
            Lines.line(e.x + x, e.y + y, Tmp.v1.x, Tmp.v1.y);
            Drawf.light(e.x + x, e.y + y, Tmp.v1.x, Tmp.v1.y, Lines.getStroke(), Draw.getColor(), 0.45f);
        });
    }),

    sulphuricSmoke = new Effect(450, 100, e -> {
        Draw.color(ModPal.sulphur);
        Draw.alpha(smokeFade.apply(e.fin()));
        float size = Mathf.randomSeed(e.id, 2, 4);
        Draw.z(Mathf.lerp(Layers.smokeLow, Layers.smokeHigh, e.fin()));

        Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 1), e.fin() * 12, e.rotation, 360, (x, y) -> {
            DrawUtils.speckOffset(e.x + x, e.y + y, e.fin(), e.time, DrawUtils.smokeWeight, Tmp.v1);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, size * e.fout(Interp.pow4));
        });
    }),

    steamSmoke = new Effect(85, 100, e -> {
        Draw.color(Pal.lightishGray);
        Draw.alpha(smokeFade.apply(e.fin()) * 0.8f);
        float size = Mathf.randomSeed(e.id, 2, 4);
        Draw.z(Mathf.lerp(Layer.blockOver, Layer.blockBuilding - 1, e.fin()));

        Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 1), e.fin() * 12, e.rotation, 360, (x, y) -> {
            DrawUtils.speckOffset(e.x + x, e.y + y, e.fin(), e.time, DrawUtils.smokeWeight, Tmp.v1);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, size * e.fout(Interp.pow4));
        });
    }),

    sulphurDrops = new Effect(55, e -> {
        Draw.color(ModPal.sulphur);
        Draw.alpha(smokeFade.apply(e.fin()));
        Draw.z(Mathf.lerp(Layers.smokeLow, Layers.smokeHigh, e.fin()));

        Angles.randLenVectors(e.id + 1, (int) (Mathf.randomSeed(e.id, 3) + 1), e.fin() * 12, e.rotation, 360, (x, y) -> {
            DrawUtils.speckOffset(e.x + x, e.y + y, e.fin(), e.time, DrawUtils.smokeWeight, Tmp.v1);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, e.fout() * 1);
        });
    }),

    chainLightning = new Effect(15f, 300f, e -> {
        if(!(e.data instanceof VisualLightningHolder)) return;
        VisualLightningHolder p = (VisualLightningHolder) e.data;

        int seed = e.id;
        //get the start and ends of the lightning, then the distance between them
        float tx = Tmp.v1.set(p.start()).x, ty = Tmp.v1.y, dst = Mathf.dst(Tmp.v2.set(p.end()).x, Tmp.v2.y, tx, ty);


        Tmp.v3.set(p.end()).sub(p.start()).nor();
        float normx = Tmp.v3.x, normy = Tmp.v3.y;

        rand.setSeed(seed);

        float arcWidth = rand.range(dst * p.arc());

        seed = e.id - (int) e.time;

        float angle = Tmp.v1.angleTo(Tmp.v2);

        Floatp arcX = () -> Mathf.sinDeg(percent * 180) * arcWidth;

        //range of lightning strike's vary depending on turret
        float range = p.segLength();
        int links = Mathf.ceil(dst / p.segLength());
        float spacing = dst / links;

        Lines.stroke(p.width() * e.fout());
        Draw.color(Color.white, e.color, e.finpow());
        Fill.circle(Tmp.v2.x, Tmp.v2.y, p.width() * e.fout()/2);

        //begin the line
        Lines.beginLine();

        Lines.linePoint(Tmp.v1.x, Tmp.v1.y);
        float lastx = Tmp.v1.x, lasty = Tmp.v1.y;

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                //line at end
                nx = Tmp.v2.x;
                ny = Tmp.v2.y;
            }else{
                float len = (i + 1) * spacing;
                rand.setSeed(seed + i);
                Tmp.v3.trns(rand.random(360), range/2);
                percent = ((float) (i + 1))/links;

                nx = tx + normx * len + Tmp.v3.x + Tmp.v4.set(0, arcX.get()).rotate(angle).x;
                ny = ty + normy * len + Tmp.v3.y + Tmp.v4.y;
            }

            Drawf.light(lastx, lasty, nx, ny, Lines.getStroke(), Draw.getColor(), Draw.getColor().a);
            lastx = nx;
            lasty = ny;
            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    });
    public static Effect steamEffect(float lifetime, float radius){
        return new Effect(lifetime, e -> {
            float a = e.fin();
            Fill.circle(e.x + Tmp.v1.set(windDirection()).x * a, e.y + Tmp.v1.y * a, radius * 1 - a);
        });
    };

    public interface VisualLightningHolder{
        Vec2 start();

        Vec2 end();

        float width();

        float segLength();

        float arc();
    }
}
