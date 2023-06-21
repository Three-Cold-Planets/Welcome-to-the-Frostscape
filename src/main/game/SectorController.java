package main.game;

import arc.Core;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;

import static main.game.ScriptedSectorHandler.controllers;

public abstract class SectorController {
    public final String name;
    //Used in the editor
    public String localizedName;

    public SectorController(String name){
        controllers.add(this);
        this.name = Vars.content.transformName(name);
    }

    public void load(){
        localizedName = Core.bundle.get("controller." + name + ".name", name);
    }

    //Resets the controller
    public abstract void reset();
    //Triggers when the game is not paused, and is playing
    public abstract void update();
    //Triggers when the game is playing
    public abstract void draw();
    //Triggered when a wave is sent
    public abstract void wave();

    //Method used to save data.
    public abstract void read(Reads r);
    //Method used to load data
    public abstract void write(Writes w);

    @Override
    public String toString() {
        return name;
    }
}
