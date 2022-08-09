package frostscape.type.upgrade;

import frostscape.world.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.type.ItemStack;

public class Upgrade<T> {
    public String name;
    public float researchTime, baseInstallTime;
    public ItemStack[][] stackCosts;

    public Upgrade(String name, ItemStack[] cost){
        UpgradeHandler.upgrades.add(this);
        this.name = Vars.content.transformName(name);
        stackCosts = new ItemStack[][]{cost};
    }

    public void update(T build){

    }
}
