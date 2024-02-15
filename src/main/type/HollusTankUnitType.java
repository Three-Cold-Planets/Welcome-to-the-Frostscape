package main.type;

import mindustry.world.meta.Env;

public class HollusTankUnitType extends HollusUnitType{
    public HollusTankUnitType(String name) {
        super(name);
        squareShape = true;
        omniMovement = false;
        rotateMoveFirst = true;
        rotateSpeed = 2.3f;
        envDisabled = Env.none;
        speed = 0.8f;
    }
}
