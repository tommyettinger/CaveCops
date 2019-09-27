package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.IntFloatMap;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Measurement;
import squidpony.squidmath.*;

/**
 * Created by Tommy Ettinger on 9/23/2019.
 */
public class Creature {
    public Moth moth;
    public IntFloatMap costs;
    public int activity = 12;
    public DijkstraMap dijkstraMap;
    public GWTRNG rng;
    
    public static final IntFloatMap WALKING = new IntFloatMap(), AQUATIC = new IntFloatMap(),
            AMPHIBIOUS = new IntFloatMap(), FLYING = new IntFloatMap();
    
    static {
        WALKING.put('.', 1f);
        WALKING.put('"', 1f);
        WALKING.put(',', 2f);
        AQUATIC.put('~', 1f);
        AQUATIC.put(',', 1f);
        AMPHIBIOUS.putAll(WALKING);
        AMPHIBIOUS.putAll(AQUATIC);
        FLYING.put('.', 1f);
        FLYING.put('"', 1f);
        FLYING.put(',', 1f);
        FLYING.put('~', 1f);
        FLYING.put('A', 1f); // acid
        FLYING.put('L', 1f); // lava
        FLYING.put('_', 1f); // pit
    }

    public Creature(Animation<TextureAtlas.AtlasRegion> animation, Coord coord)
    {
        this(animation, coord, WALKING);
    }
    public Creature(Animation<TextureAtlas.AtlasRegion> animation, Coord coord, IntFloatMap costs)
    {
        moth = new Moth(animation, coord);
        rng = new GWTRNG(CrossHash.hash64(animation.getKeyFrame(0f).name) + coord.x ^
                DiverRNG.randomize(coord.hashCode()) - coord.y);
        this.costs = costs;
    }

    /**
     * Given a char[][] for the map, with this Creature's {@link #costs} already set to an IntFloatMap (where the keys
     * are really chars) that will be used to determine costs, configures the internal DijkstraMap for this map. It
     * expects any doors to be represented by '+' if closed or '/' if open (which can be caused by calling
     * DungeonUtility.closeDoors() ) and any walls to be '#' or box drawing characters. In the parameter costs, there
     * does not need to be an entry for '#' or any box drawing characters, but if one is present for '#' it will apply
     * that cost to both '#' and all box drawing characters, and if one is not present it will default to a very high
     * number. For any other entry in costs, a char in the 2D char array that matches the key will correspond
     * (at the same x,y position in the returned 2D double array) to that key's value in costs. If a char is used in the
     * map but does not have a corresponding key in costs, it will be given the value of the parameter defaultValue.
     * <p/>
     * The values in costs are multipliers, so should not be negative, should only be 0.0 in cases where you want
     * infinite movement across all adjacent squares of that kind, should be higher than 1.0 for difficult terrain (2.0
     * and 3.0 are reasonable), should be between 0.0 and 1.0 for easy terrain, and should be 1.0 for normal terrain.
     * If a cell should not be possible to enter for this character, 999.0 should be a reasonable value for a cost.
     * <p/>
     * An example use for this would be to make a creature unable to enter any non-water cell (like a fish),
     * unable to enter doorways (like some mythological versions of vampires), or to make a wheeled vehicle take more
     * time to move across rubble or rough terrain.
     * <p/>
     * A potentially common case that needs to be addressed is NPC movement onto staircases in games that have them;
     * some games may find it desirable for NPCs to block staircases and others may not, but in either case you should
     * give both '&gt;' and '&lt;', the standard characters for staircases, the same value in costs.
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors() .
     */
    public void configureMap(final char[][] map) {
        final int width = map.length;
        final int height = map[0].length;
        if(dijkstraMap == null)
            dijkstraMap = new DijkstraMap(map, Measurement.MANHATTAN, rng);
        else 
            dijkstraMap.initialize(map);
        dijkstraMap.standardCosts = false;
        int current;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                current = map[i][j];
                if (costs.containsKey(current)) {
                    dijkstraMap.costMap[i][j] = costs.get(current, 1f);
                } else {
                    switch (current) {
                        case ' ':
                        case '\1':
                        case '├':
                        case '┤':
                        case '┴':
                        case '┬':
                        case '┌':
                        case '┐':
                        case '└':
                        case '┘':
                        case '│':
                        case '─':
                        case '┼':
                        case '#':
                            dijkstraMap.costMap[i][j] = costs.get('#', 999500.0f);//(float)squidpony.squidai.DijkstraMap.WALL)
                            break;
                        default:
                            dijkstraMap.costMap[i][j] = 999500.0f; // default to unwalkable
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return moth.animation.getKeyFrame(0).name + moth.start + " to " + moth.end;
    }
}
