package frostscape.type.upgrade;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Log;
import arc.util.Strings;
import frostscape.Frostscape;
import frostscape.content.FrostResearch;
import frostscape.world.research.ResearchHandler;
import frostscape.world.upgrades.UpgradeHandler;
import frostscape.world.upgrades.UpgradeState;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;

import java.lang.reflect.Field;

public class Upgrade<T> {
    public String name, localisedName;
    public TextureRegion region;
    public Seq<UpgradeCondition> conditions = new Seq<UpgradeCondition>();
    //loaded from settings, used by the player to categorize upgrades.
    public String[] tags;
    //Research type that unlocks this upgrade
    public ResearchHandler.ResearchType unlockedBy;
    //If false, multipliers are left null
    public boolean usesDeltas = true;

    public Upgrade(String name){
        UpgradeHandler.upgrades.add(this);
        this.name = Vars.content.transformName(name);
    }

    public void load(){
        localisedName = Core.bundle.get(name, name);
        region = Core.atlas.find(name);
    }
    public boolean unlocked(Team team){
        return Frostscape.research.getData(team, unlockedBy).unlocked;
    }

    @Override
    public String toString() {
        return localisedName;
    }

    public interface UpgradeCondition{
        boolean valid();
    }

    public class UpgradeLockedCondition implements UpgradeCondition{
        public Seq<Upgrade> previous;
        @Override
        public boolean valid() {
            return previous.find(u -> !u.unlocked(Vars.player.team())) == null;
        }
    }
}
