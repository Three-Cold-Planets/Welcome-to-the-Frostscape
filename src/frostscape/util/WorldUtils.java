package frostscape.util;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.gen.Building;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;

public class WorldUtils {

    public static Vec2 vec = new Vec2();
    public static Building tmpBuilding = null;

    public static Hitboxc linecast(float x, float y, float angle, float length, boolean ground){
        vec.trns(angle, length);
        tmpBuilding = null;
        if (ground) {
            World.raycastEachWorld(x, y, x + vec.x, y + vec.y, (cx, cy) -> {
                Building tile = Vars.world.build(cx, cy);
                if (tile != null) {
                    tmpBuilding = tile;
                    return true;
                } else {
                    return false;
                }
            });
        }
        return null;
    }
}
