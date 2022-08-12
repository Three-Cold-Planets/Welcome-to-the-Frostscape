package frostscape.world.upgrades;

import arc.struct.Seq;
import frostscape.entities.unit.DroneUnit;
import frostscape.type.upgrade.UpgradeableBuild;
import frostscape.world.FrostscapeBuilding;

public class DroneTaskManager {
    public static Seq<DroneUnit> drones = new Seq<>();
    public static Seq<UpgradeableBuild> builds = new Seq<>();

    public static float getCost(DroneUnit unit){
        return 1;
    }

    public void reset(){
        drones.clear();
        builds.clear();
    }
}
