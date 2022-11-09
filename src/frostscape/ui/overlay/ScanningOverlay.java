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
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import frostscape.content.FrostBlocks;
import frostscape.content.Palf;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.AirBlock;
import mindustry.world.blocks.environment.Cliff;
import mindustry.world.blocks.environment.SpawnBlock;

//THERE WILL BE BLOOD-SHED
public class ScanningOverlay {
    //Completely excluded from scanning. Doesn't apply to floors.
    public Seq<Class> excluded = new Seq<Class>();
    public SoundLoop scanSound = new SoundLoop(Sounds.respawning, 1);

    public float radius, warmup, progress, rotation;
    public boolean enabled = false, contained, previouslyContained, scanning;
    public Vec2 scanPos = new Vec2();
    public static Color[] colors = new Color[]{Pal.accentBack, Pal.accent};
    public Tile tile, actualTile, lastScanned;

    public ScanningOverlay(){
        excluded.addAll(AirBlock.class, Cliff.class);
    }

    public void update(){
        rotation += Time.delta * (1 + warmup) * (1 + Interp.smooth.apply(progress) * 8);
        if(Core.input.keyTap(KeyCode.semicolon) && !Core.scene.hasField() && !Core.scene.hasDialog()) {
            if(!enabled) {
                enabled = true;
                Vars.ui.showInfoToast("Environmental scanner active! Press [;] again to deactivate", 1);
                Sounds.buttonClick.play();
            }
            else{
                enabled = false;
                Vars.ui.showInfoToast("Environmental scanner deactivated", 1);
                Sounds.back.play();
            }
        }

        if(!enabled){
            radius = Mathf.lerpDelta(radius, 0, 0.03f);
            warmup = Mathf.lerpDelta(warmup, 0, 0.03f);
            progress = Mathf.lerpDelta(progress, 0, 0.03f);
            return;
        }
        scanning = Core.input.keyDown(KeyCode.mouseLeft) && scannable(actualTile);
        Vars.player.shooting = false;
        Tmp.v1.set(Mathf.clamp(Core.input.mouseWorldX(), 0, Vars.world.unitWidth() - Vars.tilesize), Mathf.clamp(Core.input.mouseWorldY(), 0, Vars.world.unitWidth() - Vars.tilesize));

        if(!scanning) {
            scanPos.lerp(Tmp.v1, 0.05f);
            //Don't move the current tile once scanning
            tile = Vars.world.tileWorld(Tmp.v1.x, Tmp.v1.y);
        }
        actualTile = Vars.world.tileWorld(scanPos.x, scanPos.y);

        if(scanning){
            //Snap targeting rectical and scanned tile to cursor
            actualTile = tile;
            if(actualTile.build != null) scanPos.lerp(actualTile.build.x, actualTile.build.y, 0.03f);
            scanPos.lerp(actualTile.worldx(), actualTile.worldy(), 0.03f);
        }

        //Store previous state, so we know when the spot is contained again
        previouslyContained = contained;
        contained = Tmp.r1.set(tile.worldx() - Vars.tilesize * 2, tile.worldy() - Vars.tilesize * 2, Vars.tilesize * 4, Vars.tilesize * 4).contains(scanPos);
        warmup = Mathf.lerpDelta(warmup, scanning ? 1 : 0, 0.02f);
        radius = Mathf.lerpDelta(radius, scanning ? 1 - 0.33f * (1 + Mathf.maxZero(progress - 0.3f) * 3) : contained ? 0.5f : 0.2f, 0.03f);

        if(scanning) {
            progress += 0.0056 * Time.delta * warmup;
        }
        else {
            progress = Mathf.lerpDelta(progress, 0, 0.03f);
            if(progress < 0.001f) progress = 0;
            if(warmup < 0.001f) warmup = 0;
        }
        scanSound.update(actualTile.worldx(), actualTile.worldy(), scanning);

        if(progress >= 1){
            finishScanning(actualTile, getLayer(actualTile));
        }
    }

    public void draw(){
        if(tile == null || actualTile == null || (!enabled && radius < 0.000001f)) return;
        Draw.reset();
        Lines.stroke(2);
        Draw.color(Pal.accent);

        float y1 = Mathf.sin(Time.time, 15, 5);
        float y2 = Mathf.sin(Time.time, 25, 5);
        Lines.line(scanPos.x - 6, scanPos.y + y1, scanPos.x - 6 + Math.min(warmup * 14, 12), scanPos.y + y1);
        Lines.line(scanPos.x - 9, scanPos.y + y2, scanPos.x - 9 + Math.min(warmup * 20, 18), scanPos.y + y2);

        Lines.stroke(1);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 45, 21), 0.75f, rotation * 2);
        Lines.stroke(1.5f);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 45, 24), 0.6f, rotation * 1.2f + 35);
        Lines.stroke(3f);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 45, 27), 0.3f, rotation);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 45, 27), 0.3f, rotation + 120);
        Lines.arc(scanPos.x, scanPos.y, Math.min(radius * 45, 27), 0.3f, rotation + 240);

        if(!scanning) {
            if(!scannable(tile)) {
                if(tile.build != null){
                    Draw.color(Pal.shadow);
                    Fill.rect(tile.build.x, tile.build.y, tile.build.block.size * Vars.tilesize, tile.build.block.size * Vars.tilesize);
                    Draw.color(Pal.remove);
                    Draw.rect(Icon.cancel.getRegion(), tile.build.x + tile.build.block.size * Vars.tilesize/2, tile.build.y + tile.build.block.size * Vars.tilesize/2);
                    return;
                }

                Draw.color(Pal.remove);
                Draw.rect(Icon.cancel.getRegion(), tile.worldx() + 4, tile.worldy() + 4);
                return;
            }
            Draw.color(Pal.shadow);
            Fill.rect(tile.worldx(), tile.worldy(), Vars.tilesize, Vars.tilesize);
        }
        float rotationPer = Mathf.clamp(Interp.Pow.slowFast.apply(Mathf.maxZero((progress - 0.5f) * 2)), 0, 1);
        float radius = Math.min(progress * 8, 4);
        Draw.color(Tmp.c1.set(Pal.darkestMetal).a(1));
        Fill.circle(actualTile.worldx() + 4, actualTile.worldy() + 4, radius);
        //Filling the circle with progress
        Lines.stroke(3.5f, Pal.accent);
        Lines.arc(actualTile.worldx() + 4, actualTile.worldy() + 4, Math.min(progress * 8, 2.5f), rotationPer);
        //Fill the outsides
        Lines.stroke(Mathf.clamp(progress * 2) * 1.5f, Pal.accentBack);
        Lines.arc(actualTile.worldx() + 4, actualTile.worldy() + 4, radius, 5, rotationPer * 360);
        //Outline progress circle
        Lines.stroke(1, Tmp.c1.set(Pal.darkMetal).a(1));
        Lines.arc(actualTile.worldx() + 4, actualTile.worldy() + 4, Math.min(progress * 8, 5), 1);
    }

    public void drawScan(){
        if(!scanning || (!enabled && radius < 0.001f)) return;

        Tmp.v1.set(actualTile.worldx(), actualTile.worldy());

        if(actualTile.build != null){
            Draw.alpha(Mathf.sin(15, 1));
            Draw.color(Pal.accent);
            Fill.square(actualTile.build.x, actualTile.build.y, warmup * actualTile.build.block.size * Vars.tilesize/2);
        }
        else {
            Draw.color(Pal.accent);
            Fill.square(Tmp.v1.x, Tmp.v1.y, Math.min(warmup * 10, 4));
            //Handle invalid blocks
            if(actualTile.block() instanceof AirBlock || actualTile.block() == Blocks.cliff){
                if(!(actualTile.overlay() instanceof AirBlock)) {
                    Draw.rect(actualTile.overlay().uiIcon, actualTile.worldx(), actualTile.worldy(), 0);
                }
            }
            else Draw.rect(actualTile.block().uiIcon, actualTile.worldx(), actualTile.worldy(), 0);
        }
    }

    public boolean valid(Building build){
        return build instanceof Scannable b && b.canBeScanned();
    }
    public boolean scannable(Tile tile){
        return tile != lastScanned && getLayer(tile) != -1;
    }

    /**
     * Returns an int corosponding to layer of the tile. Returns 3 for buildings, 2 for walls, 1 for overlays and 0 for floors. If invalid, returns a -1.
     * @param tile
     * @return
     */
    public int getLayer(Tile tile){
        if(tile == null) return -1;
        if(tile.build != null){
            if(valid(tile.build)) return 3;
            else return -1;
        }
        if(tile.overlay().wallOre) return 1;
        if(!excluded.contains(tile.block().getClass())) return 2;
        if(!excluded.contains(tile.overlay().getClass())) return 1;
        return 0;
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
        if(tile.build instanceof Scannable b) b.scaned();
        if(Vars.state.isCampaign()) Vars.ui.hudfrag.showUnlock(scanned);
    }
}
