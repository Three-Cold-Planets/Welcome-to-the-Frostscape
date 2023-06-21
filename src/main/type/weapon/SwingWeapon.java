package main.type.weapon;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import main.util.VelUtils;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;

/**A weapon which swings to the side its owner is moving*/
public class SwingWeapon extends BaseWeapon {
    public float from,
            to,
            threshold,
            target,
            targetingBounds;
    /**Whether to swing clockwise or not*/
    public boolean rotateClockwise;

    public SwingWeapon(String name){
        super(name);
        shootCone = 360;
    }

    @Override
    public void updateRotation(Unit unit, WeaponMount mount) {
        float diff = VelUtils.direction(unit.vel, unit.rotation);
        float activation = Mathf.lerp(from, to, Mathf.clamp(Mathf.maxZero(unit.vel().len2() - threshold) / target, 0, 1)) * diff;

        //rotate if applicable
        if(rotate && can && (mount.rotate || mount.shoot)){
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                    axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

            float targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            if(mount.shoot && Angles.within(targetRotation, baseRotation, targetingBounds)){
                mount.targetRotation = targetRotation;
                mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
            }
            else mount.rotation = Angles.moveToward(mount.rotation, baseRotation + rotationLimit * (-0.5f + activation) * Mathf.sign(flipSprite) * Mathf.sign(rotateClockwise), rotateSpeed);
            if(rotationLimit < 360){
                float dst = Angles.angleDist(mount.rotation, baseRotation);
                if(dst > rotationLimit/2f){
                    mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit/2f);
                }
            }
        }else if(!rotate){
            mount.rotation = baseRotation;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }
    }

    //Don't touch region part turret shading
    @Override
    public void load() {
        region = Core.atlas.find(name);
        heatRegion = Core.atlas.find(name + "-heat");
        cellRegion = Core.atlas.find(name + "-cell");
        outlineRegion = Core.atlas.find(name + "-outline");

        for(var part : parts){
            part.load(name);
        }
    }
}
