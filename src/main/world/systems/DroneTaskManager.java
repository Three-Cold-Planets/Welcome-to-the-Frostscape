package main.world.systems;

import arc.struct.Seq;
import main.entities.unit.DroneUnit;
import main.type.upgrade.Upgradeable;

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
