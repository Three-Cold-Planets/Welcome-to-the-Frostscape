package main.entities.ability;

import arc.math.Mathf;
import mindustry.entities.abilities.ArmorPlateAbility;
import mindustry.gen.Unit;

public class MoveArmorAbility extends ArmorPlateAbility {
    public float minSpeed, maxSpeed;

    public MoveArmorAbility(float minSpeed, float maxSpeed, float healthMultiplier, float z){
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.healthMultiplier = healthMultiplier;
        this.z = z;
    };

    public void update(Unit unit) {
        float scl = Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        this.warmup = Mathf.lerpDelta(this.warmup, unit.isShooting() ? 1.0F : 0.0F, 0.1F) * scl;
        unit.healthMultiplier += warmup * healthMultiplier;
    }

    @Override
    public void draw(Unit unit) {

    }
}
