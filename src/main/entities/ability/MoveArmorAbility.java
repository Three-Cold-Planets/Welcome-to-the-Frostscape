package main.entities.ability;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class MoveArmorAbility extends Ability {
    public Color color = Color.valueOf("d1efff");
    public float minSpeed, maxSpeed, healthMultiplier;
    public boolean requiresShoot;

    public String heatRegion;
    public float z = 110.0F;

    //Reminder that abilities are cloned inside units
    protected float scl;
    protected float warmup;

    public MoveArmorAbility(float minSpeed, float maxSpeed, float healthMultiplier, boolean requiresShoot, String heatRegion, float z){
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.healthMultiplier = healthMultiplier;
        this.requiresShoot = requiresShoot;
        this.heatRegion = heatRegion;
        this.z = z;
    };

    public void update(Unit unit) {
        scl = Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        this.warmup = Mathf.lerpDelta(this.warmup, (unit.isShooting() || !requiresShoot) ? 1.0F : 0.0F, 0.1F) * scl;
        unit.healthMultiplier += warmup * healthMultiplier;
    }

    @Override
    public void draw(Unit unit) {
        TextureRegion region = Core.atlas.find(this.heatRegion);
        if (Core.atlas.isFound(region) && warmup > 1.0E-5F) {
            Draw.color(this.color);
            Draw.alpha(warmup / 2.0F);
            Draw.blend(Blending.additive);
            Draw.rect(region, unit.x + Mathf.range(warmup / 2.0F), unit.y + Mathf.range(warmup / 2.0F), unit.rotation - 90.0F);
            Draw.blend();
        }
    }
}
