package frostscape.content;

import frostscape.world.meta.Family;

import static frostscape.content.Palf.hunterColor;

public class Families {
    public static Family hunter;

    public static void load(){
        hunter = new Family("hunter", hunterColor);
    }
}
