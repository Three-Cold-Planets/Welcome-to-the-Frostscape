package frostscape.type.upgrade;

import frostscape.Frostscape;
import frostscape.world.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.type.ItemStack;

public class Upgrade<T> {
    public String name;
    public float researchTime, baseInstallTime;
    public ItemStack[][] stackCosts;
    public int stacks;

    public Upgrade(String name, ItemStack[] cost){
        UpgradeHandler.upgrades.add(this);
        this.name = Vars.content.transformName(name);
        stackCosts = new ItemStack[][]{cost};
        stacks = 1;
    }

    public Upgrade(String name, ItemStack[][] costs){
        UpgradeHandler.upgrades.add(this);
        this.name = Vars.content.transformName(name);
        stackCosts = costs;
        stacks = costs.length;
    }


    public void unlock(Team team){
        Frostscape.upgrades.unlock(team, this);
    }

    public void update(T build){

    }
}
