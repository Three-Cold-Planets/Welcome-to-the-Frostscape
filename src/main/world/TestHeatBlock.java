package main.world;

import mindustry.world.draw.DrawBlock;

public class TestHeatBlock extends BaseBlock{

    public TestHeatBlock(String name) {
        super(name);
        rotate = true;
    }
    public DrawBlock drawer;

    @Override
    public void load() {
        super.load();
        if(drawer == null) throw new IllegalStateException("What are you doing messing around with this class anyway? (Drawer CANOT be null)");
        drawer.load(this);
    }

    public class TestHeatBuild extends BaseBuilding{

        @Override
        public void draw(){
            drawer.draw(this);
        }

    }
}
