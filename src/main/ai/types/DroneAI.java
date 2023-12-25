package main.ai.types;

import main.entities.unit.DroneUnit;
import mindustry.ai.types.FlyingAI;

public class DroneAI extends FlyingAI {
    public DroneUnit drone;

    @Override
    public void init() {
        super.init();
        if(unit instanceof DroneUnit drone) this.drone = drone;
    }
}
