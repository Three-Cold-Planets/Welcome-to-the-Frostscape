package main.content;

import main.world.systems.research.ResearchHandler.ResearchType;

public class FrostResearch {
    public static ResearchType improvedWelding, shrunkenBoilers;
    public static void load(){
        improvedWelding = new ResearchType("improved-welding", 160);
    }
}