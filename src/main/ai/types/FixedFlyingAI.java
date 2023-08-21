package main.ai.types;

import arc.math.Angles;
import mindustry.ai.types.FlyingAI;

public class FixedFlyingAI extends FlyingAI {
    @Override
    public void circleAttack(float circleLength) {
        vec.set(target).sub(unit);
        float ang = unit.angleTo(target);
        float diff = Angles.angleDist(ang, unit.rotation());
        if (diff > 70.0f && vec.len() < circleLength) {
            vec.setAngle(unit.vel().angle());
        } else {
            vec.setAngle(Angles.moveToward(unit.vel().angle(), vec.angle(), 6.0F));
        }

        vec.setLength(this.unit.speed());
        if(unit.type.faceTarget) unit.rotateMove(vec);
        else unit.moveAt(vec);
    }
}
