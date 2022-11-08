package frostscape.ui.overlay;

import arc.Core;
import arc.audio.AudioSource;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.content.FrostBlocks;
import frostscape.content.Palf;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.LegsUnit;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

//THERE WILL BE BLOOD-SHED
public class ScanningOverlay {
    public SoundLoop scanSound = new SoundLoop(Sounds.respawning, 1);

    public float radius, squareRadius, progress, warmup;
    public boolean enabled = false, contained, previouslyContained, scanning;
    public Vec2 scanPos = new Vec2();
    public static Color[] colors = new Color[]{Pal.accentBack, Pal.accent};
    public Tile tile, actualTile, lastScanned;

    public void update(){
        if(Core.input.keyTap(KeyCode.semicolon) && !Core.scene.hasField() && !Core.scene.hasDialog()) {
            if(!enabled) {
                enabled = true;
                Vars.ui.showInfoToast("Block scan active! Press [;] again to cancel", 1);
            }
            else{
                enabled = false;
                Vars.ui.showInfoToast("Block scanning canceled", 1);
            }
        }

        if(!enabled){
            radius = Mathf.lerpDelta(radius, 0, 0.03f);
            squareRadius = Mathf.lerpDelta(squareRadius, 0, 0.03f);
            progress = Mathf.lerpDelta(progress, 0, 0.03f);
            return;
        }
        scanning = Core.input.keyDown(KeyCode.mouseLeft) && scannable(actualTile);
        Vars.player.shooting = false;
        Tmp.v1.set(Mathf.clamp(Core.input.mouseWorldX(), 0, Vars.world.unitWidth()), Mathf.clamp(Core.input.mouseWorldY(), 0, Vars.world.unitHeight()));
        if(!scanning) scanPos.lerp(Tmp.v1, 0.05f);
        tile = Vars.world.tileWorld(Tmp.v1.x, Tmp.v1.y);
        actualTile = scanning ? tile : Vars.world.tileWorld(scanPos.x, scanPos.y);
        if(scanning) scanPos.lerp(actualTile.worldx(), actualTile.worldy(), 0.03f);

        //Store previous state, so we know when the spot is contained again
        previouslyContained = contained;
        contained = Tmp.r1.set(tile.worldx() - Vars.tilesize * 2, tile.worldy() - Vars.tilesize * 2, Vars.tilesize * 4, Vars.tilesize * 4).contains(scanPos);
        radius = Mathf.lerpDelta(radius, scanning ? 1 : contained ? 0.5f : 0.2f, 0.03f);
        squareRadius = Mathf.lerpDelta(squareRadius, scanning ? 1 : 0, 0.03f);

        if(scanning) {
            progress += 0.0056 * Time.delta;
        }
        else {
            progress = Mathf.lerpDelta(progress, 0, 0.03f);
            if(Mathf.zero(progress)) progress = 0;
        }
        scanSound.update(actualTile.worldx(), actualTile.worldy(), scanning);

        if(progress >= 1){
            finishScanning(actualTile, 2);
        }
    }

    public void draw(){
        if(tile == null || actualTile == null || (!enabled && radius < 0.000001f)) return;
        Draw.reset();
        Lines.stroke(2);
        Draw.color(Pal.accent);

        float y1 = Mathf.sin(Time.time, 15, 5);
        float y2 = Mathf.sin(Time.time, 25, 5);
        Lines.line(scanPos.x - 6, scanPos.y + y1, scanPos.x - 6 + Math.min(squareRadius * 14, 12), scanPos.y + y1);
        Lines.line(scanPos.x - 9, scanPos.y + y2, scanPos.x - 9 + Math.min(squareRadius * 20, 18), scanPos.y + y2);

        Lines.stroke(1);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 30, 21), 0.75f, Time.time * 2);
        Lines.stroke(1.5f);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 30, 24), 0.6f, Time.time * 1.2f + 35);
        Lines.stroke(3f);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 30, 27), 0.3f, Time.time);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 30, 27), 0.3f, Time.time + 120);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 30, 27), 0.3f, Time.time + 240);

        if(!scanning) {
            Draw.color(Pal.shadow);
            Fill.rect(tile.worldx(), tile.worldy(), Vars.tilesize, Vars.tilesize);
        }

        if(!scannable(tile)) {
            Draw.color(Pal.remove);
            Draw.rect(Icon.cancel.getRegion(), tile.worldx() + 4, tile.worldy() + 4);
            return;
        }
        float rotationPer = Mathf.clamp(Interp.Pow.slowFast.apply(Mathf.maxZero((progress - 0.5f) * 2)), 0, 1);
        float radius = Math.min(progress * 8, 4);
        Draw.color(Tmp.c1.set(Pal.darkestMetal).a(1));
        Fill.circle(tile.worldx() + 4, tile.worldy() + 4, radius);
        //Filling the circle with progress
        Lines.stroke(3.5f, Pal.accent);
        Lines.arc(tile.worldx() + 4, tile.worldy() + 4, Math.min(progress * 8, 2.5f), rotationPer);
        //Fill the outsides
        Lines.stroke(Mathf.clamp(progress * 2) * 1.5f, Pal.accentBack);
        Lines.arc(tile.worldx() + 4, tile.worldy() + 4, radius, 5, rotationPer * 360);
        //Outline progress circle
        Lines.stroke(1, Tmp.c1.set(Pal.darkMetal).a(1));
        Lines.arc(tile.worldx() + 4, tile.worldy() + 4, Math.min(progress * 8, 5), 1);
    }

    public void drawScan(){
        if(!scanning || (!enabled && radius < 0.000001f)) return;
        Draw.color(Pal.accent);

        Tmp.v1.set(actualTile.worldx(), actualTile.worldy());

        //Draw the target lines
        float rotation = Time.time/2;
        Tmp.v2.trns(rotation, 8);
        Fill.square(Tmp.v1.x, Tmp.v1.y, Math.min(squareRadius * 10, 4));
        Draw.color();
        Draw.alpha(Mathf.sin(10, 1));
        Draw.rect(actualTile.block().uiIcon, actualTile.worldx(), actualTile.worldy(), 0);
    }

    public boolean scannable(Tile tile){
        return tile != lastScanned;
    }

    public void finishScanning(Tile tile, int part){
        lastScanned = tile;
        progress = 0;
        Sounds.unlock.at(tile.worldx(), tile.worldy());
        UnlockableContent scanned = tile.block();

        part %= 3;
        switch (part){
            case 0 -> {
                scanned = tile.floor();
                break;
            }
            case 1 -> {
                scanned = tile.overlay();
                break;
            }
        }
        Vars.ui.hudfrag.showUnlock(scanned);
    }
}
