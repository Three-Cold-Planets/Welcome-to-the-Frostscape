package main.game;

import main.game.controller.FartController;

public class SectorControllers {
    public static FartController fart;
    public static void load(){
        fart = new FartController("fart");
    }
}
