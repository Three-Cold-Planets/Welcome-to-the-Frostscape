package main.entities;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import main.content.Fxf;
import mindustry.entities.Effect;
import mindustry.gen.Groups;
import mindustry.gen.Unit;

public class ModDamage {
    public static Seq<Unit> list = new Seq<>();

    public static void chain(Position origin, Unit current, IntSeq collided, Sound hitSound, Effect hitEffect, float power, float initialPower, float width, float distanceDamageFalloff, float pierceDamageFactor, int branches, float segmentLength, float arc, Color color){
        float newDamage = power - distanceDamageFalloff * origin.dst(current);
        if(newDamage < 0) return;
        current.damage(newDamage);
        hitSound.at(current.x, current.y, Mathf.random(0.8f, 1.1f));
        //Scales down width based on percent of power left
        float w = width * power/(initialPower);

        Fxf.chainLightning.at(current.x, current.y, 0, color, new Fxf.VisualLightningHolder() {
            @Override
            public Vec2 start() {
                return new Vec2(origin.getX(), origin.getY());
            }

            @Override
            public Vec2 end() {
                return new Vec2(current.x, current.y);
            }

            @Override
            public float width() {
                return w;
            }

            @Override
            public float segLength(){
                return segmentLength;
            }

            @Override
            public float arc() {
                return arc;
            }
        });
        hitEffect.at(current.x, current.y, 0, color);
        if(!current.dead) collided.add(current.id);

        float effectiveRange = power/distanceDamageFalloff;

        final float newPower = newDamage * (pierceDamageFactor == 0 ? 1 : pierceDamageFactor);

        Time.run(15, () -> {
            Seq<Unit> units = Groups.unit.intersect(current.x - effectiveRange, current.y - effectiveRange, effectiveRange * 2, effectiveRange * 2);
            units.sort(u -> u.dst(current));
            if(units.contains(current)) units.remove(current);
            list.clear();
            for (int i = 0; i < Math.min(branches, units.size); i++) {
                Unit unit = units.get(i);
                if(collided.contains(unit.id)) continue;
                float dst = unit.dst(current);
                if(dst > effectiveRange) break;
                list.add(unit);
            }
            if(list.size == 0) return;
            float numberMultiplier = 1.0f/list.size;
            list.each(u -> {
                chain(current, u, collided, hitSound, hitEffect, newPower * numberMultiplier, initialPower, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, color);
            });
        });
    }
}
