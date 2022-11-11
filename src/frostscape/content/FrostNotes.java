package frostscape.content;

import frostscape.world.meta.LoreNote;

import static frostscape.Frostscape.NAME;

public class FrostNotes {

    public static LoreNote
        thankYou;
    public static void load(){
        thankYou = new LoreNote("thank-you", "icon"){{
            alwaysUnlocked = true;
        }};
    }
}
