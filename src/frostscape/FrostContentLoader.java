package frostscape;

import arc.Events;
import arc.util.Log;
import frostscape.content.*;
import frostscape.game.SectorController;
import frostscape.game.SectorControllers;
import mindustry.game.EventType.ClientLoadEvent;

import static frostscape.game.ScriptedSectorHandler.controllers;

public class FrostContentLoader {
    public static void load(){
        Families.load();
        FrostItems.load();
        FrostBullets.load();
        FrostUpgrades.load();
        FrostStatusEffects.load();
        FrostUnits.load();
        FrostBlocks.load();
        SectorControllers.load();
        Events.run(ClientLoadEvent.class, () -> {
            controllers.each(SectorController::load);
        });
    }
}
