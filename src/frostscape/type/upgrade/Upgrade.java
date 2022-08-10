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
        super();
        stackCosts = new ItemStack[][]{cost};
        stacks = 1;
    }

    public Upgrade(String name, ItemStack[][] costs){
        super();
        stackCosts = costs;
        stacks = costs.length;
    }

    public Upgrade(String name){
        UpgradeHandler.upgrades.add(this);
        this.name = Vars.content.transformName(name);
    }

    public void unlock(Team team){
        Frostscape.upgrades.unlock(team, this);
    }

    public void update(T build){

    }
}
