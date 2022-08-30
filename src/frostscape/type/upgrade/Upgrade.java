package frostscape.type.upgrade;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Log;
import arc.util.Strings;
import frostscape.Frostscape;
import frostscape.content.FrostResearch;
import frostscape.world.research.ResearchHandler;
import frostscape.world.upgrades.UpgradeHandler;
import frostscape.world.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;

public class Upgrade<T> {

    public String name;
    public float researchTime, baseInstallTime;
    public Seq<UpgradeCondition> conditions = new Seq<UpgradeCondition>();
    public ItemStack[][] stackCosts;
    public int stacks;
    //loaded from settings, used by the player to categorize upgrades.
    public String[] tags;
    //Research type that unlocks this upgrade
    public ResearchHandler.ResearchType unlockedBy;
    //If false, multipliers are left null
    public boolean usesDeltas = true;
    //Deltas applied when using an upgrade based on stack. (Copied from status effects)
    public float[]
            damageMultiplier,
            healthMultiplier,
            speedMultiplier,
            reloadMultiplier,
            rangeMultiplier,
            buildSpeedMultiplier;

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

    public void initialiseDeltas(){
        //Quick way to fill all empty arrays
        float[][] empty = new float[6][];
        if(healthMultiplier == null) empty[0] = healthMultiplier = new float[stacks + 1];
        if(damageMultiplier == null) empty[1] = damageMultiplier = new float[stacks + 1];
        if(reloadMultiplier == null) empty[2] = reloadMultiplier = new float[stacks + 1];
        if(rangeMultiplier == null) empty[3] = rangeMultiplier = new float[stacks + 1];
        if(speedMultiplier == null) empty[4] = speedMultiplier = new float[stacks + 1];
        if(buildSpeedMultiplier == null) empty[5] = buildSpeedMultiplier = new float[stacks + 1];

        for (int i = 0; i < empty.length; i++) {
            if(empty[i] != null) for (int j = 0; j < empty[i].length; j++) {
                empty[i][j] = 1;
            }
        }
    };

    public boolean unlocked(Team team){
        return Frostscape.research.getData(team, unlockedBy).unlocked;
    }

    //Generates an array of floats used in upgrade stats. PUT THE INDEXES IN ORDER
    public static float[] genStats(int size, float base, int[] indexes, float[] values){
        float[] returnArr = new float[size];
        float current = base;
        int index = 0;
        for (int i = 0; i < size; i++) {
            Log.info(index);
            Log.info(index < indexes.length);
            if(index < indexes.length && indexes[index] == i) {
                current = values[index];
                index++;
            }
            returnArr[i] = current;
        }
        return returnArr;
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
