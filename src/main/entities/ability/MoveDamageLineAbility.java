package main.entities.ability;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

import static main.content.FrostBullets.placeholder1;

public class MoveDamageLineAbility extends Ability {
    public Color color = Color.valueOf("d1efff");
    public String heatRegion = "";

    /** Bullet damage */
    public float damage = 35f;
    /** Chance of firing every tick. Set >= 1 to always run every tick at max speed */
    public float chance = 0.15f;
    /** Length of the lightning. <= 0 to disable */
    public int length = 12;
    /** Speeds for when to start checking and when to stop getting faster */
    public float minSpeed = 0.8f, maxSpeed = 1.2f;
    /** Shifts where the lightning spawns along the Y axis */
    public float y = 0f;
    /** Offset along the X axis */
    public float x = 0f;

    /** Recoil when hitting things. **/
    public float recoil = 0;

    /** Whether the spawn side alternates */
    public boolean alternate = false;
    /** Whether ground/air are affected units */
    public boolean hitGround, hitAir;
    /** Bullet angle parameters */
    public float angleOffset = 0f;
    /** Effect spawned upon bullet creation **/
    public Effect effect,
    /** Effect for hitting things. Self explanitory **/
    hitEffect;

    protected float side = 1f;
    protected float scl;

    MoveDamageLineAbility(){}

    public MoveDamageLineAbility(float damage, int length, float chance, float x, float y, float minSpeed, float maxSpeed, float angleOffset, boolean hitGround, boolean hitAir, Effect effect, Effect hitEffect){
        this.damage = damage;
        this.length = length;
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.angleOffset = angleOffset;
        this.hitGround = hitGround;
        this.hitAir = hitAir;
        this.effect = effect;
        this.hitEffect = hitEffect;
    }
    public MoveDamageLineAbility(float damage, int length, float chance, float x, float y, float minSpeed, float maxSpeed, float angleOffset, float recoil, boolean hitGround, boolean hitAir, Effect effect, Effect hitEffect){
        this.damage = damage;
        this.length = length;
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.angleOffset = angleOffset;
        this.recoil = recoil;
        this.hitGround = hitGround;
        this.hitAir = hitAir;
        this.effect = effect;
        this.hitEffect = hitEffect;
    }

    public MoveDamageLineAbility(float damage, int length, float chance, float x, float y, float minSpeed, float maxSpeed, float angleOffset, float recoil, boolean hitGround, boolean hitAir, Effect effect, Effect hitEffect, String heatRegion) {
        this.damage = damage;
        this.length = length;
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.angleOffset = angleOffset;
        this.recoil = recoil;
        this.hitGround = hitGround;
        this.hitAir = hitAir;
        this.effect = effect;
        this.hitEffect = hitEffect;
        this.heatRegion = heatRegion;
    }

    @Override
    public void update(Unit unit){
        scl = Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        if(chance > 1 || Mathf.chance(Time.delta * chance * scl)){
            float x = unit.x + Angles.trnsx(unit.rotation, this.y, this.x * side), y = unit.y + Angles.trnsy(unit.rotation, this.y, this.x * side);

            if(length > 0){
                placeholder1.damage = damage;
                placeholder1.length = length;
                placeholder1.collidesGround = hitGround;
                placeholder1.collidesAir = hitAir;
                placeholder1.hitEffect = hitEffect;
                Bullet b = placeholder1.create(unit, x, y, unit.rotation + angleOffset);
                if(b.collided().size > 0 && recoil > 0) unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, recoil * chance * scl));
                effect.at(x, y, unit.rotation + angleOffset);
            }

            if(alternate) side *= -1f;
        }
    }

    @Override
    public void draw(Unit unit) {
        TextureRegion region = Core.atlas.find(this.heatRegion);
        if (Core.atlas.isFound(region) && scl > 1.0E-5F) {
            Draw.color(this.color);
            Draw.alpha(scl / 2.0F);
            Draw.blend(Blending.additive);
            Draw.rect(region, unit.x + Mathf.range(scl / 2.0F), unit.y + Mathf.range(scl / 2.0F), unit.rotation - 90.0F);
            Draw.blend();
        }
    }
}
