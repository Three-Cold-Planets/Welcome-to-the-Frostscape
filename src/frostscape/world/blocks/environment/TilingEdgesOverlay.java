package frostscape.world.blocks.environment;

import arc.graphics.g2d.TextureRegion;
import mindustry.world.blocks.environment.Floor;

public class TilingEdgesOverlay extends Floor {
    public TilingEdgesOverlay(String name) {
        super(name);
    }

    public TextureRegion edgeRegion, cornerRegion, innerCornerRegion;
}
