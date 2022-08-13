package frostscape.content;

import frostscape.type.upgrade.Upgrade;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class FrostUpgrades {
    public static Upgrade improvedWelding;

    public static void load(){
        improvedWelding = new Upgrade("welding1", ItemStack.with(FrostItems.magnetite, 15));
    }
}
