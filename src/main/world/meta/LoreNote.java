package main.world.meta;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.Vars;

public class LoreNote {

    public static Seq<LoreNote> all = new Seq<LoreNote>();
    private static int nextFree = 0;

    private boolean unlocked;
    public boolean seen, alwaysUnlocked;

    public int id;

    public Color color;
    public String name, iconName, localizedName, description, details;
    public TextureRegion icon;

    public LoreNote(String name, String iconName){
        this.name = Vars.content.transformName(name);
        this.iconName = Vars.content.transformName(iconName);
        this.unlocked = Core.settings.getBool(name + "-unlocked");
        this.seen = Core.settings.getBool(name + "-seen");

        id = nextFree;
        nextFree++;

        all.add(this);
    }

    public void unlock(){
        unlocked = true;
        Core.settings.put(name + "-unlocked", true);
        Vars.ui.showInfoToast(Core.bundle.get("toast.notes"), 5);
    }

    public boolean unlocked(){
        return unlocked || alwaysUnlocked;
    }

    public void unlockTmp(){
        unlocked = true;
        Core.settings.put(name + "-unlocked", true);
    }

    public void load(){
        localizedName = Core.bundle.get("notes." + name + ".name", name);
        description = Core.bundle.get("notes." + name + ".description", "[grey]<No description provided>");
        details = Core.bundle.get("notes." + name + ".details", "[grey]<No details provided>");

        icon = Core.atlas.find(iconName);
    }

    @Override
    public String toString(){
        return "LoreNote#" + id;
    }
}