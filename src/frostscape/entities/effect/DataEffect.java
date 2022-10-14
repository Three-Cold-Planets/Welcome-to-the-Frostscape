package frostscape.entities.effect;

import arc.graphics.Color;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;

/**
    An effect which spawns its child effect along with itself, using the child's state as it's data
    @author Sh1penfire
 */

public class DataEffect extends FrostEffect {
    public FrostEffect child;

    public DataEffect() {
    }

    public DataEffect(FrostEffect child) {
        this.child = child;
    }

    public void create(float x, float y, float rotation, Color color, Object data) {
        if (this.shouldCreate()) {
            EffectState state = child.state(x, y, rotation, color, data);
            add(x, y, rotation, color, state);
        }
    }
}
