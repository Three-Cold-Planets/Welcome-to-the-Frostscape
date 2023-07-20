package main;

import arc.Events;
import main.content.*;
import main.game.SectorController;
import main.game.SectorControllers;
import mindustry.game.EventType.ClientLoadEvent;

import static main.game.ScriptedSectorHandler.controllers;

public class FrostContentLoader {
    public static void load(){
        FrostNotes.load();
        FrostResearch.load();
        Families.load();
        FrostStatusEffects.load();
        FrostItems.load();
        FrostLiquids.load();
        FrostBullets.load();
        FrostUpgrades.load();
        FrostUnits.load();
        FrostBlocks.load();
        SectorControllers.load();
        Events.run(ClientLoadEvent.class, () -> {
            controllers.each(SectorController::load);
            ImmunityHandler.handle();
        });
    }
}
