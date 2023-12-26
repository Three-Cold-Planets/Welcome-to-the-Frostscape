package main.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
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

    //Internal!

    public static StatusEffect conflexInternal;

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
            opposite(StatusEffects.freezing, StatusEffects.wet);
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

        conflex = new FrostStatusEffect("conflex"){

            public void update(Unit u, float time){
                super.update(u, time);
                u.speedMultiplier /= speedMultiplier;
                u.dragMultiplier /= dragMultiplier;
                u.reloadMultiplier /= reloadMultiplier;
                float multiplier = Mathf.clamp(time/(5 * 60), 0, 1);
                u.dragMultiplier *= multiplier * dragMultiplier;
                u.reloadMultiplier *= Mathf.clamp(1-multiplier, 0, 1f);
                u.speedMultiplier *= Mathf.lerp(1, speedMultiplier, multiplier);
            };

            {
            speedMultiplier = 0.2f;
            dragMultiplier = 1.2f;
            reloadMultiplier = 0.65f;
            transitionDamage = 0.01f;
            effect = Fx.regenSuppressParticle;
            applyEffect = Fx.regenSuppressParticle;
            color = ModPal.hunter;
            applyColor = ModPal.hunter;
        }};

        //Used internally to make conflex stack. As of current, units always call applied if a status already exists on a unit.
        //Stackingg gets weaker logarithmically
        conflexInternal = new FrostStatusEffect("conflex-internal"){
            public void applied(Unit unit, float time, boolean extend) {
                //Grows by 1 per block the unit's hitbox's perimeter takes up.
                float current = unit.getDuration(conflex);
                float scale = Mathf.pow(unit.hitSize/Vars.tilesize, 2);
                unit.apply(conflex, Mathf.clamp(current+(current == 0 ? time : time/(Mathf.log(current+2, time/current+1) + 1))/scale,0,60 * 5));
            }
            {
            show = false;
        }};
    }
}
