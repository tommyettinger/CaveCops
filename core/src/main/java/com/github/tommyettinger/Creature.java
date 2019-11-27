package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.IntFloatMap;
import squidpony.FakeLanguageGen;
import squidpony.StringKit;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Measurement;
import squidpony.squidmath.*;

/**
 * Created by Tommy Ettinger on 9/23/2019.
 */
public class Creature {
    public CreatureFactory.CreatureArchetype archetype;
    public String name;
    public String nameTitled;
    public Moth moth;
    public MoveType moveType;
    public IntFloatMap costs;
    public int activity = 12;
    public DijkstraMap dijkstraMap;
    public SilkRNG rng;
    public TweakRNG fortune;
    public Radiance glow;
    
    public StatHolder stats;
    
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

    public Creature(Animation<TextureAtlas.AtlasRegion> animation, Coord coord,
                    CreatureFactory.CreatureArchetype archetype)
    {
        this(animation, coord, archetype, null); 
    }
    public Creature(Animation<TextureAtlas.AtlasRegion> animation, Coord coord,
                    CreatureFactory.CreatureArchetype archetype, StatHolder stats)
    {
        moth = new Moth(animation, coord);
        this.archetype = archetype;
        final long a = CrossHash.hash64(archetype.name) + coord.x,
                b = DiverRNG.randomize(coord.hashCode()) - coord.y;
        rng = new SilkRNG(a ^ b);
        fortune = new TweakRNG(b, a, (a & b) >>> 54, -500);
        name = FakeLanguageGen.GOBLIN.word(rng, true, Math.min(rng.nextSignedInt(3), rng.nextSignedInt(3)) + 1);
        nameTitled = name + " the " + StringKit.capitalize(archetype.name);
        glow = new Radiance(0.9f,
                Visuals.getYCwCmSat(0xA0, 0xFF, 0x08, 0x40),
                0f,
                0.4f,
                rng.nextFloat());
//          final int c = rng.nextInt();
//          glow = new Radiance(rng.nextFloat() * 3f + 2f,
//                Visuals.getYCwCmSat((c & 0x3F) + 0x90, c >>> 8 & 0xFF, c >>> 16 & 0xFF, (c >>> 26) + 20),
//                rng.nextFloat() * 0.45f + 0.3f,
//                0f);
        moveType = archetype.move;
        this.costs = moveType.moves;
        this.stats = (stats == null)
                ? new StatHolder(rng.between(4, 16), rng.between(4, 16),
                rng.next(2)+1, rng.next(2)+1, rng.next(2)+1, rng.next(2)+1) 
                : stats;
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
    public void configureMap(final DungeonLevel map) {
        final int width = map.width;
        final int height = map.height;
        if(dijkstraMap == null)
            dijkstraMap = new DijkstraMap(map.decoDungeon, Measurement.MANHATTAN, rng);
        else 
            dijkstraMap.initialize(map.decoDungeon);
        dijkstraMap.standardCosts = false;
        int current;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                current = map.decoDungeon[i][j];
                if((dijkstraMap.costMap[i][j] = costs.get(current, 999500.0f)) >= DijkstraMap.WALL)
                    dijkstraMap.physicalMap[i][j] = DijkstraMap.WALL;
//                if (costs.containsKey(current)) {
//                    dijkstraMap.costMap[i][j] = costs.get(current, 1f);
//                } else {
//                    switch (current) {
//                        case ' ':
//                        case '\1':
//                        case '├':
//                        case '┤':
//                        case '┴':
//                        case '┬':
//                        case '┌':
//                        case '┐':
//                        case '└':
//                        case '┘':
//                        case '│':
//                        case '─':
//                        case '┼':
//                        case '#':
//                            dijkstraMap.costMap[i][j] = costs.get('#', 999500.0f);//(float)squidpony.squidai.DijkstraMap.WALL)
//                            break;
//                        default:
//                            dijkstraMap.costMap[i][j] = 999500.0f; // default to unwalkable
//                    }
//                }
            }
        }
    }

    @Override
    public String toString() {
        return moth.animation.getKeyFrame(0).name + moth.start + " to " + moth.end;
    }

    public enum MoveType {
        WALKING(Creature.WALKING),
        AQUATIC(Creature.AQUATIC),
        AMPHIBIOUS(Creature.AMPHIBIOUS),
        FLYING(Creature.FLYING);
        
        MoveType() {
            moves = Creature.WALKING;
        }
        MoveType(IntFloatMap moves) {
            this.moves = moves;
        }
        public IntFloatMap moves;
        
        public static final MoveType[] ALL = values();
    }
}
