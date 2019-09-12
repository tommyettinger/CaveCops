package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Scaling;
import squidpony.ArrayTools;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Measurement;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.FloatFilters;
import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.GreasedRegion;

import java.util.ArrayList;

import static com.badlogic.gdx.Input.Keys.*;
import static squidpony.squidgrid.gui.gdx.SColor.FLOAT_WHITE;

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
    // FilterBatch is almost the same as SpriteBatch, but is a bit faster with SquidLib and allows color filtering
    private FilterBatch batch;
    private PixelPerfectViewport mainViewport;
    private Camera camera;

    // a type of random number generator, see below
    private GWTRNG rng;

    // Stores all images we use here efficiently, as well as the font image 
    private TextureAtlas atlas;
    // This maps chars, such as '#', to specific images, such as a pillar.
    private IntMap<TextureAtlas.AtlasRegion> charMapping;
    
    private BitmapFont font;
    
    // generates a dungeon as a 2D char array; can also fill some simple features into the dungeon.
    private DungeonGenerator dungeonGen;
    // decoDungeon stores the dungeon map with features like grass and water, if present, as chars like '"' and '~'.
    // bareDungeon stores the dungeon map with just walls as '#' and anything not a wall as '.'.
    // Both of the above maps use '#' for walls, and the next two use box-drawing characters instead.
    // lineDungeon stores the whole map the same as decoDungeon except for walls, which are box-drawing characters here.
    // prunedDungeon takes lineDungeon and adjusts it so unseen segments of wall (represented by box-drawing characters)
    //   are removed from rendering; unlike the others, it is frequently changed.
    private char[][] decoDungeon, bareDungeon, lineDungeon, prunedDungeon;
    private float[][] colors, bgColors;

    //Here, gridHeight refers to the total number of rows to be displayed on the screen.
    //We're displaying 25 rows of dungeon, then 7 more rows of text generation to show some tricks with language.
    //gridHeight is 25 because that variable will be used for generating the dungeon (the actual size of the dungeon
    //will be triple gridWidth and triple gridHeight), and determines how much off the dungeon is visible at any time.
    //The bonusHeight is the number of additional rows that aren't handled like the dungeon rows and are shown in a
    //separate area; here we use them for translations. The gridWidth is 90, which means we show 90 grid spaces
    //across the whole screen, but the actual dungeon is larger. The cellWidth and cellHeight are 10 and 20, which will
    //match the starting dimensions of a cell in pixels, but won't be stuck at that value because we use a "Stretchable"
    //font, and so the cells can change size (they don't need to be scaled by equal amounts, either). While gridWidth
    //and gridHeight are measured in spaces on the grid, cellWidth and cellHeight are the initial pixel dimensions of
    //one cell; resizing the window can make the units cellWidth and cellHeight use smaller or larger than a pixel.

    /** In number of cells */
    private static final int gridWidth = 64;
    /** In number of cells */
    private static final int gridHeight = 32;

    /** In number of cells */
    private static final int bigWidth = gridWidth * 2;
    /** In number of cells */
    private static final int bigHeight = gridHeight * 2;

    /** The pixel width of a cell */
    private static final int cellWidth = 16;
    /** The pixel height of a cell */
    private static final int cellHeight = 16;
    private TextureAtlas.AtlasRegion solid;
    private Sprite playerSprite;
    private static final float
            FLOAT_BLOOD = -0x1.564f86p125F,  // same result as SColor.PURE_CRIMSON.toFloatBits()
            FLOAT_LIGHTING = SColor.floatGetHSV(0.17f, 0.12f, 0.8f, 1f),//-0x1.cff1fep126F, // same result as SColor.COSMIC_LATTE.toFloatBits()
            FLOAT_GRAY = -0x1.7e7e7ep125F; // same result as SColor.CW_GRAY_BLACK.toFloatBits()

    private boolean onGrid(int screenX, int screenY)
    {
        return screenX >= 0 && screenX < bigWidth && screenY >= 0 && screenY < bigHeight;
    }
    private InputProcessor input;

    private Color bgColor;
    private DijkstraMap playerToCursor;
    private Coord cursor, player;
    private ArrayList<Coord> toCursor;
    private ArrayList<Coord> awaitedMoves;

    private Vector2 screenPosition;
    private Vector3 pos = new Vector3();


    private double[][] resistance;
    private double[][] visible;
    // Here, we use a GreasedRegion to store all floors that the player can walk on, a small rim of cells just beyond
    // the player's vision that blocks pathfinding to areas we can't see a path to, and we also store all cells that we
    // have seen in the past in a GreasedRegion (in most roguelikes, there would be one of these per dungeon floor).
    private GreasedRegion floors, blockage, seen, currentlySeen;
    // This filters colors in a way we adjust over time, producing a sort of hue shift effect.
    // It can also be used to over- or under-saturate colors, change their brightness, or any combination of these. 
    private FloatFilters.YCwCmFilter warmMildFilter;
    @Override
    public void create () {
        // gotta have a random number generator. We can seed an RNG with any long we want, or even a String.
        // if the seed is identical between two runs, any random factors will also be identical (until user input may
        // cause the usage of an RNG to change). You can randomize the dungeon and several other initial settings by
        // just removing the String seed, making the line "rng = new GWTRNG();" . Keeping the seed as a default allows
        // changes to be more easily reproducible, and using a fixed seed is strongly recommended for tests. 

        // SquidLib has many methods that expect an IRNG instance, and there's several classes to choose from.
        // In this program we'll use GWTRNG, which will behave better on the HTML target than other generators.
        rng = new GWTRNG(1337);
        // YCwCmFilter multiplies the brightness (Y), warmth (Cw), and mildness (Cm) of a color 
        warmMildFilter = new FloatFilters.YCwCmFilter(0.875f, 0.6f, 0.6f);

        // FilterBatch is exactly like libGDX' SpriteBatch, except it is a fair bit faster when the Batch color is set
        // often (which is always true for SquidLib's text-based display), and it allows a FloatFilter to be optionally
        // set that can adjust colors in various ways. The FloatFilter here, a YCwCmFilter, can have its adjustments to
        // brightness (Y, also called luma), warmth (blue/green vs. red/yellow) and mildness (blue/red vs. green/yellow)
        // changed at runtime, and the putMap() method does this. This can be very powerful; you might increase the
        // warmth of all colors (additively) if the player is on fire, for instance.
        batch = new FilterBatch(warmMildFilter);
        mainViewport = new PixelPerfectViewport(Scaling.fill, gridWidth * cellWidth, gridHeight * cellHeight);
        mainViewport.setScreenBounds(0, 0, gridWidth * cellWidth, gridHeight * cellHeight);
        camera = mainViewport.getCamera();
        camera.update();

        atlas = new TextureAtlas("Dawnlike.atlas");
        font = new BitmapFont(Gdx.files.internal("font.fnt"), atlas.findRegion("font"));
        font.getData().scale(2f);

        charMapping = new IntMap<>(64);
        solid = atlas.findRegion("totally lit tile");
        playerSprite = atlas.createSprite("keystone kop");
        charMapping.put('.', solid);
        charMapping.put(',', atlas.findRegion("shallow water tile"      ));
        charMapping.put('~', atlas.findRegion("deep water tile"      ));
        charMapping.put('"', atlas.findRegion("dusk grass floor c"      ));
        charMapping.put('#', atlas.findRegion("lit rock wall center"     ));
        charMapping.put('+', atlas.findRegion("closed wooden door front"));
        charMapping.put('/', atlas.findRegion("closed wooden door side"  ));
        charMapping.put('└', atlas.findRegion("lit rock wall right up"            ));
        charMapping.put('┌', atlas.findRegion("lit rock wall right down"            ));
        charMapping.put('┬', atlas.findRegion("lit rock wall left right down"           ));
        charMapping.put('┴', atlas.findRegion("lit rock wall left right up"           ));
        charMapping.put('─', atlas.findRegion("lit rock wall left right"            ));
        charMapping.put('│', atlas.findRegion("lit rock wall up down"            ));
        charMapping.put('├', atlas.findRegion("lit rock wall right up down"           ));
        charMapping.put('┼', atlas.findRegion("lit rock wall left right up down"          ));
        charMapping.put('┤', atlas.findRegion("lit rock wall left up down"           ));
        charMapping.put('┐', atlas.findRegion("lit rock wall left down"            ));
        charMapping.put('┘', atlas.findRegion("lit rock wall left up"            ));
        
        //This uses the seeded RNG we made earlier to build a procedural dungeon using a method that takes rectangular
        //sections of pre-drawn dungeon and drops them into place in a tiling pattern. It makes good winding dungeons
        //with rooms by default, but in the later call to dungeonGen.generate(), you can use a TilesetType such as
        //TilesetType.ROUND_ROOMS_DIAGONAL_CORRIDORS or TilesetType.CAVES_LIMIT_CONNECTIVITY to change the sections that
        //this will use, or just pass in a full 2D char array produced from some other generator, such as
        //SerpentMapGenerator, OrganicMapGenerator, or DenseRoomMapGenerator.
        dungeonGen = new DungeonGenerator(bigWidth, bigHeight, rng);
        //uncomment this next line to randomly add water to the dungeon in pools.
        //dungeonGen.addWater(15);
        //decoDungeon is given the dungeon with any decorations we specified. (Here, we didn't, unless you chose to add
        //water to the dungeon. In that case, decoDungeon will have different contents than bareDungeon, next.)
        decoDungeon = dungeonGen.generate();
        //getBareDungeon provides the simplest representation of the generated dungeon -- '#' for walls, '.' for floors.
        bareDungeon = dungeonGen.getBareDungeon();
        //When we draw, we may want to use a nicer representation of walls. DungeonUtility has lots of useful methods
        //for modifying char[][] dungeon grids, and this one takes each '#' and replaces it with a box-drawing char.
        //The end result looks something like this, for a smaller 60x30 map:
        //
        // ┌───┐┌──────┬──────┐┌──┬─────┐   ┌──┐    ┌──────────┬─────┐
        // │...││......│......└┘..│.....│   │..├───┐│..........│.....└┐
        // │...││......│..........├──┐..├───┤..│...└┴────......├┐.....│
        // │...││.................│┌─┘..│...│..│...............││.....│
        // │...││...........┌─────┘│....│...│..│...........┌───┴┴───..│
        // │...│└─┐....┌───┬┘      │........│..│......─────┤..........│
        // │...└─┐│....│...│       │.......................│..........│
        // │.....││........└─┐     │....│..................│.....┌────┘
        // │.....││..........│     │....├─┬───────┬─┐......│.....│
        // └┬──..└┼───┐......│   ┌─┴─..┌┘ │.......│ │.....┌┴──┐..│
        //  │.....│  ┌┴─..───┴───┘.....└┐ │.......│┌┘.....└─┐ │..│
        //  │.....└──┘..................└─┤.......││........│ │..│
        //  │.............................│.......├┘........│ │..│
        //  │.............┌──────┐........│.......│...─┐....│ │..│
        //  │...........┌─┘      └──┐.....│..─────┘....│....│ │..│
        // ┌┴─────......└─┐      ┌──┘..................│..──┴─┘..└─┐
        // │..............└──────┘.....................│...........│
        // │............................┌─┐.......│....│...........│
        // │..│..│..┌┐..................│ │.......├────┤..──┬───┐..│
        // │..│..│..│└┬──..─┬───┐......┌┘ └┐.....┌┘┌───┤....│   │..│
        // │..├──┤..│ │.....│   │......├───┘.....│ │...│....│┌──┘..└──┐
        // │..│┌─┘..└┐└┬─..─┤   │......│.........└─┘...│....││........│
        // │..││.....│ │....│   │......│...............│....││........│
        // │..││.....│ │....│   │......│..┌──┐.........├────┘│..│.....│
        // ├──┴┤...│.└─┴─..┌┘   └┐....┌┤..│  │.....│...└─────┘..│.....│
        // │...│...│.......└─────┴─..─┴┘..├──┘.....│............└─────┤
        // │...│...│......................│........│..................│
        // │.......├───┐..................│.......┌┤.......┌─┐........│
        // │.......│   └──┐..┌────┐..┌────┤..┌────┘│.......│ │..┌──┐..│
        // └───────┘      └──┘    └──┘    └──┘     └───────┘ └──┘  └──┘
        //this is also good to compare against if the map looks incorrect, and you need an example of a correct map when
        //no parameters are given to generate().
        lineDungeon = DungeonUtility.hashesToLines(decoDungeon);

        resistance = DungeonUtility.generateResistances(decoDungeon);
        visible = new double[bigWidth][bigHeight];

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
        floors = new GreasedRegion(bareDungeon, '.');
        //player is, here, just a Coord that stores his position. In a real game, you would probably have a class for
        //creatures, and possibly a subclass for the player. The singleRandom() method on GreasedRegion finds one Coord
        // in that region that is "on," or -1,-1 if there are no such cells. It takes an RNG object as a parameter, and
        // if you gave a seed to the RNG constructor, then the cell this chooses will be reliable for testing. If you
        // don't seed the RNG, any valid cell should be possible.
        player = floors.singleRandom(rng);
        playerSprite.setPosition(player.x * 16, player.y * 16);
        // Uses shadowcasting FOV and reuses the visible array without creating new arrays constantly.
        FOV.reuseFOV(resistance, visible, player.x, player.y, 9.0, Radius.CIRCLE);//, (System.currentTimeMillis() & 0xFFFF) * 0x1p-4, 60.0);
        
        // 0.01 is the upper bound (inclusive), so any Coord in visible that is more well-lit than 0.01 will _not_ be in
        // the blockage Collection, but anything 0.01 or less will be in it. This lets us use blockage to prevent access
        // to cells we can't see from the start of the move.
        blockage = new GreasedRegion(visible, 0.0);
        seen = blockage.not().copy();
        currentlySeen = seen.copy();
        blockage.fringe8way();
        // prunedDungeon starts with the full lineDungeon, which includes features like water and grass but also stores
        // all walls as box-drawing characters. The issue with using lineDungeon as-is is that a character like '┬' may
        // be used because there are walls to the east, west, and south of it, even when the player is to the north of
        // that cell and so has never seen the southern connecting wall, and would have no reason to know it is there.
        // By calling LineKit.pruneLines(), we adjust prunedDungeon to hold a variant on lineDungeon that removes any
        // line segments that haven't ever been visible. This is called again whenever seen changes. 
        prunedDungeon = ArrayTools.copy(lineDungeon);
        // We call pruneLines with an optional parameter here, LineKit.lightAlt, which will allow prunedDungeon to use
        // the half-line chars "╴╵╶╷". These chars aren't supported by all fonts, but they are by the one we use here.
        // The default is to use LineKit.light , which will replace '╴' and '╶' with '─' and '╷' and '╵' with '│'.
        LineKit.pruneLines(lineDungeon, seen, LineKit.lightAlt, prunedDungeon);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(200);
        //When a path is confirmed by clicking, we draw from this List to find which cell is next to move into.
        awaitedMoves = new ArrayList<>(200);
        //DijkstraMap is the pathfinding swiss-army knife we use here to find a path to the latest cursor position.
        //DijkstraMap.Measurement is an enum that determines the possibility or preference to enter diagonals. Here, the
        // MANHATTAN value is used, which means 4-way movement only, no diagonals possible. Alternatives are CHEBYSHEV,
        // which allows 8 directions of movement at the same cost for all directions, and EUCLIDEAN, which allows 8
        // directions, but will prefer orthogonal moves unless diagonal ones are clearly closer "as the crow flies."
        playerToCursor = new DijkstraMap(decoDungeon, Measurement.MANHATTAN);
        //These next two lines mark the player as something we want paths to go to or from, and get the distances to the
        // player from all walkable cells in the dungeon.
        playerToCursor.setGoal(player);
        playerToCursor.setGoal(player);
        // DijkstraMap.partialScan only finds the distance to get to a cell if that distance is less than some limit,
        // which is 13 here. It also won't try to find distances through an impassable cell, which here is the blockage
        // GreasedRegion that contains the cells just past the edge of the player's FOV area.
        playerToCursor.partialScan(13, blockage);

        //The next three lines set the background color for anything we don't draw on, but also create 2D arrays of the
        //same size as decoDungeon that store the colors for the foregrounds and backgrounds of each cell as packed
        //floats (a format SparseLayers can use throughout its API), using the colors for the cell with the same x and
        //y. By changing an item in SColor.LIMITED_PALETTE, we also change the color assigned by MapUtility to floors.
        bgColor = SColor.DB_INK;
        SColor.LIMITED_PALETTE[3] = SColor.DB_GRAPHITE;
        colors = MapUtility.generateDefaultColorsFloat(decoDungeon);
        bgColors = MapUtility.generateDefaultBGColorsFloat(decoDungeon);
//        for (int x = 0; x < bigWidth; x++) {
//            for (int y = 0; y < bigHeight; y++) {
//                colors[x][y] = f(colors[x][y]);
//                bgColors[x][y] = f(bgColors[x][y]);
//            }
//        }
        
        input = new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode)
                {
                    case UP:
                    case 'w':
                    case 'W':
                    case NUMPAD_8:
                        toCursor.clear();
                        //+1 is up on the screen
                        awaitedMoves.add(player.translate(0, 1));
                        break;
                    case DOWN:
                    case 's':
                    case 'S':
                    case NUMPAD_2:
                        toCursor.clear();
                        //-1 is down on the screen
                        awaitedMoves.add(player.translate(0, -1));
                        break;
                    case LEFT:
                    case 'a':
                    case 'A':
                    case NUMPAD_4:
                        toCursor.clear();
                        awaitedMoves.add(player.translate(-1, 0));
                        break;
                    case RIGHT:
                    case 'd':
                    case 'D':
                    case NUMPAD_6:
                        toCursor.clear();
                        awaitedMoves.add(player.translate(1, 0));
                        break;
                    case NUMPAD_1:
                        toCursor.clear();
                        awaitedMoves.add(player.translate(-1, -1));
                        break;
                    case NUMPAD_3:
                        toCursor.clear();
                        awaitedMoves.add(player.translate(1, -1));
                        break;
                    case NUMPAD_7:
                        toCursor.clear();
                        awaitedMoves.add(player.translate(-1, 1));
                        break;
                    case NUMPAD_9:
                        toCursor.clear();
                        awaitedMoves.add(player.translate(1, 1));
                        break;
                    case '.':
                    case NUMPAD_5:
                        toCursor.clear();
                        awaitedMoves.add(player);
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
                pos.set(screenX, screenY, 0f);
                mainViewport.unproject(pos);
                if (onGrid(MathUtils.floor(pos.x) >> 4, MathUtils.floor(pos.y) >> 4)) {
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
                if(!awaitedMoves.isEmpty())
                    return false;
                pos.set(screenX, screenY, 0f);
                mainViewport.unproject(pos);
                if (onGrid(screenX = MathUtils.floor(pos.x) >> 4, screenY = MathUtils.floor(pos.y) >> 4)) {
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
                    toCursor = playerToCursor.findPathPreScanned(cursor);
                    // findPathPreScanned includes the current cell (goal) by default, which is helpful when
                    // you're finding a path to a monster or loot, and want to bump into it, but here can be
                    // confusing because you would "move into yourself" as your first move without this.
                    if (!toCursor.isEmpty()) {
                        toCursor.remove(0);
                    }
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(input);

        screenPosition = new Vector2(cellWidth, cellHeight);
    }
    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     * In a fully-fledged game, this would not be organized like this, but this is a one-file demo.
     * @param xmod
     * @param ymod
     */
    private void move(final int xmod, final int ymod) {
        int newX = player.x + xmod, newY = player.y + ymod;
        if (newX >= 0 && newY >= 0 && newX < bigWidth && newY < bigHeight
                && bareDungeon[newX][newY] != '#')
        {
            playerSprite.translate(xmod * 16, ymod * 16);
            // this just moves the grid position of the player as it is internally tracked.
            player = player.translate(xmod, ymod);
            // calculates field of vision around the player again, in a circle of radius 9.0 .
            FOV.reuseFOV(resistance, visible, player.x, player.y, 9.0, Radius.CIRCLE);
            // This is just like the constructor used earlier, but affects an existing GreasedRegion without making
            // a new one just for this movement.
            blockage.refill(visible, 0.0);
            seen.or(currentlySeen.remake(blockage.not()));
            blockage.fringe8way();
            // By calling LineKit.pruneLines(), we adjust prunedDungeon to hold a variant on lineDungeon that removes any
            // line segments that haven't ever been visible. This is called again whenever seen changes.
            LineKit.pruneLines(lineDungeon, seen, LineKit.lightAlt, prunedDungeon);
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putMap()
    {
        for (int i = 0; i < bigWidth; i++) {
            for (int j = 0; j < bigHeight; j++) {
                if(visible[i][j] > 0.0) {
                    pos.set(i * cellWidth, j * cellHeight, 0f);
                    batch.setPackedColor(toCursor.contains(Coord.get(i, j))
                            ? SColor.lerpFloatColors(bgColors[i][j], FLOAT_WHITE, 0.9f)
                            : SColor.lerpFloatColors(bgColors[i][j], FLOAT_LIGHTING, (float)visible[i][j] * 0.75f + 0.25f));
                    //batch.draw(solid, pos.x, pos.y);                     
                    batch.setPackedColor(SColor.lerpFloatColors(colors[i][j], FLOAT_LIGHTING, (float)visible[i][j] * 0.75f + 0.25f));
                    batch.draw(charMapping.get(lineDungeon[i][j], solid), pos.x, pos.y);
                } else if(seen.contains(i, j)) {
                    pos.set(i * cellWidth, j * cellHeight, 0f);
                    batch.setPackedColor(SColor.lerpFloatColors(bgColors[i][j], FLOAT_GRAY, 0.7f));
                    //batch.draw(solid, pos.x, pos.y);
//                    if ((monster = monsters.get(Coord.get(i, j))) != null)
//                        monster.setAlpha(0f);
                    batch.setPackedColor(SColor.lerpFloatColors(colors[i][j], FLOAT_GRAY, 0.7f));
                    batch.draw(charMapping.get(lineDungeon[i][j], solid), pos.x, pos.y);
                }
            }
        }
//        for (int i = 0; i < monsters.size(); i++) {
//            monsters.getAt(i).draw(batch);
//        }
        playerSprite.draw(batch);
        Gdx.graphics.setTitle(Gdx.graphics.getFramesPerSecond() + " FPS");
    }
    @Override
    public void render () {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = playerSprite.getX();
        camera.position.y =  playerSprite.getY();
        camera.update();

        mainViewport.apply(false);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        putMap();
        // if the user clicked, we have a list of moves to perform.
        if(!awaitedMoves.isEmpty()) {
            // this doesn't check for input, but instead processes and removes Coords from awaitedMoves.
            Coord m = awaitedMoves.remove(0);
            if (!toCursor.isEmpty())
                toCursor.remove(0);
            move(m.x - player.x, m.y - player.y);
            // this only happens if we just removed the last Coord from awaitedMoves, and it's only then that we need to
            // re-calculate the distances from all cells to the player. We don't need to calculate this information on
            // each part of a many-cell move (just the end), nor do we need to calculate it whenever the mouse moves.
            if (awaitedMoves.isEmpty()) {
                // the next two lines remove any lingering data needed for earlier paths
                playerToCursor.clearGoals();
                playerToCursor.resetMap();
                // the next line marks the player as a "goal" cell, which seems counter-intuitive, but it works because all
                // cells will try to find the distance between themselves and the nearest goal, and once this is found, the
                // distances don't change as long as the goals don't change. Since the mouse will move and new paths will be
                // found, but the player doesn't move until a cell is clicked, the "goal" is the non-changing cell (the
                // player's position), and the "target" of a pathfinding method like DijkstraMap.findPathPreScanned() is the
                // currently-moused-over cell, which we only need to set where the mouse is being handled.
                playerToCursor.setGoal(player);
                // DijkstraMap.partialScan only finds the distance to get to a cell if that distance is less than some limit,
                // which is 13 here. It also won't try to find distances through an impassable cell, which here is the blockage
                // GreasedRegion that contains the cells just past the edge of the player's FOV area.
                playerToCursor.partialScan(13, blockage);
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
}
