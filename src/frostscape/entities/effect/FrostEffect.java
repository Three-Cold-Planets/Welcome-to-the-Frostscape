package frostscape.entities.effect;

import arc.func.Cons;
import arc.graphics.Color;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.Posc;

public class FrostEffect extends Effect {
    public FrostEffect(float life, float clipsize, Cons<EffectContainer> renderer) {
        super(life, clipsize, renderer);
    }

    public FrostEffect(float life, Cons<EffectContainer> renderer) {
        this(life, 50.0F, renderer);
    }

    public FrostEffect() {
        super();
    }

    public static FrostEffect convert(Effect effect){
        return new FrostEffect(){{
            renderer = effect.renderer;
            lifetime = effect.lifetime;
            clip = effect.clip;
            startDelay = effect.startDelay;
            followParent = effect.followParent;
            rotWithParent = effect.rotWithParent;
            layer = effect.layer;
            layerDuration = effect.layerDuration;
        }};
    }

    public EffectState state(float x, float y, float rotation, Color color, Object data) {
        EffectState entity = EffectState.create();
        entity.effect = this;
        entity.rotation = this.baseRotation + rotation;
        entity.data = data;
        entity.lifetime = this.lifetime;
        entity.set(x, y);
        entity.color.set(color);
        if (this.followParent && data instanceof Posc) {
            Posc p = (Posc)data;
            entity.parent = p;
            entity.rotWithParent = this.rotWithParent;
        }

        entity.add();
        return entity;
    }
}
