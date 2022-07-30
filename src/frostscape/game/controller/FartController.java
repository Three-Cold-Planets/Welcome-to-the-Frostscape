package frostscape.game.controller;

import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.game.SectorController;
import mindustry.content.Fx;
import mindustry.gen.Groups;

public class FartController extends SectorController {

    float fartTimer = 0;
    float maxTime = 60;

    public FartController(String name) {
        super(name);
    }

    @Override
    public void reset() {

    }

    @Override
    public void update() {
        if((fartTimer += Time.delta) > maxTime) {
            fartTimer %= maxTime;
            Groups.unit.each(u -> {
                if(!u.isAdded()) return;
                Fx.plasticExplosion.at(u);
            });
        }
    }

    @Override
    public void draw() {

    }

    @Override
    public void wave() {

    }

    @Override
    public void read(Reads r) {
        fartTimer = r.f();
    }

    @Override
    public void write(Writes w) {
        w.f(fartTimer);
    }
}
