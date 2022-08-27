package frostscape.content;

import arc.graphics.Color;
import frostscape.world.meta.Family;

import frostscape.content.Palf;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;

public class Families {
    public static Family assault, hunter;

    public static void load(){
        assault = new Family("assault", Color.valueOf("ffa465"));
        assault.members.addAll(UnitTypes.dagger, UnitTypes.mace, UnitTypes.flare, UnitTypes.stell);
        hunter = new Family("hunter", Palf.hunter);
    }
}
