package frostscape.entities.ability;
import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

import static frostscape.content.FrostBullets.placeholder1;

public class MoveDamageLineAbility extends Ability {
    /** Bullet damage */
    public float damage = 35f;
    /** Chance of firing every tick. Set >= 1 to always fire lightning every tick at max speed */
    public float chance = 0.15f;
    /** Length of the lightning. <= 0 to disable */
    public int length = 12;
    /** Speeds for when to start lightninging and when to stop getting faster */
    public float minSpeed = 0.8f, maxSpeed = 1.2f;
    /** Shifts where the lightning spawns along the Y axis */
    public float y = 0f;
    /** Offset along the X axis */
    public float x = 0f;
    /** Whether the spawn side alternates */
    public boolean alternate = false;
    /** Bullet angle parameters */
    public float angleOffset = 0f;
    /** Effect spawned upon bullet creation **/
    public Effect effect;

    protected float side = 1f;

    MoveDamageLineAbility(){}

    public MoveDamageLineAbility(float damage, int length, float chance, float y, float minSpeed, float maxSpeed,  float angleOffset, Effect effect){
        this.damage = damage;
        this.length = length;
        this.chance = chance;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.angleOffset = angleOffset;
        this.effect = effect;
    }

    @Override
    public void update(Unit unit){
        float scl = Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        if(Mathf.chance(Time.delta * chance * scl * Time.delta)){
            float x = unit.x + Angles.trnsx(unit.rotation, this.y, this.x * side), y = unit.y + Angles.trnsy(unit.rotation, this.y, this.x * side);

            if(length > 0){
                placeholder1.damage = damage;
                placeholder1.length = length;
                placeholder1.create(unit, x, y, unit.rotation + angleOffset);
                effect.at(x, y, unit.rotation + angleOffset);
            }

            if(alternate) side *= -1f;
        }
    }
}
