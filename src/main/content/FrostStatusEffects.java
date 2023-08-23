package main.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import main.graphics.ModPal;
import main.type.status.FrostStatusEffect;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class FrostStatusEffects {
    public static StatusEffect[] spriteTests = new StatusEffect[5];
    public static StatusEffect napalm, causticCoating, attackBoost, engineBoost, lowGrav, conflex;

    public static void load(){
        for (int i = 0; i < spriteTests.length; i++) {
            spriteTests[i] = new StatusEffect("test-" + i){{}};
        }

        napalm = new StatusEffect("napalm") {{
            damage = 0.18f;
            transitionDamage = 80;
            speedMultiplier = 0.9f;
            effect = Fx.oily;
            color = ModPal.heat;
            opposite(StatusEffects.melting, StatusEffects.wet);
            init(() -> {
                affinity(StatusEffects.burning, (unit, result, time) -> unit.damagePierce(Math.min(transitionDamage, StatusEffects.burning.damage * time)));
                affinity(StatusEffects.tarred, (unit, result, time) -> result.set(napalm, Math.min(transitionDamage/damage, result.time + time)));
            });
            }

            @Override
            public void update(Unit unit, float time) {
                super.update(unit, time);
                unit.unapply(StatusEffects.burning);
            }
        };

        causticCoating = new FrostStatusEffect("caustic-coating"){{
            color = ModPal.sulphur;
            effect = Fxf.sulphurDrops;
            damage = 0.43f;
            damageMultiplier = 0.35f;
            speedMultiplier = 0.85f;
            dragMultiplier = 0.85f;
            shieldDamageMultiplier = 3;
        }};

        attackBoost = new StatusEffect("attack-boost"){{
            damageMultiplier = 1.35f;
        }};

        engineBoost = new StatusEffect("engine-boost"){{
            speedMultiplier = 2.45f;
            dragMultiplier = 0.75f;
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

        conflex = new StatusEffect("conflex"){

            public void update(Unit u, float time){
                super.update(u, time);
                u.dragMultiplier /= dragMultiplier;
                u.reloadMultiplier /= reloadMultiplier;
                //Grows by 1 per block the unit's hitbox's perimeter takes up.
                float maxMulti = u.hitSize/Vars.tilesize * 4;
                float multiplier = Interp.smooth2.apply(Mathf.clamp(time/(2 * 60)/maxMulti, 0, 1)) * maxMulti;
                u.dragMultiplier += multiplier * dragMultiplier;
                u.reloadMultiplier *= Mathf.clamp(1/multiplier, 0, 1f);

                Log.info(multiplier);

            };
            {
            dragMultiplier = 1.2f;
            reloadMultiplier = 0.65f;
            effect = Fx.colorSpark;
            color = ModPal.hunter;
        }};
    }
}