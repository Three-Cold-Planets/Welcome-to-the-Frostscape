package frostscape.type.upgrade;

import arc.struct.Seq;
import frostscape.Frostscape;
import frostscape.world.upgrades.UpgradeHandler;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.type.ItemStack;

public class Upgrade<T> {
    public String name;
    public float researchTime, baseInstallTime;
    public Seq<UpgradeCondition> conditions = new Seq<UpgradeCondition>();
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

    public boolean unlocked(Team team){
        return Frostscape.upgrades.getData(team, this).state > 0;
    }

    public void unlock(Team team){
        Frostscape.upgrades.unlock(team, this);
    }

    public void update(T build){

    }

    public interface UpgradeCondition{
        boolean valid();
    }

    public class UpgradeLockedCondition implements UpgradeCondition{
        public Seq<Upgrade> previous;
        @Override
        public boolean valid() {
            return previous.find(u -> !u.unlocked(Vars.player.team())) == null;
        }
    }
}
