package frostscape.world.upgrades;

import arc.struct.Seq;
import frostscape.entities.unit.DroneUnit;
import frostscape.type.upgrade.Upgradeable;

public class DroneTaskManager {
    public static Seq<DroneUnit> drones = new Seq<>();
    public static Seq<Upgradeable> builds = new Seq<>();

    public static float getCost(DroneUnit unit){
        return 1;
    }

    public void reset(){
        drones.clear();
        builds.clear();
    }
}
