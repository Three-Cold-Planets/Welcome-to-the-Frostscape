package main.world.systems.upgrades;

import arc.func.Cons;
import main.type.upgrade.Upgradeable;
import mindustry.type.ItemStack;

import java.util.Arrays;

//Used to store information about a type's upgrades
public class UpgradeEntry {
    //Set to -1 to disable
    public float researchTime, baseInstallTime;

    //If this is set to true, then the upgrade never moves past its first level after completion.
    public boolean repeatable = false;

    //Listener with params of the thing being upgraded. Useful with repeatable upgrades.
    public Cons<Upgradeable> listener = null;

    public UpgradeEntry(Upgrade upgrade){
        this.upgrade = upgrade;
    }

    public Upgrade upgrade;
    public ItemStack[][] costs;
    //Deltas applied when using an upgrade based on stack. (Copied from status effects)
    public float[]
            damageMultiplier,
            healthMultiplier,
            armorMultiplier,
            speedMultiplier,
            reloadMultiplier,
            rangeMultiplier,
            buildSpeedMultiplier;

    float[] one(int size){
        float[] out = new float[size];
        for (int i = 0; i < size; i++) {
            out[i] = 1;
        }
        return out;
    }
    public void initialiseDeltas(){
        int levels = stacks();
        int size = levels - 1;

        //Initializing an array fills all elements with zeros, I need them to default to ones
        if(damageMultiplier == null) damageMultiplier = one(levels);
        if(healthMultiplier == null) healthMultiplier = one(levels);
        if(armorMultiplier == null) armorMultiplier = one(levels);
        if(speedMultiplier == null) speedMultiplier = one(levels);
        if(reloadMultiplier == null) reloadMultiplier = one(levels);
        if(rangeMultiplier == null) rangeMultiplier = one(levels);
        if(buildSpeedMultiplier == null) buildSpeedMultiplier = one(levels);
        float[][] all = new float[][]{damageMultiplier, healthMultiplier, armorMultiplier, reloadMultiplier, rangeMultiplier, speedMultiplier, buildSpeedMultiplier};

        for (int i = 0; i < all.length; i++) {
            float[] list = all[i];
            int length = all[i].length;
            if(length < levels) {
                all[i] = Arrays.copyOf(list, levels);
                float lastValue = list[list.length - 1];
                for (int j = length - 1; j < stacks(); j++){
                    all[i][j] = lastValue;
                }
            }
        }

        damageMultiplier = all[0];
        healthMultiplier = all[1];
        armorMultiplier = all[2];
        speedMultiplier = all[3];
        reloadMultiplier = all[4];
        rangeMultiplier = all[5];
        buildSpeedMultiplier = all[6];
    }

    public int stacks(){
        return costs.length;
    }
}
