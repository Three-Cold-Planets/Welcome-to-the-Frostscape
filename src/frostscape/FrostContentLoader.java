package frostscape;

import frostscape.content.*;

public class FrostContentLoader {
    public static void load(){
        Families.load();
        FrostBullets.load();
        FrostBlocks.load();
        FrostStatusEffects.load();
        FrostUnits.load();
    }
}
