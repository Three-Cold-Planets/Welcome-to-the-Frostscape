package main.net.packet;

import arc.util.io.Reads;
import arc.util.io.Writes;
import main.world.systems.research.ResearchHandler;
import mindustry.game.Team;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

public class ResearchUnlockPacket extends Packet {
    int researchId, teamId;

    public ResearchUnlockPacket() {
    }

    public void read(Reads read) {
        this.researchId = read.i();
        this.teamId = read.i();
    }

    public void write(Writes write) {
        write.i(researchId);
        write.i(teamId);
    }

    public void read(Reads read, int length) {
        this.read(read);
    }

    public void handled() {
        this.researchId = READ.i();
        this.teamId = READ.i();
    }

    public int getPriority() {
        return 1;
    }

    public void handleClient() {
        ResearchHandler.get().unlock(Team.get(teamId), ResearchHandler.types.get(researchId));
    }

    public void handleServer(NetConnection con) {
    }

}
