package frostscape.content;

import mindustry.Vars;
import mindustry.content.StatusEffects;

public class ImmunityHandler {
    public static void handle(){
        Vars.content.units().each(u -> {
            if(u.immunities.contains(StatusEffects.burning)) u.immunities.add(FrostStatusEffects.napalm);
        });
    }
}
