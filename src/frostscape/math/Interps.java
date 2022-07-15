package frostscape.math;

import arc.math.Interp;

public class Interps {
    public static Interp
    inout = new MultiInterp(new float[]{0, 0.5f}, new Interp[]{a -> a, a -> 1-a});
}
