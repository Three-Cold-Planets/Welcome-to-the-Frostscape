package main.content;

import main.world.meta.LoreNote;

public class FrostNotes {

    public static LoreNote
        thankYou;
    public static void load(){
        thankYou = new LoreNote("thank-you", "icon"){{
            alwaysUnlocked = true;
        }};
    }
}
