package main.game;

import arc.util.Nullable;
import mindustry.type.Planet;
import mindustry.type.SectorPreset;

import static main.game.ScriptedSectorHandler.scriptedSectors;

public class ScriptedSector extends SectorPreset {

    //default controller of the scripted sector
    public @Nullable
    SectorController defaultController;

    public ScriptedSector(String name, Planet planet, int sector) {
        super(name, planet, sector);
        scriptedSectors.add(this);
    }
}
