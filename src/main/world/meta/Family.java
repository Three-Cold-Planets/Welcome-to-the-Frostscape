package main.world.meta;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.*;
import mindustry.Vars;
import mindustry.ctype.*;

/** Describes a group of UnlockableContent and related metadata**/
public class Family {

    public static Seq<Family> all = new Seq<Family>();

    public Seq<UnlockableContent> members = new Seq<>();
    private static int nextFree = 0;

    public int id;

    public Color color;
    public String name, localizedName, description, details;
    public TextureRegion icon, flag;

    public Family(String name, Color color){
        this.name = Vars.content.transformName(name);
        this.color = color;

        id = nextFree;
        nextFree++;

        all.add(this);
    }

    public void load(){
        localizedName = Core.bundle.get("family." + name + ".name", name);
        description = Core.bundle.get("family." + name + ".description", "[grey]<No description provided>");
        details = Core.bundle.get("family." + name + ".details", "[grey]<No details provided>");

        icon = Core.atlas.find(name);
        flag = Core.atlas.find(name + "-flag");
    }

    @Override
    public String toString(){
        return "Family#" + id;
    }
}
