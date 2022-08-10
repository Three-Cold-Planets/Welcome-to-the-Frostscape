package frostscape.content;

import frostscape.type.upgrade.Upgrade;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class FrostUpgrades {
    public static Upgrade test1;

    public static void load(){
        test1 = new Upgrade("test1", ItemStack.with(Items.copper, 15));
    }
}
