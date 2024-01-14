package main.entities.unit;

import mindustry.gen.UnitEntity;
import mindustry.gen.Unitc;

public class DroneUnit extends UnitEntity {

    public Unitc parent;

    @Override
    public void update() {
        super.update();
        if(parent.dead()) kill();
    }
}
