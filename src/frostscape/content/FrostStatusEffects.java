package frostscape.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.*;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class FrostStatusEffects {
    public static StatusEffect[] spriteTests = new StatusEffect[5];
    public static StatusEffect napalm, attackBoost, engineBoost, lowGrav;

    public static void load(){
        for (int i = 0; i < spriteTests.length; i++) {
            spriteTests[i] = new StatusEffect("test-" + i){{}};
        }

        napalm = new StatusEffect("sticky-fire"){{
            damage = 0.15f;
            speedMultiplier = 0.6f;
            effect = Fx.oily;
            opposite(StatusEffects.melting);
            init(() -> {
                affinity(StatusEffects.melting, (unit, result, time) -> result.set(StatusEffects.melting, result.time + time));
                affinity(StatusEffects.burning, (unit, result, time) -> result.set(tarred, result.time + time));
                affinity(tarred , (unit, result, time) -> result.set(napalm, result.time + time));
            });
        }};

        attackBoost = new StatusEffect("attack-boost"){{
            damageMultiplier = 1.35f;
        }};

        engineBoost = new StatusEffect("engine-boost"){{
            speedMultiplier = 2.45f;
            dragMultiplier = 0.5f;
        }};

        lowGrav = new StatusEffect("low-grav"){
            @Override
            public void draw(Unit unit) {
                super.draw(unit);
                Lines.stroke(unit.elevation);
                Draw.color(Pal.berylShot);
                Lines.circle(unit.x, unit.y, unit.type.hitSize + Mathf.sin(Time.time/10, 1,9));
                Draw.color(Pal.berylShot);
                Lines.circle(unit.x, unit.y, unit.type.hitSize + Mathf.sin(Time.time/15, 1,6));
            }

            public void update(Unit u, float time){
                super.update(u, time);
                if(u.elevation > 0.1f) u.elevation = Mathf.clamp(u.elevation + (u.type.flying ? 0 : u.type.fallSpeed/100 * 99), 0, 1);
            };
            {
                effect = Fx.fallSmoke;
            }
        };
    }
}
