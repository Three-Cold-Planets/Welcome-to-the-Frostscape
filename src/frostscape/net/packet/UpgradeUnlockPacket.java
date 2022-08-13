package frostscape.net.packet;

import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.Frostscape;
import frostscape.type.upgrade.Upgrade;
import frostscape.world.upgrades.UpgradeHandler;
import mindustry.game.Team;
import mindustry.gen.Itemsc;
import mindustry.io.TypeIO;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

public class UpgradeUnlockPacket extends Packet {
    int upgradeId, teamId;

    public UpgradeUnlockPacket() {
    }

    public void read(Reads read) {
        this.upgradeId = read.i();
        this.teamId = read.i();
    }

    public void write(Writes write) {
        write.i(upgradeId);
        write.i(teamId);
    }

    public void read(Reads read, int length) {
        this.read(read);
    }

    public void handled() {
        this.upgradeId = READ.i();
        this.teamId = READ.i();
    }

    public int getPriority() {
        return 1;
    }

    public void handleClient() {
        Frostscape.upgrades.unlock(Team.get(teamId), UpgradeHandler.upgrades.get(upgradeId));
    }

    public void handleServer(NetConnection con) {
    }

}
