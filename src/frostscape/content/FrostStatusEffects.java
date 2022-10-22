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
    public static StatusEffect napalm, causticCoating, attackBoost, engineBoost, lowGrav;

    public static void load(){
        for (int i = 0; i < spriteTests.length; i++) {
            spriteTests[i] = new StatusEffect("test-" + i){{}};
        }

        napalm = new StatusEffect("sticky-fire"){{
            damage = 0.18f;
            transitionDamage = 80;
            speedMultiplier = 0.9f;
            effect = Fx.oily;
            color = Palf.heat;
            opposite(StatusEffects.melting, StatusEffects.wet);
            init(() -> {
                affinity(StatusEffects.burning, (unit, result, time) -> unit.damagePierce(Math.min(transitionDamage, StatusEffects.burning.damage * time)));
                affinity(StatusEffects.tarred, (unit, result, time) -> result.set(napalm, Math.min(transitionDamage/damage, result.time + time)));
            });
        }};

        causticCoating = new StatusEffect("caustic-coating"){{
            color = Palf.sulphur;
            effect = Fxf.sulphurDrops;
            damage = 0.43f;
            damageMultiplier = 0.35f;
            speedMultiplier = 0.85f;
            dragMultiplier =  0.85f;
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
