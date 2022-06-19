package frostscape;

import frostscape.content.FrostBlocks;
import frostscape.content.FrostBullets;

public class FrostContentLoader {
    public static void load(){
        FrostBullets.load();
        FrostBlocks.load();
    }
}
