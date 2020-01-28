package com.github.tommyettinger;

import com.badlogic.gdx.utils.IntIntMap;
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
    public GreasedRegion floors;
    public OrderedMap<Coord, Coord> decorations;

    public DungeonLevel()
    {
        this(0, new DungeonGenerator(80, 48), new IntIntMap(0));
    }
    
    public DungeonLevel(int depth, DungeonGenerator dg, IntIntMap decorationIndices)
    {
        this.depth = depth;
        width = dg.getWidth();
        height = dg.getHeight();
        dg.rng = new SilkRNG(DiverRNG.randomize(depth));
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
                        32 + (h & 7) - (h >>> 3 & 7) + (h >>> 6 & 7) - (h >>> 9 & 7) + (h >>> 12 & 3) - (h >>> 14 & 3),
                        128 + (h >>> 16 & 7) - (h >>> 19 & 7),
                        128 + (h >>> 22 & 7) - (h >>> 25 & 7), 
                    0xFF
//                        120 + (h >>> 28) // LSB of alpha/Sat is discarded
                );
            }
        }
        floors = new GreasedRegion(bareDungeon, '.');
        final int floorSpace = floors.size();
        decorations = new OrderedMap<>(floorSpace >>> 1, 0.25f);
//        for(IntMap.Entry<ArrayList<Animation<TextureAtlas.AtlasRegion>>> e : decorationMapping.entries())
        for(IntIntMap.Entry e : decorationIndices.entries())
        {
            floors.refill(decoDungeon, (char)e.key).mixedRandomRegion(0.375, -1, dg.rng.nextLong());
            final int count = floors.size(), count2 = (32 - Integer.numberOfLeadingZeros(count)) << 4;
            for(Coord c : floors)
            {
                if(dg.rng.nextSignedInt(count) < count2)
                {
                    decorations.put(c, Coord.get(e.key, ~dg.rng.next(1) & (int)(dg.rng.nextFloat() * (dg.rng.nextSignedInt(e.value) + 0.5f))));
                }
            }
        }
        floors.refill(bareDungeon, '.');
        lighting = new LightingHandler(DungeonUtility.generateResistances(decoDungeon), Visuals.FLOAT_BLACK, Radius.CIRCLE, 3.0);
    }
    
    public void prune(final GreasedRegion seen)
    {
        LineKit.pruneLines(lineDungeon, seen, LineKit.light, prunedDungeon);

    }
}
