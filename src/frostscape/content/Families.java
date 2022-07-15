package frostscape.content;

import arc.graphics.Color;
import frostscape.world.meta.Family;

import frostscape.content.Palf;
import mindustry.content.Fx;

public class Families {
    public static Family assault, hunter;

    public static void load(){
        assault = new Family("assault", Color.valueOf("ffa465"));
        hunter = new Family("hunter", Palf.hunter);
    }
}
