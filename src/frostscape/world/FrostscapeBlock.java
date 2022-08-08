package frostscape.world;

import arc.struct.Seq;
import frostscape.world.upgrades.Upgrade;
import mindustry.gen.Sounds;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;

public class FrostscapeBlock extends Block {
    public FrostscapeBlock(String name) {
        super(name);
        this.update = true;
        this.solid = true;
        this.hasLiquids = true;
        this.liquidCapacity = 5.0F;
        this.hasItems = true;
    }

    public Seq<Upgrade> upgrades = new Seq<>();
}
