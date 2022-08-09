package frostscape.world.upgrades;

import frostscape.type.upgrade.Upgrade;
import mindustry.type.ItemStack;

public class UpgradeState {
    public ItemStack[] cost, items;
    public Upgrade upgrade;
    public float progress;
    public boolean installed = false, installing = false;
    
    public UpgradeState(Upgrade upgrade, ItemStack[] cost){
        this.upgrade = upgrade;
        this.cost = cost;
        items = ItemStack.mult(cost, 0);
    }

    public float progress(ProgressType type){
        float total = 0;
        switch (type){
            case PER_ITEM: {
                for (int i = 0; i < cost.length; i++) {
                    total = items[i].amount/cost[i].amount;
                }
                break;
            }
            case TOTAL: {
                float iitems = 0, citems = 0;
                for (int i = 0; i < cost.length; i++) {
                    iitems += items[i].amount;
                    citems += cost[i].amount;
                }
                total = iitems/citems;
            }
        }
        return total;
    }

    public enum ProgressType{
        TOTAL,
        PER_ITEM
    }
}

