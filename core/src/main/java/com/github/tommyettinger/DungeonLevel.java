package com.github.tommyettinger;

import squidpony.ArrayTools;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.*;

/**
 * Created by Tommy Ettinger on 9/30/2019.
 */
public class DungeonLevel {
    public final int width, height, depth;
    public char[][] decoDungeon, bareDungeon, lineDungeon, prunedDungeon;
    public float[][] backgrounds;
    public LightingHandler lighting;
    
    public DungeonLevel()
    {
        this(0, new DungeonGenerator(80, 48));
    }
    
    public DungeonLevel(int depth, DungeonGenerator dg)
    {
        this.depth = depth;
        width = dg.getWidth();
        height = dg.getHeight();
        dg.rng = new GWTRNG(DiverRNG.randomize(depth));
        decoDungeon = dg.getDungeon();
        if(decoDungeon == null)
            decoDungeon = dg.generate();
        bareDungeon = dg.getBareDungeon();
        lineDungeon = DungeonUtility.hashesToLines(decoDungeon);
        prunedDungeon = ArrayTools.copy(lineDungeon);
        backgrounds = new float[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int h = Noise.IntPointHash.hashAll(x, y, depth);
                backgrounds[x][y] = Visuals.getYCwCmSat(
                        128 + (h & 7) - (h >>> 3 & 7) + (h >>> 6 & 7) - (h >>> 9 & 7) + (h >>> 12 & 3) - (h >>> 14 & 3),
                        128 + (h >>> 16 & 7) - (h >>> 19 & 7),
                        128 + (h >>> 22 & 7) - (h >>> 25 & 7),
                        48 + (h >>> 29) // LSB of alpha/Sat is discarded
                );
            }
        }
        lighting = new LightingHandler(DungeonUtility.generateResistances(decoDungeon), Visuals.FLOAT_BLACK, Radius.CIRCLE, 3.0);
    }
    
    public void prune(final GreasedRegion seen)
    {
        LineKit.pruneLines(lineDungeon, seen, LineKit.light, prunedDungeon);

    }
}
