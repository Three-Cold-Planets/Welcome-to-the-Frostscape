package frostscape.world.upgrades;

import mindustry.Vars;

public class Upgrade {
    public String name;

    public Upgrade(String name){
        UpgradeHandler.upgrades.add(this);
        this.name = Vars.content.transformName(name);
    }
}
