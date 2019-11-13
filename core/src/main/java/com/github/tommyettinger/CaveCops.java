package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import regexodus.Pattern;
import regexodus.Replacer;
import squidpony.FakeLanguageGen;
import squidpony.Maker;
import squidpony.StringKit;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;
import squidpony.squidgrid.mapping.styled.TilesetType;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.badlogic.gdx.Input.Keys.*;

/**
 * This is a small, not-overly-simple demo that presents some important features of SquidLib and shows a faster,
 * cleaner, and more recently-introduced way of displaying the map and other text. Features include dungeon map
 * generation, field of view, pathfinding (to the mouse position), continuous noise (used for a wavering torch effect),
 * language generation/ciphering, a colorful glow effect, and ever-present random number generation (with a seed).
 * You can increase the size of the map on most target platforms (but GWT struggles with large... anything) by
 * changing gridHeight and gridWidth to affect the visible area or bigWidth and bigHeight to adjust the size of the
 * dungeon you can move through, with the camera following your '@' symbol.
 * <br>
 * The assets folder of this project, if it was created with SquidSetup, will contain the necessary font files (just one
 * .fnt file and one .png are needed, but many more are included by default). You should move any font files you don't
 * use out of the assets directory when you produce a release JAR, APK, or GWT build.
 */
public class CaveCops extends ApplicationAdapter {
    public ISync sync;
    public CaveCops()
    {
        sync = new ISync.EmptySync();
    }
    
    public CaveCops(ISync iSync)
    {
        sync = iSync;
    }
    
    public static final int SELECT = 0, ANIMATE = 1, NPC = 2;
    private static final int CREATURE_COUNT = 64;

    public int mode = SELECT;
    
    // MutantBatch is almost the same as SpriteBatch, but is a bit faster with SquidLib since it sets colors quickly
    private MutantBatch batch;
    private PixelPerfectViewport mainViewport;
    private Camera camera;

    private SilkRNG rng;

    private TextureAtlas atlas;
    // This maps chars, such as '#', to specific images, such as a pillar.
    private IntMap<Animation<TextureAtlas.AtlasRegion>> charMapping;
    private IntMap<ArrayList<Animation<TextureAtlas.AtlasRegion>>> decorationMapping;
    private IntIntMap decorationIndices;
    private BitmapFont font;
    
    private DungeonGenerator dungeonGen;
    public DungeonLevel dl;
//    private char[][] decoDungeon, bareDungeon, lineDungeon, prunedDungeon;
//    private float[][] backgrounds;
    
    public ShaderProgram shader;
//    public Vector3 add, mul;
    private Texture palette, currentPalette, oldPalette, bigPalette;
    
//    public IndexedAPNG png;

    /** In number of cells */
    private static final int gridWidth = 40;
    /** In number of cells */
    private static final int gridHeight = 24;

    /** In number of cells */
    private static final int bigWidth = gridWidth * 2;
    /** In number of cells */
    private static final int bigHeight = gridHeight * 2;

    /** The pixel width of a cell */
    private static final int cellWidth = 32;
    /** The pixel height of a cell */
    private static final int cellHeight = 32;
    public long startTime = 0L, animationStart = 0L;
    private Animation<TextureAtlas.AtlasRegion> solid, playerAnimation;

    private InputProcessor input;

    private Color bgColor;
    private DijkstraMap playerToCursor;
    private Coord cursor;
    private ArrayList<Coord> toCursor;
    private ArrayList<Coord> awaitedMoves;

    private Vector2 pos = new Vector2();


//    private double[][] resistance;
    private double[][] visible;
    // Here, we use a GreasedRegion to store all floors that the player can walk on, a small rim of cells just beyond
    // the player's vision that blocks pathfinding to areas we can't see a path to, and we also store all cells that we
    // have seen in the past in a GreasedRegion (in most roguelikes, there would be one of these per dungeon floor).
    //private GreasedRegion blockage, seen, currentlySeen;
    private LinkedHashSet<Coord> impassable;
    
    private GapShuffler<String> zodiacShuffler, phraseShuffler, meaningShuffler;
    private Replacer anReplacer;
    private String horoscope;

    private Creature playerCreature;
    public Populace creatures;
    public CreatureFactory creatureFactory;
    public LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> mapping;
    private OrderedMap<Coord, Animation<TextureAtlas.AtlasRegion>> decorations;
    

    public static LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> makeMapping(final TextureAtlas atlas){
        final Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
        TextureAtlas.AtlasRegion item;
        final LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> lhm = new LinkedHashMap<>(regions.size, 0.5f);
        for (int i = 0; i < regions.size; i++) {
            if(!lhm.containsKey((item = regions.get(i)).name))
                lhm.put(item.name, new Animation<>(0.375f, atlas.findRegions(item.name), Animation.PlayMode.LOOP));
        }
        return lhm;
    }


    @Override
    public void create () {
        startTime = TimeUtils.millis();
        Coord.expandPoolTo(bigWidth, bigHeight);
//        png = new IndexedAPNG(gridWidth * cellWidth * gridHeight * cellHeight * 3 >> 1);
        // gotta have a random number generator. We can seed an RNG with any long we want, or even a String.
        // if the seed is identical between two runs, any random factors will also be identical (until user input may
        // cause the usage of an RNG to change). You can randomize the dungeon and several other initial settings by
        // just removing the String seed, making the line "rng = new SilkRNG();" . Keeping the seed as a default allows
        // changes to be more easily reproducible, and using a fixed seed is strongly recommended for tests. 

        // SquidLib has many methods that expect an IRNG instance, and there's several classes to choose from.
        // In this program we'll use SilkRNG, which will behave better on the HTML target than other generators.
        rng = new SilkRNG(Long.parseLong("CAVECOPS", 36));
        
        String[] zodiac = new String[12];
        RNG languageRNG = new RNG(new XoshiroStarPhi32RNG(DiverRNG.determine(startTime)));
        FakeLanguageGen lang = FakeLanguageGen.randomLanguage(languageRNG).mix(FakeLanguageGen.ANCIENT_EGYPTIAN, 0.6);
        lang.modifiers.add(FakeLanguageGen.Modifier.REDUCE_ACCENTS);
        for (int i = 0; i < zodiac.length; i++) {
            zodiac[i] = lang.word(languageRNG, true, languageRNG.maxIntOf(4, 2) + 1);
        }
        String[] phrases = new String[]{" is in retrograde", " ascends", " reaches toward the North", " leans Southward",
                " stands against the West wind", " charges into the East", " resides in the Castle",
                " feels pensive", " seizes the day", " looms mightily", " retreats into darkness"},
                meanings = new String[]
                        {". It is a dire omen for those under the sign of @.",
                                ". This bodes ill for the house of @.",
                                ". Mayhaps this is a significant portent for they with the sign of @...",
                                "! Buy a lottery ticket if you're a @!",
                                ". You should avoid spicy foods if you bear the sign of @.",
                                ". That's some bad juju for those poor fools from @.",
                                ". A golden opportunity for any @!",
                                ". If you're a @, you're probably not gonna die!",
                                ". If you're a @, gird thy loins...",
                                ". This is going to be a bad one.",
                                ". Oh yeah, this is gonna be good...",
                                ". Does anyone else smell smoke?",
                                "! Party time!",
                                "! This is the dawning of the Age of Aquarius!"};
        zodiacShuffler = new GapShuffler<>(zodiac, languageRNG);
        phraseShuffler = new GapShuffler<>(phrases, languageRNG);
        meaningShuffler = new GapShuffler<>(meanings, languageRNG);
        anReplacer = new Replacer(Pattern.compile("\\b([Aa]) (?=[AEIOUaeiou])"), "$1n ");
        horoscope = anReplacer.replace(zodiacShuffler.next() + phraseShuffler.next() + meaningShuffler.next().replace("@", zodiacShuffler.next()));
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Visuals.vertexShader, Visuals.fragmentShader);
        //shader = new ShaderProgram(Visuals.vertexShader, Visuals.fragmentShaderTrue);
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        batch = new MutantBatch(8000, shader);
//        add = new Vector3(0, 0, 0);
//        mul = new Vector3(1, 1, 1);

        mainViewport = new PixelPerfectViewport(Scaling.fill, gridWidth, gridHeight);
        mainViewport.setScreenBounds(0, 0, gridWidth * cellWidth, gridHeight * cellHeight);
        camera = mainViewport.getCamera();
        camera.update();
        
        atlas = new TextureAtlas("Dawnlike2.atlas");
        mapping = makeMapping(atlas);
        font = new BitmapFont(Gdx.files.internal("font2.fnt"), atlas.findRegion("font"));
        font.setUseIntegerPositions(false);
        font.getData().setScale(1f / cellHeight);
//        font.getData().setLineHeight(font.getLineHeight() * 2f / cellHeight);

//        palette = new Texture("AuroraLloyd_GLSL.png");
        palette = currentPalette = new Texture("Ward_GLSL.png");
        oldPalette = new Texture("RelaxedRoll_GLSL.png");
        bigPalette = new Texture("WardBonus_GLSL.png");
//        palette = new Texture("GBGreen_GLSL.png");
//        palette = new Texture("AuroraRelaxed_GLSL.png");
//        palette = new Texture("DB_Aurora_GLSL.png");
//        palette = new Texture("Sheltzy32_GLSL.png");
//        palette = new Texture("DawnSmash256_GLSL.png");
//        palette = new Texture("GBGreen16_GLSL.png");
        
        charMapping = new IntMap<>(64);
        decorationMapping = new IntMap<>(64);
        decorationIndices = new IntIntMap(64);
//        spawnMapping = new IntMap<>(64);
        solid = mapping.get("day tile floor c");
        playerAnimation = mapping.get("keystone kop");
        charMapping.put('.', solid);
        charMapping.put(',', mapping.get("brick clear pool center"      ));
        charMapping.put('~', mapping.get("brick murky pool center"      ));
        charMapping.put('"', mapping.get("dusk grass floor c"      ));
        charMapping.put('#', mapping.get("lit brick wall center"     ));
        charMapping.put('+', mapping.get("closed wooden door front"));
        charMapping.put('/', mapping.get("closed wooden door side"  ));
        charMapping.put('└', mapping.get("lit brick wall right down"            ));
        charMapping.put('┌', mapping.get("lit brick wall right up"            ));
        charMapping.put('┬', mapping.get("lit brick wall left right up"           ));
        charMapping.put('┴', mapping.get("lit brick wall left right down"           ));
        charMapping.put('─', mapping.get("lit brick wall left right"            ));
        charMapping.put('│', mapping.get("lit brick wall up down"            ));
        charMapping.put('├', mapping.get("lit brick wall right up down"           ));
        charMapping.put('┼', mapping.get("lit brick wall left right up down"          ));
        charMapping.put('┤', mapping.get("lit brick wall left up down"           ));
        charMapping.put('┐', mapping.get("lit brick wall left up"            ));
        charMapping.put('┘', mapping.get("lit brick wall left down"            ));

        decorationMapping.put('"', Maker.makeList(
                mapping.get("sparse green grass"),
                mapping.get("green grass"),
                mapping.get("sparse green scrub"),
                mapping.get("green scrub"),
                mapping.get("sparse white flowers"),
                mapping.get("white flowers"),
                mapping.get("sparse blue flowers"),
                mapping.get("blue flowers"),
                mapping.get("sparse gold flowers"),
                mapping.get("gold flowers"),
                mapping.get("sparse red flowers"),
                mapping.get("red flowers")
        ));
        decorationMapping.put('.', Maker.makeList(mapping.get("sparse gray pebbles"),
                mapping.get("gray pebbles"),
                mapping.get("sparse brown pebbles"),
                mapping.get("brown pebbles"),
                mapping.get("sparse gray rocks"),
                mapping.get("gray rocks")
        ));
        
        for (IntMap.Entry<ArrayList<Animation<TextureAtlas.AtlasRegion>>> e : decorationMapping.entries())
        {
            decorationIndices.put(e.key, e.value.size());
        }
        
//        spawnMapping.put('~', Maker.makeList(
//                mapping.get("fighting fish"),
//                mapping.get("red snapper"),
//                mapping.get("stonefish"),
//                mapping.get("catfish"),
//                mapping.get("piranha"),
//                mapping.get("lobster"),
//                mapping.get("jellyfish"),
//                mapping.get("man o war"),
//                mapping.get("barracuda"),
//                mapping.get("great white shark"),
//                mapping.get("tiger shark"),
//                mapping.get("beluga whale"),
//                mapping.get("blue whale"),
//                mapping.get("river dolphin"),
//                mapping.get("snakefish"),
//                mapping.get("bobbit worm"),
//                mapping.get("eel"),
//                mapping.get("electric eel"),
//                mapping.get("kraken"),
//                mapping.get("sea tiger"),
//                mapping.get("platypus"),
//                mapping.get("cave penguin"),
//                mapping.get("penguin"),
//                mapping.get("pelican"),
//                mapping.get("duck"),
//                mapping.get("puffin"),
//                mapping.get("swan"),
//                mapping.get("albatross")
//        ));
//        spawnMapping.put(',', Maker.makeList(
//                mapping.get("fighting fish"),
//                mapping.get("stonefish"),
//                mapping.get("piranha"),
//                mapping.get("lobster"),
//                mapping.get("snakefish"),
//                mapping.get("sea nymph"),
//                mapping.get("river nymph"),
//                mapping.get("bog nymph"),
//                mapping.get("platypus"),
//                mapping.get("leech"),
//                mapping.get("frog"),
//                mapping.get("cave penguin"),
//                mapping.get("penguin"),
//                mapping.get("baby pelican"),
//                mapping.get("pelican"),
//                mapping.get("baby duck"),
//                mapping.get("duck"),
//                mapping.get("baby puffin"),
//                mapping.get("puffin"),
//                mapping.get("baby swan"),
//                mapping.get("swan"),
//                mapping.get("baby albatross"),
//                mapping.get("albatross")
//        ));
//        
//        for(ArrayList<Animation<TextureAtlas.AtlasRegion>> list : spawnMapping.values())
//        {
//            for(Animation<TextureAtlas.AtlasRegion> animation : list)
//            {
//                System.out.println(animation == null ? "!!!!!!" : animation.getKeyFrame(0).name);
//            }
//        }
        
        //This uses the seeded RNG we made earlier to build a procedural dungeon using a method that takes rectangular
        //sections of pre-drawn dungeon and drops them into place in a tiling pattern. It makes good winding dungeons
        //with rooms by default, but in the later call to dungeonGen.generate(), you can use a TilesetType such as
        //TilesetType.ROUND_ROOMS_DIAGONAL_CORRIDORS or TilesetType.CAVES_LIMIT_CONNECTIVITY to change the sections that
        //this will use, or just pass in a full 2D char array produced from some other generator, such as
        //SerpentMapGenerator, OrganicMapGenerator, or DenseRoomMapGenerator.
        dungeonGen = new DungeonGenerator(bigWidth, bigHeight, rng);
        //uncomment this next line to randomly add water to the dungeon in pools.
        dungeonGen.addWater(18);
        dungeonGen.addGrass(12);
        FlowingCaveGenerator flowing = new FlowingCaveGenerator(bigWidth, bigHeight, TilesetType.DEFAULT_DUNGEON, rng);
        dungeonGen.generate(flowing.generate());
        dl = new DungeonLevel(0, dungeonGen, decorationIndices);
//        DungeonBoneGen gen = new DungeonBoneGen(this.rng);
//        CellularAutomaton ca = new CellularAutomaton(bigWidth, bigHeight);
//        gen.generate(TilesetType.DEFAULT_DUNGEON, bigWidth, bigHeight);
//        ca.remake(gen.region);
//        gen.region.and(ca.runBasicSmoothing()).deteriorate(rng, 0.9);
//        gen.region.and(ca.runBasicSmoothing()).deteriorate(rng, 0.9);
//        ca.current.remake(gen.region.deteriorate(rng, 0.9));
//        gen.region.or(ca.runBasicSmoothing());
//        gen.region = gen.region.removeEdges().largestPart();
//        decoDungeon = dungeonGen.generate(gen.region.intoChars(gen.getDungeon(), '.', '#'));

//        decoDungeon = dungeonGen.generate(TilesetType.DEFAULT_DUNGEON);
        
//        bareDungeon = dungeonGen.getBareDungeon();
//        lineDungeon = DungeonUtility.hashesToLines(decoDungeon);

        //resistance = dl.lighting.resistances;
        visible = new double[bigWidth][bigHeight];
        dl.floors.copy().expand8way().writeDoublesInto(visible, 1.0);
        final int floorSpace = dl.floors.size();
        decorations = new OrderedMap<>(floorSpace >>> 1, 0.25f);
        final int decoSize = dl.decorations.size();
        for (int i = 0; i < decoSize; i++) {
            Coord ki = dl.decorations.getAt(i);
            decorations.put(dl.decorations.keyAt(i), decorationMapping.get(ki.x).get(ki.y));
        }
        creatures = new Populace(dl);
        creatureFactory = new CreatureFactory(creatures, mapping);
        for (int i = 0; i < CREATURE_COUNT; i++) {
            creatureFactory.place();
        }
//        for(IntMap.Entry<ArrayList<Animation<TextureAtlas.AtlasRegion>>> e : spawnMapping.entries())
//        {
//            floors.refill(decoDungeon, (char)e.key);
//            floors.mixedRandomRegion(0.1, floors.size() * 48 / floorSpace, rng.nextLong());
//            for(Coord c : floors)
//            {
//                creatures.place(new Creature(rng.getRandomElement(e.value), c, Creature.AQUATIC));
//            }
//        }
        //Coord is the type we use as a general 2D point, usually in a dungeon.
        //Because we know dungeons won't be incredibly huge, Coord performs best for x and y values less than 256, but
        // by default it can also handle some negative x and y values (-3 is the lowest it can efficiently store). You
        // can call Coord.expandPool() or Coord.expandPoolTo() if you need larger maps to be just as fast.
        cursor = Coord.get(-1, -1);
        // here, we need to get a random floor cell to place the player upon, without the possibility of putting him
        // inside a wall. There are a few ways to do this in SquidLib. The most straightforward way is to randomly
        // choose x and y positions until a floor is found, but particularly on dungeons with few floor cells, this can
        // have serious problems -- if it takes too long to find a floor cell, either it needs to be able to figure out
        // that random choice isn't working and instead choose the first it finds in simple iteration, or potentially
        // keep trying forever on an all-wall map. There are better ways! These involve using a kind of specific storage
        // for points or regions, getting that to store only floors, and finding a random cell from that collection of
        // floors. The two kinds of such storage used commonly in SquidLib are the "packed data" as short[] produced by
        // CoordPacker (which use very little memory, but can be slow, and are treated as unchanging by CoordPacker so
        // any change makes a new array), and GreasedRegion objects (which use slightly more memory, tend to be faster
        // on almost all operations compared to the same operations with CoordPacker, and default to changing the
        // GreasedRegion object when you call a method on it instead of making a new one). Even though CoordPacker
        // sometimes has better documentation, GreasedRegion is generally a better choice; it was added to address
        // shortcomings in CoordPacker, particularly for speed, and the worst-case scenarios for data in CoordPacker are
        // no problem whatsoever for GreasedRegion. CoordPacker is called that because it compresses the information
        // for nearby Coords into a smaller amount of memory. GreasedRegion is called that because it encodes regions,
        // but is "greasy" both in the fatty-food sense of using more space, and in the "greased lightning" sense of
        // being especially fast. Both of them can be seen as storing regions of points in 2D space as "on" and "off."

        // Here we fill a GreasedRegion so it stores the cells that contain a floor, the '.' char, as "on."
        dl.floors.refill(dl.bareDungeon, '.');
        //player is, here, just a Coord that stores his position. In a real game, you would probably have a class for
        //creatures, and possibly a subclass for the player. The singleRandom() method on GreasedRegion finds one Coord
        // in that region that is "on," or -1,-1 if there are no such cells. It takes an RNG object as a parameter, and
        // if you gave a seed to the RNG constructor, then the cell this chooses will be reliable for testing. If you
        // don't seed the RNG, any valid cell should be possible.
        playerCreature = new Creature(playerAnimation, dl.floors.singleRandom(rng), Creature.WALKING);
        playerCreature.glow = new Radiance(1f, Visuals.getYCwCmSat(0xB0, 0x00, 0x60, 0x70), 0f, 0.9f, languageRNG.nextFloat());
        playerCreature.configureMap(creatures.dl);
        creatures.putAt(playerCreature.moth.end, playerCreature, 0);
        dl.lighting.addLight(playerCreature.moth.start, playerCreature.glow);
//        dl.lighting.calculateFOV(playerCreature.moth.start);
        dl.lighting.updateAll();
        // Uses shadowcasting FOV and reuses the visible array without creating new arrays constantly.
        //FOV.reuseFOV(resistance, visible, playerCreature.moth.start.x, playerCreature.moth.start.y, 9.0, Radius.CIRCLE);//, (System.currentTimeMillis() & 0xFFFF) * 0x1p-4, 60.0);
        
        // 0.01 is the upper bound (inclusive), so any Coord in visible that is more well-lit than 0.01 will _not_ be in
        // the blockage Collection, but anything 0.01 or less will be in it. This lets us use blockage to prevent access
        // to cells we can't see from the start of the move.
        //blockage = new GreasedRegion(visible, 0.0);
        //seen = blockage.not().copy();
        //currentlySeen = seen.copy();
        //blockage.fringe8way();
        impassable = new LinkedHashSet<>(creatures.size(), 0.25f);
        // prunedDungeon starts with the full lineDungeon, which includes features like water and grass but also stores
        // all walls as box-drawing characters. The issue with using lineDungeon as-is is that a character like '┬' may
        // be used because there are walls to the east, west, and south of it, even when the player is to the north of
        // that cell and so has never seen the southern connecting wall, and would have no reason to know it is there.
        // By calling LineKit.pruneLines(), we adjust prunedDungeon to hold a variant on lineDungeon that removes any
        // line segments that haven't ever been visible. This is called again whenever seen changes. 
        //dl.prune(seen);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(200);
        //When a path is confirmed by clicking, we draw from this List to find which cell is next to move into.
        awaitedMoves = new ArrayList<>(200);
        //DijkstraMap is the pathfinding swiss-army knife we use here to find a path to the latest cursor position.
        //DijkstraMap.Measurement is an enum that determines the possibility or preference to enter diagonals. Here, the
        // MANHATTAN value is used, which means 4-way movement only, no diagonals possible. Alternatives are CHEBYSHEV,
        // which allows 8 directions of movement at the same cost for all directions, and EUCLIDEAN, which allows 8
        // directions, but will prefer orthogonal moves unless diagonal ones are clearly closer "as the crow flies."
        playerToCursor = playerCreature.dijkstraMap;//new DijkstraMap(decoDungeon, Measurement.MANHATTAN);
        playerToCursor.setGoal(playerCreature.moth.start);
        //impassable.addAll(blockage);
        impassable.addAll(creatures.keySet());
        playerToCursor.partialScan(gridWidth + gridHeight, impassable);

        bgColor = new Color(0x100818ff); // for Ward
//        bgColor = new Color(0x100818FF); // for RelaxedRoll
//        bgColor = new Color(0x132C2DFF); // for GBGreen16
//        bgColor = new Color(0x000008FF);   // for AuroraLloyd
//        bgColor = new Color(0x000010FF);   // for AuroraRelaxed
//        bgColor = new Color(0x010101FF);   // for DB_Aurora
//        bgColor = new Color(0x000000FF);   // for Sheltzy32
//        bgColor = new Color(0x140C1CFF);   // for DawnSmash256
        
        input = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(mode != SELECT) return false;
                switch (keycode)
                {
                    case UP:
                    case W:
                    case NUMPAD_8:
                        toCursor.clear();
                        //+1 is up on the screen
                        awaitedMoves.add(playerCreature.moth.start.translate(0, 1));
                        break;
                    case DOWN:
                    case S:
                    case NUMPAD_2:
                        toCursor.clear();
                        //-1 is down on the screen
                        awaitedMoves.add(playerCreature.moth.start.translate(0, -1));
                        break;
                    case LEFT:
                    case A:
                    case NUMPAD_4:
                        toCursor.clear();
                        awaitedMoves.add(playerCreature.moth.start.translate(-1, 0));
                        break;
                    case RIGHT:
                    case D:
                    case NUMPAD_6:
                        toCursor.clear();
                        awaitedMoves.add(playerCreature.moth.start.translate(1, 0));
                        break;
//                    case NUMPAD_1:
//                        toCursor.clear();
//                        awaitedMoves.add(playerGrid.translate(-1, -1));
//                        break;
//                    case NUMPAD_3:
//                        toCursor.clear();
//                        awaitedMoves.add(playerGrid.translate(1, -1));
//                        break;
//                    case NUMPAD_7:
//                        toCursor.clear();
//                        awaitedMoves.add(playerGrid.translate(-1, 1));
//                        break;
//                    case NUMPAD_9:
//                        toCursor.clear();
//                        awaitedMoves.add(playerGrid.translate(1, 1));
//                        break;
                    case PERIOD:
                    case NUMPAD_5:
                        toCursor.clear();
                        awaitedMoves.add(playerCreature.moth.start);
                        break;
                    case P:
                        if(Gdx.input.isKeyPressed(SHIFT_LEFT))
                            palette = bigPalette;
                        else 
                            palette = (palette == oldPalette) ? currentPalette : oldPalette;
                        break;
//                    case BACKSLASH:
//                        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
//                        // this loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
//                        for(int i = 3; i < pixels.length; i += 4) {
//                            pixels[i] = (byte) 255;
//                        }
//                        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
//                        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
//                        try {
//                            png.write(Gdx.files.local("Screenshot " + new Date().toString().replace(':', '-') +".png"), pixmap);
//                        } catch (IOException e) {
//                        }
//                        pixmap.dispose();
//                        break;
                    case F:
                        if(Gdx.graphics.isFullscreen())
                            Gdx.graphics.setWindowedMode(cellWidth * gridWidth, cellHeight * gridHeight);
                        else 
                        {
                            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                            Gdx.graphics.setVSync(true);
                        }
                        break;
                    case ESCAPE:
                        Gdx.app.exit();
                        break;
                }
                return true;
            }

            // if the user clicks and mouseMoved hasn't already assigned a path to toCursor, then we call mouseMoved
            // ourselves and copy toCursor over to awaitedMoves.
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if(mode != SELECT) return false;
                pos.set(screenX, screenY);
                mainViewport.unproject(pos);
                if (onGrid(MathUtils.floor(pos.x), MathUtils.floor(pos.y))) {
                    mouseMoved(screenX, screenY);
                    awaitedMoves.addAll(toCursor);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return mouseMoved(screenX, screenY);
            }

            // causes the path to the mouse position to become highlighted (toCursor contains a list of Coords that
            // receive highlighting). Uses DijkstraMap.findPathPreScanned() to find the path, which is rather fast.
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if(mode != SELECT) return false;
                if(!awaitedMoves.isEmpty())
                    return false;
                pos.set(screenX, screenY);
                mainViewport.unproject(pos);
                if (onGrid(screenX = MathUtils.floor(pos.x), screenY = MathUtils.floor(pos.y))) {
                    // we also need to check if screenX or screenY is out of bounds.
                    if (screenX < 0 || screenY < 0 || screenX >= bigWidth || screenY >= bigHeight ||
                            (cursor.x == screenX && cursor.y == screenY)) {
                        return false;
                    }
                    cursor = Coord.get(screenX, screenY);
                    // This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                    // player position to the position the user clicked on. The "PreScanned" part is an optimization
                    // that's special to DijkstraMap; because the part of the map that is viable to move into has
                    // already been fully analyzed by the DijkstraMap.partialScan() method at the start of the
                    // program, and re-calculated whenever the player moves, we only need to do a fraction of the
                    // work to find the best path with that info.
                    toCursor.clear();
                    final int a = playerCreature.rng.stateA, b = playerCreature.rng.stateB;
                    playerCreature.rng.stateA = playerCreature.moth.start.hashCode();
                    playerCreature.rng.stateB = Noise.IntPointHash.hashAll(screenX, screenY, ~playerCreature.rng.stateA);
                    playerToCursor.findPathPreScanned(toCursor, cursor);
                    playerCreature.rng.setState(a, b);
//                    // findPathPreScanned includes the current cell (goal) by default, which is helpful when
//                    // you're finding a path to a monster or loot, and want to bump into it, but here can be
//                    // confusing because you would "move into yourself" as your first move without this.
//                    if (!toCursor.isEmpty()) {
//                        toCursor.remove(0);
//                    }
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(input);
    }
    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     * In a fully-fledged game, this would not be organized like this, but this is a one-file demo.
     * @param start
     * @param end
     */
    private void move(final Coord start, final Coord end) {
        if(creatures.containsKey(end))
        {
            awaitedMoves.clear();
            toCursor.clear();
            return;
        }
        if (onGrid(end.x, end.y) && dl.bareDungeon[end.x][end.y] != '#')
        {
            creatures.alterCarefully(playerCreature.moth.end, end);
            playerCreature.moth.start = start;
            playerCreature.moth.end = end;
            playerCreature.moth.alpha = 0f;
            mode = ANIMATE;
            animationStart = TimeUtils.millis();
            dl.lighting.moveLight(start, end);
            // calculates field of vision around the player again, in a circle of radius 9.0 .
            //FOV.reuseFOV(resistance, visible, end.x, end.y, 9.0, Radius.CIRCLE);
            dl.lighting.calculateFOV(end);
            // This is just like the constructor used earlier, but affects an existing GreasedRegion without making
            // a new one just for this movement.
            //blockage.refill(visible, 0.0);
            //seen.or(currentlySeen.remake(blockage.not()));
            //blockage.fringe8way();
            // By calling LineKit.pruneLines(), we adjust prunedDungeon to hold a variant on lineDungeon that removes any
            // line segments that haven't ever been visible. This is called again whenever seen changes.
            //dl.prune(seen);
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putMap()
    {
        final float time = TimeUtils.timeSinceMillis(startTime) * 0.001f;
        dl.lighting.updateAll();
        dl.lighting.draw(dl.backgrounds);
        Animation<TextureAtlas.AtlasRegion> decoration;
        Creature creature;
        Coord c;
        
        for (int i = 0; i < bigWidth; i++) {
            for (int j = 0; j < bigHeight; j++) {
                c = Coord.get(i, j);
                switch (dl.prunedDungeon[i][j])
                    {
                        case '"':
                        case '~':
                            dl.lighting.currentBackgrounds[i][j] =
                                    toCursor.contains(c)
                                            ? NumberTools.setSelectedByte(dl.lighting.currentBackgrounds[i][j], 0, (byte)-26)
                                            : NumberTools.setSelectedByte(dl.lighting.currentBackgrounds[i][j], 0, (byte)(//dl.lighting.colorLighting[0][i][j] * 140
                                            110 + FastNoise.instance.getConfiguredNoise(i * 2f, j * 2f, time * 3.5f) * 60));
                            break;
                        case ',':
                            dl.lighting.currentBackgrounds[i][j] = 
                                    toCursor.contains(c)
                                            ? NumberTools.setSelectedByte(dl.lighting.currentBackgrounds[i][j], 0, (byte)-26)
                                            : NumberTools.setSelectedByte(dl.lighting.currentBackgrounds[i][j], 0, (byte)(//dl.lighting.colorLighting[0][i][j] * 130
                                            140 + FastNoise.instance.getConfiguredNoise(i * 2.25f, j * 2.25f, time * 5.5f) * 50));
                                    break;
                        default:
                            if(toCursor.contains(c))
                                dl.lighting.currentBackgrounds[i][j] = NumberTools.setSelectedByte(dl.lighting.currentBackgrounds[i][j], 0, (byte)-26);
                    }

            }
        }
        for (int i = 0; i < bigWidth; i++) {
            for (int j = 0; j < bigHeight; j++) {
                if(visible[i][j] > 0.0) {
                    c = Coord.get(i, j);
//                    pos.set(i * cellWidth, j * cellHeight, 0f);
//                    batch.setPackedColor(toCursor.contains(Coord.get(i, j))
//                            ? FLOAT_WHITE
//                            : SColor.lerpFloatColors(FLOAT_GRAY, FLOAT_LIGHTING, (float)visible[i][j] * 0.75f + 0.25f));
//                    batch.setPackedColor(Visuals.lerpFloatColors(dl.backgrounds[i][j], batch.getPackedColor(), 0.6f));
                    batch.setPackedColor(dl.lighting.currentBackgrounds[i][j]);
                    //batch.draw(solid, pos.x, pos.y);
//                    batch.setPackedColor(SColor.lerpFloatColors(colors[i][j], FLOAT_LIGHTING, (float)visible[i][j] * 0.75f + 0.25f));
                    batch.draw(charMapping.get(dl.prunedDungeon[i][j], solid).getKeyFrame(time), i, j, 1f, 1f);
                    if((decoration = decorations.get(c)) != null)
                    {
                        batch.draw(decoration.getKeyFrame(time), i, j, 1f, 1f);
                    }
                }
//                else if(seen.contains(i, j)) {
//                    batch.setPackedColor(Visuals.FLOAT_GRAY);
//                    batch.draw(charMapping.get(dl.prunedDungeon[i][j], solid).getKeyFrame(time), i, j, 1f, 1f);
//                    if((decoration = decorations.get(Coord.get(i, j))) != null)
//                    {
//                        batch.draw(decoration.getKeyFrame(time), i, j, 1f, 1f);
//                    }
//                }
            }
        }
//        for (int i = 0; i < monsters.size(); i++) {
//            monsters.getAt(i).draw(batch);
//        }
        for (int i = 0; i < creatures.size(); i++) {
            Coord pos = creatures.keyAt(i);
            if(visible[pos.x][pos.y] > 0) {
                creature = creatures.getAt(i);
                batch.setPackedColor(creature.moth.color);
                batch.draw(creature.moth.animate(time), creature.moth.getX(), creature.moth.getY(), 1f, 1f);
            }
        }
//        batch.setPackedColor(playerCreature.moth.color);
//        batch.draw(playerCreature.moth.animate(time), playerCreature.moth.getX(), playerCreature.moth.getY(), 1f, 1f);
        font.setColor(Color.WHITE);
        font.draw(batch, 
                horoscope + "\nRunning at " + StringKit.padLeftStrict(Gdx.graphics.getFramesPerSecond() + " FPS???", ' ', 11),
                        (playerCreature.moth.getX() - mainViewport.getWorldWidth() * 0.25f),
                (playerCreature.moth.getY() + mainViewport.getWorldHeight() * 0.375f), mainViewport.getWorldWidth() * 0.5f,
                Align.center, true);

        //Visuals.getYCwCmSat(140, 128, 128, 0)
        
//        final String trogdor = "TROGDOR! TROGDOR! Trogdor was a man! Or maybe he was a... Dragon-Man!";
//        font.getCache().clear();
//        font.getCache().addText(trogdor, 24f, 37, 20, Align.center, true);
//        font.getCache().setColors(Color.FOREST, 0, trogdor.length());
//        font.getCache().draw(batch);
//        Gdx.graphics.setTitle(horoscope);
//        Gdx.graphics.setTitle(Gdx.graphics.getFramesPerSecond() + " FPS");
    }

    @Override
    public void render () {
        //sync.sync(Gdx.graphics.getDisplayMode().refreshRate);
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = playerCreature.moth.getX();
        camera.position.y =  playerCreature.moth.getY();
        camera.update();

        mainViewport.apply(false);
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        palette.bind();
        batch.begin();
        shader.setUniformi("u_palette", 1);
//        shader.setUniformf("u_mul", mul);
//        shader.setUniformf("u_add", add);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        putMap();
        if(mode == NPC) {
            float t = TimeUtils.timeSinceMillis(animationStart) * 0.006f;
            for (int i = 1; i < creatures.size(); i++) {
                creatures.getAt(i).moth.alpha = t;
            }
            if(t >= 1f)
            {
                mode = SELECT;
                if(!awaitedMoves.isEmpty()) {
//                    if(mode == SELECT) { // || playerCreature.moth.alpha >= 1f
//                        // this doesn't check for input, but instead processes and removes Coords from awaitedMoves.
                        Coord m = awaitedMoves.remove(0);
                        if (!toCursor.isEmpty())
                            toCursor.remove(0);
                        move(playerCreature.moth.start, m);
//                    }
//                    else {
//                        playerCreature.moth.alpha = TimeUtils.timeSinceMillis(animationStart) * 0.006f;
//                    }
                }
            }
        }
        else if(mode == SELECT && !awaitedMoves.isEmpty())
        {
            Coord m = awaitedMoves.remove(0);
            if (!toCursor.isEmpty())
                toCursor.remove(0);
            move(playerCreature.moth.start, m);
        }
        else if(mode == ANIMATE) {
            playerCreature.moth.alpha = TimeUtils.timeSinceMillis(animationStart) * 0.006f;
            if(playerCreature.moth.alpha >= 1f)
            {
                mode = NPC;
                animationStart = TimeUtils.millis();
                for (int i = 1; i < creatures.size(); i++) {
                    creatures.act(creatures.keyAt(i));
                }

                // this only happens if we just removed the last Coord from awaitedMoves, and it's only then that we need to
                // re-calculate the distances from all cells to the player. We don't need to calculate this information on
                // each part of a many-cell move (just the end), nor do we need to calculate it whenever the mouse moves.
//                if (awaitedMoves.isEmpty()) {
                    // the next two lines remove any lingering data needed for earlier paths
                    playerToCursor.clearGoals();
                    playerToCursor.resetMap();
                    // the next line marks the player as a "goal" cell, which seems counter-intuitive, but it works because all
                    // cells will try to find the distance between themselves and the nearest goal, and once this is found, the
                    // distances don't change as long as the goals don't change. Since the mouse will move and new paths will be
                    // found, but the player doesn't move until a cell is clicked, the "goal" is the non-changing cell (the
                    // player's position), and the "target" of a pathfinding method like DijkstraMap.findPathPreScanned() is the
                    // currently-moused-over cell, which we only need to set where the mouse is being handled.
                    playerToCursor.setGoal(playerCreature.moth.end);
                    // DijkstraMap.partialScan only finds the distance to get to a cell if that distance is less than some limit,
                    // which is 13 here. It also won't try to find distances through an impassable cell, which here is the blockage
                    // GreasedRegion that contains the cells just past the edge of the player's FOV area.
                    impassable.clear();
                    //impassable.addAll(blockage);
                    impassable.addAll(creatures.keySet());
                    playerToCursor.partialScan(gridWidth + gridHeight, impassable);
                    
                    horoscope = anReplacer.replace(zodiacShuffler.next() + phraseShuffler.next() + meaningShuffler.next().replace("@", zodiacShuffler.next()));
//                }

            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        mainViewport.update(width, height, false);
        mainViewport.setScreenBounds(0, 0, width, height);
    }

    private boolean onGrid(int gridX, int gridY)
    {
        return gridX >= 0 && gridX < bigWidth && gridY >= 0 && gridY < bigHeight;
    }

}
