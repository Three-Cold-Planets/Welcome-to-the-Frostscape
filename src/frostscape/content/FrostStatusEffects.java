package frostscape.content;

import mindustry.type.StatusEffect;

public class FrostStatusEffects {
    public static StatusEffect[] spriteTests = new StatusEffect[5];
    public static StatusEffect attackBoost, engineBoost;

    public static void load(){
        for (int i = 0; i < spriteTests.length; i++) {
            spriteTests[i] = new StatusEffect("test-" + i){{}};
        }

        attackBoost = new StatusEffect("attack-boost"){{
            damageMultiplier = 1.35f;
        }};

        engineBoost = new StatusEffect("engine-boost"){{
            speedMultiplier = 1.45f;
        }};
    }
}
