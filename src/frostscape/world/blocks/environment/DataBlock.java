package frostscape.world.blocks.environment;

import arc.util.io.Reads;
import arc.util.io.Writes;
import frostscape.world.environment.FloorDataHandler;

public interface DataBlock<T extends EnvironmentData> {
    default void handle(){
        FloorDataHandler.blocks.add(this);
    }
    boolean updates();
    default void update(T data){};
    T read(Reads read, byte revision);
    void write(T data, Writes writes);
    default byte revision(){
        return 0;
    };
    String name();
}
