package frostscape.game;

import frostscape.game.controller.FartController;

public class SectorControllers {
    public static FartController fart;
    public static void load(){
        fart = new FartController("fart");
    }
}
