package main.math;

import arc.math.Interp;
import arc.math.Mathf;

public class Interps {
    public static Interp
    inout = new MultiInterp(new float[]{0, 0.5f}, new Interp[]{a -> a, a -> 1-a}),
    fadePow = (a) -> (-1/ Mathf.pow(a + 1, 5) + 1) * 15/15.5f;
}
