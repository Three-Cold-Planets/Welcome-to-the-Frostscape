package frostscape.content;

import frostscape.Frostscape;
import frostscape.world.research.ResearchHandler.ResearchType;

public class FrostResearch {
    public static ResearchType improvedWelding, shrunkenBoilers;
    public static void load(){
        improvedWelding = new ResearchType("improved-welding", 160);
    }
}