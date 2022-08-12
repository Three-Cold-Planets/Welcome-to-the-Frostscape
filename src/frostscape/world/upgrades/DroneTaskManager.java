package frostscape.world.upgrades;

import arc.struct.Seq;
import frostscape.entities.unit.DroneUnit;

public class DroneTaskManager {
    public static Seq<DroneUnit> drones = new Seq<>();

    public static float getCost(DroneUnit unit){
        return 1;
    }
}
