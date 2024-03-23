package main.ai.types;

import arc.math.Angles;
import mindustry.ai.types.FlyingAI;

public class ChargeFlyingAI extends FlyingAI {
    @Override
    public void circleAttack(float circleLength) {
        vec.set(target).sub(unit);
        float angle = Angles.angleDist(vec.angle(), unit.vel.angle());

        if(vec.len() < circleLength){
            vec.setLength(unit.vel.len() * unit.range()/circleLength);
            if(unit.type.faceTarget) {
                unit.rotateMove(vec);
            }
            else unit.moveAt(vec);
        }
        else if(angle > 15){
            unit.lookAt(target);
        }
    }
}
