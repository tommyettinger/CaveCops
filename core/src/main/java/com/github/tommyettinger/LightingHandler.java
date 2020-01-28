package com.github.tommyettinger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.ArrayTools;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.OrderedMap;

import java.io.Serializable;

import static com.github.tommyettinger.Visuals.FLOAT_NEUTRAL;

/**
 * A convenience class that makes dealing with multiple colored light sources easier.
 * All fields are public and documented to encourage their use alongside the API methods. The typical usage case for
 * this class is when a game has complex lighting needs that should be consolidated into one LightingHandler per level,
 * where a level corresponds to a {@code char[][]}. After constructing a LightingHandler with the resistances for a
 * level, you should add all light sources with their positions, either using {@link #addLight(int, int, Radiance)} or
 * by directly putting keys and values into {@link #lights}. Then you can calculate the visible cells once lighting is
 * considered (which may include distant lit cells with unseen but unobstructing cells between the viewer and the light)
 * using {@link #calculateFOV(Coord)}, which should be called every time the viewer moves. You can update the flicker
 * and strobe effects on all Radiance objects, which is typically done every frame, using {@link #update()} or
 * {@link #updateAll()} (updateAll() is for when there is no viewer), and once that update() call has been made you can
 * call {@link #draw(float[][])} to change a 2D float array that holds packed
 * float colors (which may be used in some custom setup). To place user-interface lighting effects that don't affect the
 * actual FOV of creatures in the game, you can use {@link #updateUI(Coord, Radiance)}, which is called after
 * {@link #update()} but before {@link #draw(float[][])}.
 * <br>
 * Created by Tommy Ettinger on 11/2/2018.
 */
public class LightingHandler implements Serializable {
    private static final long serialVersionUID = 0L;

    /**
     * How light should spread; usually {@link Radius#CIRCLE} unless gameplay reasons need it to be SQUARE or DIAMOND.
     */
    public Radius radiusStrategy;
    /**
     * The 2D array of light-resistance values from 0.0 to 1.0 for each cell on the map, as produced by
     * {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}.
     */
    public double[][] resistances;
    /**
     * What the "viewer" (as passed to {@link #calculateFOV(Coord)}) can see either nearby without light or because an
     * area in line-of-sight has light in it. Edited by {@link #calculateFOV(Coord)} and {@link  #update()}, but
     * not {@link #updateUI(Coord, Radiance)} (which is meant for effects that are purely user-interface).
     */
    public double[][] fovResult;
    /**
     * A 2D array of doubles that are either 0.0 if a cell has an obstruction between it and the viewer, or greater than
     * 0.0 otherwise.
     */
    public double[][] losResult;
    /**
     * Temporary storage array used for calculations involving {@link #fovResult}; it sometimes may make sense for other
     * code to use this as temporary storage as well.
     */
    public double[][] tempFOV;
    /**
     * A pair of 2D float arrays with different usages; {@code colorLighting[0]} is a 2D array that stores the strength
     * of light in each cell, and {@code colorLighting[1]} is a 2D array that stores the color of light in each cell, as
     * a packed float color. Both 2D arrays are the size of the map, as defined by {@link #resistances} initially and
     * later available in {@link #width} and {@link #height}.
     */
    public float[][][] colorLighting;
    /**
     * Temporary storage array used for calculations involving {@link #colorLighting}; it sometimes may make sense for
     * other code to use this as temporary storage as well.
     */
    public float[][][] tempColorLighting;
    /**
     * Width of the 2D arrays used in this, as obtained from {@link #resistances}.
     */
    public int width;
    /**
     * Height of the 2D arrays used in this, as obtained from {@link #resistances}.
     */
    public int height;
    /**
     * The packed float color to mix background cells with when a cell has lighting and is within line-of-sight, but has
     * no background color to start with (its color is {@link Visuals#FLOAT_BLACK}).
     */
    public float backgroundColor;
    /**
     * How far the viewer can see without light; defaults to 4.0 cells, and you are encouraged to change this member
     * field if the vision range changes after construction.
     */
    public double viewerRange;
    /**
     * A mapping from positions as {@link Coord} objects to {@link Radiance} objects that describe the color, lighting
     * radius, and changes over time of any in-game lights that should be shown on the map and change FOV. You can edit
     * this manually or by using {@link #moveLight(int, int, int, int)}, {@link #addLight(int, int, Radiance)}, and
     * {@link #removeLight(int, int)}.
     */
    public OrderedMap<Coord, Radiance> lights;

    /**
     * A GreasedRegion that stores any cells that are in line-of-sight or are close enough to a cell in line-of-sight to
     * potentially cast light into such a cell. Depends on the highest {@link Radiance#range} in {@link #lights}.
     */
    public GreasedRegion noticeable;
    /**
     * Background packed colors in YCwCm+Sat format; written to by {@link #draw(float[][])}
     */
    public float[][] currentBackgrounds;
    
    
    /**
     * Unlikely to be used except during serialization; makes a LightingHandler for a 20x20 fully visible level.
     * The viewer vision range will be 4.0, and lights will use a circular shape.
     */
    public LightingHandler()
    {
        this(new double[20][20], Visuals.FLOAT_BLACK, Radius.CIRCLE, 4.0);
    }

    /**
     * Given a resistance array as produced by {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}
     * or {@link squidpony.squidgrid.mapping.DungeonUtility#generateSimpleResistances(char[][])}, makes a
     * LightingHandler that can have {@link Radiance} objects added to it in various locations. This will use a solid
     * black background when it casts light on cells without existing lighting. The viewer vision range will be 4.0, and
     * lights will use a circular shape.
     * @param resistance a resistance array as produced by DungeonUtility
     */
    public LightingHandler(double[][] resistance)
    {
        this(resistance, Visuals.FLOAT_BLACK, Radius.CIRCLE, 4.0);
    }
    /**
     * Given a resistance array as produced by {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}
     * or {@link squidpony.squidgrid.mapping.DungeonUtility#generateSimpleResistances(char[][])}, makes a
     * LightingHandler that can have {@link Radiance} objects added to it in various locations.
     * @param resistance a resistance array as produced by DungeonUtility
     * @param backgroundColor the background color to use, as a libGDX color
     * @param radiusStrategy the shape lights should take, typically {@link Radius#CIRCLE} for "realistic" lights or one
     *                       of {@link Radius#DIAMOND} or {@link Radius#SQUARE} to match game rules for distance
     * @param viewerVisionRange how far the player can see without light, in cells
     */
    public LightingHandler(double[][] resistance, Color backgroundColor, Radius radiusStrategy, double viewerVisionRange)
    {
        this(resistance, backgroundColor.toFloatBits(), radiusStrategy, viewerVisionRange);
    }
    /**
     * Given a resistance array as produced by {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}
     * or {@link squidpony.squidgrid.mapping.DungeonUtility#generateSimpleResistances(char[][])}, makes a
     * LightingHandler that can have {@link Radiance} objects added to it in various locations.
     * @param resistance a resistance array as produced by DungeonUtility
     * @param backgroundColor the background color to use, as a packed float (produced by {@link Color#toFloatBits()})
     * @param radiusStrategy the shape lights should take, typically {@link Radius#CIRCLE} for "realistic" lights or one
     *                       of {@link Radius#DIAMOND} or {@link Radius#SQUARE} to match game rules for distance
     * @param viewerVisionRange how far the player can see without light, in cells
     */
    public LightingHandler(double[][] resistance, float backgroundColor, Radius radiusStrategy, double viewerVisionRange)
    {
        this.radiusStrategy = radiusStrategy;
        viewerRange = viewerVisionRange;
        this.backgroundColor = backgroundColor;
        resistances = resistance;
        width = resistances.length;
        height = resistances[0].length;
        fovResult = new double[width][height];
        tempFOV = new double[width][height];
        losResult = new double[width][height];
        colorLighting = blankColoredLighting(width, height);
        tempColorLighting = new float[2][width][height];
        Coord.expandPoolTo(width, height);
        lights = new OrderedMap<>(32);
        noticeable = new GreasedRegion(width, height);
        currentBackgrounds = new float[width][height];
    }

    /**
     * Adds a Radiance as a light source at the given position. Overwrites any existing Radiance at the same position.
     * @param x the x-position to add the Radiance at
     * @param y the y-position to add the Radiance at
     * @param light a Radiance object that can have a changing radius, color, and various other effects on lighting
     * @return this for chaining
     */
    public LightingHandler addLight(int x, int y, Radiance light)
    {
        return addLight(Coord.get(x, y), light);
    }
    /**
     * Adds a Radiance as a light source at the given position. Overwrites any existing Radiance at the same position.
     * @param position the position to add the Radiance at
     * @param light a Radiance object that can have a changing radius, color, and various other effects on lighting
     * @return this for chaining
     */
    public LightingHandler addLight(Coord position, Radiance light)
    {
        lights.put(position, light);
        return this;
    }

    /**
     * Removes a Radiance as a light source from the given position, if any is present.
     * @param x the x-position to remove the Radiance from
     * @param y the y-position to remove the Radiance from
     * @return this for chaining
     */
    public LightingHandler removeLight(int x, int y)
    {
        return removeLight(Coord.get(x, y));
    }
    /**
     * Removes a Radiance as a light source from the given position, if any is present.
     * @param position the position to remove the Radiance from
     * @return this for chaining
     */
    public LightingHandler removeLight(Coord position)
    {
        lights.remove(position);
        return this;
    }
    /**
     * If a Radiance is present at oldX,oldY, this will move it to newX,newY and overwrite any existing Radiance at
     * newX,newY. If no Radiance is present at oldX,oldY, this does nothing.
     * @param oldX the x-position to move a Radiance from
     * @param oldY the y-position to move a Radiance from
     * @param newX the x-position to move a Radiance to
     * @param newY the y-position to move a Radiance to
     * @return this for chaining
     */
    public LightingHandler moveLight(int oldX, int oldY, int newX, int newY)
    {
        return moveLight(Coord.get(oldX, oldY), Coord.get(newX, newY));
    }
    /**
     * If a Radiance is present at oldPosition, this will move it to newPosition and overwrite any existing Radiance at
     * newPosition. If no Radiance is present at oldPosition, this does nothing.
     * @param oldPosition the Coord to move a Radiance from
     * @param newPosition the Coord to move a Radiance to
     * @return this for chaining
     */
    public LightingHandler moveLight(Coord oldPosition, Coord newPosition)
    {
        Radiance old = lights.get(oldPosition);
        if(old == null) return this;
        lights.alter(oldPosition, newPosition);
        return this;
    }

    /**
     * Gets the Radiance at the given position, if present, or null if there is no light source there.
     * @param x the x-position to look up
     * @param y the y-position to look up
     * @return the Radiance at the given position, or null if none is present there
     */
    public Radiance get(int x, int y)
    {
        return lights.get(Coord.get(x, y));
    }
    /**
     * Gets the Radiance at the given position, if present, or null if there is no light source there.
     * @param position the position to look up
     * @return the Radiance at the given position, or null if none is present there
     */
    public Radiance get(Coord position)
    {
        return lights.get(position);
    }

    /**
     * Edits {@link #colorLighting} by adding in and mixing the colors in {@link #tempColorLighting}, with the strength
     * of light in tempColorLighting boosted by flare (which can be any finite float greater than -1f, but is usually
     * from 0f to 1f when increasing strength).
     * Primarily used internally, but exposed so outside code can do the same things this class can.
     * @param flare boosts the effective strength of lighting in {@link #tempColorLighting}; usually from 0 to 1
     */
    public void mixColoredLighting(float flare)
    {
        float[][][] basis = colorLighting, other = tempColorLighting;
        flare += 1f;
        float b0, b1, o0, o1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0) {
                    if (resistances[x][y] >= 1) {
                        o0 = 0f;
                        if (y > 0) {
                            if ((losResult[x][y - 1] > 0 && other[0][x][y - 1] > 0 && resistances[x][y - 1] < 1)
                                    || (x > 0 && losResult[x - 1][y - 1] > 0 && other[0][x - 1][y - 1] > 0 && resistances[x - 1][y - 1] < 1)
                                    || (x < width - 1 && losResult[x + 1][y - 1] > 0 && other[0][x + 1][y - 1] > 0 && resistances[x + 1][y - 1] < 1)) {
                                o0 = other[0][x][y];
                            }
                        }
                        if (y < height - 1) {
                            if ((losResult[x][y + 1] > 0 && other[0][x][y + 1] > 0 && resistances[x][y + 1] < 1)
                                    || (x > 0 && losResult[x - 1][y + 1] > 0 && other[0][x - 1][y + 1] > 0 && resistances[x - 1][y + 1] < 1)
                                    || (x < width - 1 && losResult[x + 1][y + 1] > 0 && other[0][x + 1][y + 1] > 0 && resistances[x + 1][y + 1] < 1)) {
                                o0 = other[0][x][y];
                            }
                        }
                        if (x > 0 && losResult[x - 1][y] > 0 && other[0][x - 1][y] > 0 && resistances[x - 1][y] < 1) {
                            o0 = (float) other[0][x][y];
                        }
                        if (x < width - 1 && losResult[x + 1][y] > 0 && other[0][x + 1][y] > 0 && resistances[x + 1][y] < 1) {
                            o0 = other[0][x][y];
                        }
                        if(o0 > 0f) o1 = other[1][x][y];
                        else continue;
                    } else {
                        o0 = other[0][x][y];
                        o1 = other[1][x][y];
                    }
                    if (o0 <= 0f || o1 == 0f)
                        continue;
                    b0 = basis[0][x][y];
                    b1 = basis[1][x][y];
                    if (b1 == FLOAT_NEUTRAL) {
                        basis[1][x][y] = o1;
                        basis[0][x][y] = Math.min(1.0f, b0 + o0 * flare);
                    } else {
                        if (o1 != FLOAT_NEUTRAL) {
                            float change = (o0 - b0) * 0.5f + 0.5f;
                            final int s = NumberTools.floatToIntBits(b1), e = NumberTools.floatToIntBits(o1),
                                    ys = (s & 0xFF), cws = (s >>> 8) & 0xFF, cms = (s >>> 16) & 0xFF, sas = s >>> 24 & 0xFE,
                                    ye = (e & 0xFF), cwe = (e >>> 8) & 0xFF, cme = (e >>> 16) & 0xFF, sae = e >>> 24 & 0xFE;
                            basis[1][x][y] = NumberTools.intBitsToFloat(((int) (ys + change * (ye - ys)) & 0xFF)
                                    | (((int) (cws + change * (cwe - cws)) & 0xFF) << 8)
                                    | (((int) (cms + change * (cme - cms)) & 0xFF) << 16)
                                    | (((int) (sas + change * (sae - sas)) & 0xFE) << 24));
                            basis[0][x][y] = Math.min(1.0f, b0 + o0 * change * flare);
                        } else {
                            basis[0][x][y] = Math.min(1.0f, b0 + o0 * flare);
                        }
                    }
                }
            }
        }
    }

    /**
     * Edits {@link #colorLighting} by adding in and mixing the given color where the light strength in {@link #tempFOV}
     * is greater than 0, with that strength boosted by flare (which can be any finite float greater than -1f, but is
     * usually from 0f to 1f when increasing strength).
     * Primarily used internally, but exposed so outside code can do the same things this class can.
     * @param flare boosts the effective strength of lighting in {@link #tempColorLighting}; usually from 0 to 1
     */
    public void mixColoredLighting(float flare, float color)
    {
        final float[][][] basis = colorLighting;
        final double[][] otherStrength = tempFOV;
        flare += 1f;
        float b0, b1, o0, o1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0) {
                    if (resistances[x][y] >= 1) {
                        o0 = 0f;
                        if (y > 0) {
                            if ((losResult[x][y - 1] > 0 && otherStrength[x][y - 1] > 0 && resistances[x][y - 1] < 1)
                                    || (x > 0 && losResult[x - 1][y - 1] > 0 && otherStrength[x - 1][y - 1] > 0 && resistances[x - 1][y - 1] < 1)
                                    || (x < width - 1 && losResult[x + 1][y - 1] > 0 && otherStrength[x + 1][y - 1] > 0 && resistances[x + 1][y - 1] < 1)) {
                                o0 = (float) otherStrength[x][y];
                            }
                        }
                        if (y < height - 1) {
                            if ((losResult[x][y + 1] > 0 && otherStrength[x][y + 1] > 0 && resistances[x][y + 1] < 1)
                                    || (x > 0 && losResult[x - 1][y + 1] > 0 && otherStrength[x - 1][y + 1] > 0 && resistances[x - 1][y + 1] < 1)
                                    || (x < width - 1 && losResult[x + 1][y + 1] > 0 && otherStrength[x + 1][y + 1] > 0 && resistances[x + 1][y + 1] < 1)) {
                                o0 = (float) otherStrength[x][y];
                            }
                        }
                        if (x > 0 && losResult[x - 1][y] > 0 && otherStrength[x - 1][y] > 0 && resistances[x - 1][y] < 1) {
                            o0 = (float) otherStrength[x][y];
                        }
                        if (x < width - 1 && losResult[x + 1][y] > 0 && otherStrength[x + 1][y] > 0 && resistances[x + 1][y] < 1) {
                            o0 = (float) otherStrength[x][y];
                        }
                        if(o0 > 0f) o1 = color;
                        else continue;
                    } else {
                        if((o0 = (float) otherStrength[x][y]) != 0) o1 = color;
                        else continue;
                    }
                    b0 = basis[0][x][y];
                    b1 = basis[1][x][y];
                    final float baseStrength = Math.min(1.0f, b0 + o0 * flare);
                    if (b1 == FLOAT_NEUTRAL) {
                        basis[1][x][y] = o1;
                        basis[0][x][y] = baseStrength;
                    } else {
                        if (o1 != FLOAT_NEUTRAL) {
                            float change = (o0 - b0) * 0.5f + 0.5f,
                                    str = Math.min(1.0f, b0 + o0 * change * flare);
                            final int s = NumberTools.floatToIntBits(b1), e = NumberTools.floatToIntBits(o1),
                                    ys = (s & 0xFF), cws = (s >>> 8) & 0xFF, cms = (s >>> 16) & 0xFF,// sas = s >>> 24 & 0xFE,
                                    ye = (e & 0xFF), cwe = (e >>> 8) & 0xFF, cme = (e >>> 16) & 0xFF;//, sae = e >>> 24 & 0xFE;
                            basis[0][x][y] = str;
                            basis[1][x][y] = NumberTools.intBitsToFloat(((int) (ys + change * (ye - ys)) & 0xFF)
                                    | (((int) (cws + change * (cwe - cws)) & 0xFF) << 8)
                                    | (((int) (cms + change * (cme - cms)) & 0xFF) << 16)
                                    | 0xFE000000);
//                                    | (((int) (str * (sas + change * (sae - sas))) & 0xFE) << 24));
                        } else {
                            basis[0][x][y] = baseStrength;
                        }
                    }
                }
            }
        }
    }

    /**
     * Typically called every frame, this updates the flicker and strobe effects of Radiance objects and applies those
     * changes in lighting color and strength to the various fields of this LightingHandler. This will only have an
     * effect if {@link #calculateFOV(Coord)} or {@link #calculateFOV(int, int)} was called during the last time the
     * viewer position changed; typically calculateFOV() only needs to be called once per move, while update() needs to
     * be called once per frame. This method is usually called before each call to {@link #draw(float[][])}, but other
     * code may be between the calls and may affect the lighting in customized ways.
     */
    public void update()
    {
        Radiance radiance;
        eraseColoredLighting(colorLighting);
        final int sz = lights.size();
        Coord pos;
        for (int i = 0; i < sz; i++) {
            pos = lights.keyAt(i);
            if(!noticeable.contains(pos))
                continue;
            radiance = lights.getAt(i);
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.currentRange());
            mixColoredLighting(radiance.flare, radiance.color);
        }
    }
    /**
     * Typically called every frame when there isn't a single viewer, this updates the flicker and strobe effects of
     * Radiance objects and applies those changes in lighting color and strength to the various fields of this
     * LightingHandler. This method is usually called before each call to {@link #draw(float[][])}, but other code may
     * be between the calls and may affect the lighting in customized ways. This overload has no viewer, so all cells
     * are considered visible unless they are fully obstructed (solid cells behind walls, for example). Unlike update(),
     * this method does not need {@link #calculateFOV(Coord)} to be called for it to work properly.
     */
    public void updateAll()
    {
        Radiance radiance;
        for (int x = 0; x < width; x++) {
            PER_CELL:
            for (int y = 0; y < height; y++) {
                for (int xx = Math.max(0, x - 1), xi = 0; xi < 3 && xx < width; xi++, xx++) {
                    for (int yy = Math.max(0, y - 1), yi = 0; yi < 3 && yy < height; yi++, yy++) {
                        if(resistances[xx][yy] < 1.0){
                            losResult[x][y] = 1.0;
                            continue PER_CELL;
                        }
                    }
                }
            }
        }
        eraseColoredLighting(colorLighting);
        final int sz = lights.size();
        Coord pos;
        for (int i = 0; i < sz; i++) {
            pos = lights.keyAt(i);
            radiance = lights.getAt(i);
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.currentRange());
            mixColoredLighting(radiance.flare, radiance.color);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0.0) {
                    fovResult[x][y] = MathUtils.clamp(losResult[x][y] + colorLighting[0][x][y], 0, 1);
                }
            }
        }
    }
    /**
     * Updates the flicker and strobe effects of a Radiance object and applies the lighting from just that Radiance to
     * just the {@link #colorLighting} field, without changing FOV. This method is meant to be used for GUI effects that
     * aren't representative of something a character in the game could interact with. It is usually called after
     * {@link #update()} and before each call to {@link #draw(float[][])}, but other code may be between the calls
     * and may affect the lighting in customized ways.
     * @param pos the position of the light effect
     * @param radiance the Radiance to update standalone, which does not need to be already added to this 
     */
    public void updateUI(Coord pos, Radiance radiance)
    {
        updateUI(pos.x, pos.y, radiance);
    }

    /**
     * Updates the flicker and strobe effects of a Radiance object and applies the lighting from just that Radiance to
     * just the {@link #colorLighting} field, without changing FOV. This method is meant to be used for GUI effects that
     * aren't representative of something a character in the game could interact with. It is usually called after
     * {@link #update()} and before each call to {@link #draw(float[][])}, but other code may be between the calls
     * and may affect the lighting in customized ways.
     * @param lightX the x-position of the light effect
     * @param lightY the y-position of the light effect
     * @param radiance the Radiance to update standalone, which does not need to be already added to this 
     */
    public void updateUI(int lightX, int lightY, Radiance radiance)
    {
        FOV.reuseFOV(resistances, tempFOV, lightX, lightY, radiance.currentRange());
        mixColoredLighting(radiance.flare, radiance.color);
    }

    /**
     * Given a 2D array of packed float colors, fills the field {@link #currentBackgrounds} with different colors based on what
     * lights are present in line of sight of the viewer and the various flicker or strobe effects that Radiance light sources
     * can do. You should usually call {@link #update()} before each call to draw(), but you may want to make custom
     * changes to the lighting in between those two calls (that is the only place those changes will be noticed).
     * @param backgrounds a 2D float array of packed colors, here YCwCm+Sat format, which will not be modified
     */
    public void draw(float[][] backgrounds)
    {
        draw(currentBackgrounds, backgrounds);
    }
    /**
     * Given a 2D array of packed float colors, fills the 2D array with different colors based on what lights are
     * present in line of sight of the viewer and the various flicker or strobe effects that Radiance light sources can
     * do. You should usually call {@link #update()} before each call to draw(), but you may want to make custom
     * changes to the lighting in between those two calls (that is the only place those changes will be noticed).
     * @param editingBackgrounds a 2D float array of packed colors, here YCwCm+Sat format, which will be overwritten
     * @param stableBackgrounds a 2D float array of packed colors, here YCwCm+Sat format, which will stay the same
     */
    public void draw(float[][] editingBackgrounds, float[][] stableBackgrounds)
    {
        float current;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0.0 && fovResult[x][y] > 0.0) {
                    current = stableBackgrounds[x][y];
                    if(current == 0f)
                        current = backgroundColor;
                    editingBackgrounds[x][y] = Visuals.lerpFloatColors(current,
                            colorLighting[1][x][y], colorLighting[0][x][y]);
                }
            }
        }
    }
    /**
     * Used to calculate what cells are visible as if any flicker or strobe effects were simply constant light sources.
     * Runs part of the calculations to draw lighting as if all radii are at their widest, but does no actual drawing.
     * This should be called any time the viewer moves to a different cell, and it is critical that this is called (at
     * least) once after a move but before {@link #update()} gets called to change lighting at the new cell. This sets
     * important information on what lights might need to be calculated during each update(Coord) call; it does not need
     * to be called before {@link #updateAll()} (with no arguments) because that doesn't need a viewer. Sets
     * {@link #fovResult}, {@link #losResult}, and {@link #noticeable} based on the given viewer position and any lights
     * in {@link #lights}.
     * @param viewer the position of the player or other viewer
     * @return the calculated FOV 2D array, which is also stored in {@link #fovResult}
     */
    public double[][] calculateFOV(Coord viewer)
    {
        return calculateFOV(viewer.x, viewer.y);
    }

    /**
     * Used to calculate what cells are visible as if any flicker or strobe effects were simply constant light sources.
     * Runs part of the calculations to draw lighting as if all radii are at their widest, but does no actual drawing.
     * This should be called any time the viewer moves to a different cell, and it is critical that this is called (at
     * least) once after a move but before {@link #update()} gets called to change lighting at the new cell. This sets
     * important information on what lights might need to be calculated during each update(Coord) call; it does not need
     * to be called before {@link #updateAll()} (with no arguments) because that doesn't need a viewer. Sets
     * {@link #fovResult}, {@link #losResult}, and {@link #noticeable} based on the given viewer position and any lights
     * in {@link #lights}.
     * @param viewerX the x-position of the player or other viewer
     * @param viewerY the y-position of the player or other viewer
     * @return the calculated FOV 2D array, which is also stored in {@link #fovResult}
     */
    public double[][] calculateFOV(int viewerX, int viewerY)
    {
        return calculateFOV(viewerX, viewerY, 0, 0, width, height);
    }

    /**
     * Used to calculate what cells are visible as if any flicker or strobe effects were simply constant light sources.
     * Runs part of the calculations to draw lighting as if all radii are at their widest, but does no actual drawing.
     * This should be called any time the viewer moves to a different cell, and it is critical that this is called (at
     * least) once after a move but before {@link #update()} gets called to change lighting at the new cell. This sets
     * important information on what lights might need to be calculated during each update(Coord) call; it does not need
     * to be called before {@link #updateAll()} (with no arguments) because that doesn't need a viewer. This overload
     * allows the area this processes to be restricted to a rectangle between {@code minX} and {@code maxX} and between
     * {@code minY} and {@code maxY}, ignoring any lights outside that area (typically because they are a long way out
     * from the map's shown area). Sets {@link #fovResult}, {@link #losResult}, and {@link #noticeable} based on the
     * given viewer position and any lights in {@link #lights}.
     * @param viewerX the x-position of the player or other viewer
     * @param viewerY the y-position of the player or other viewer
     * @param minX inclusive lower bound on x to calculate
     * @param minY inclusive lower bound on y to calculate
     * @param maxX exclusive upper bound on x to calculate
     * @param maxY exclusive upper bound on y to calculate
     * @return the calculated FOV 2D array, which is also stored in {@link #fovResult}
     */
    public double[][] calculateFOV(int viewerX, int viewerY, int minX, int minY, int maxX, int maxY)
    {
        Radiance radiance;
        minX = MathUtils.clamp(minX, 0, width);
        maxX = MathUtils.clamp(maxX, 0, width);
        minY = MathUtils.clamp(minY, 0, height);
        maxY = MathUtils.clamp(maxY, 0, height);
        FOV.reuseFOV(resistances, fovResult, viewerX, viewerY, viewerRange, radiusStrategy);
        eraseColoredLighting(colorLighting);
        final int sz = lights.size();
        float maxRange = 0, range;
        Coord pos;
        for (int i = 0; i < sz; i++) {
            pos = lights.keyAt(i);
            range = lights.getAt(i).range;
            if(range > maxRange && 
                    pos.x + range >= minX && pos.x - range < maxX && pos.y + range >= minY && pos.y - range < maxY) 
                maxRange = range;
        }
        FOV.reuseLOS(resistances, losResult, viewerX, viewerY, minX, minY, maxX, maxY);
        noticeable.refill(losResult, 0.0001, Double.POSITIVE_INFINITY).expand8way((int) Math.ceil(maxRange));
        for (int i = 0; i < sz; i++) {
            pos = lights.keyAt(i);
            if(!noticeable.contains(pos))
                continue;
            radiance = lights.getAt(i);
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.range);
            mixColoredLighting(radiance.flare, radiance.color);
        }
        for (int x = Math.max(0, minX); x < maxX && x < width; x++) {
            for (int y = Math.max(0, minY); y < maxY && y < height; y++) {
                if (losResult[x][y] > 0.0) {
                    fovResult[x][y] = MathUtils.clamp(fovResult[x][y] + colorLighting[0][x][y], 0, 1);
                }
            }
        }
        return fovResult;
    }

    /**
     * Similar to {@link #colorLighting(double[][], float)}, but meant for an initial state before you have FOV or color
     * data to fill the lighting with, and you just need a map of a specific size that starts with no lighting. This
     * will produce a map that has white as the lighting color for all cells, but no cells will be lit.
     *
     * @param width  the width of the colored lighting area to generate
     * @param height the height of the colored lighting area to generate
     * @return the colored lighting 3D float array, with no cells lit and all colors set to white
     */
    public static float[][][] blankColoredLighting(int width, int height) {
        return new float[][][]
                {
                        new float[width][height],
                        ArrayTools.fill(FLOAT_NEUTRAL, width, height)
                };
    }

    /**
     * Removes any information from the given 3D float array for colored lighting, making it as if it had just been
     * built by {@link #blankColoredLighting(int, int)}, but without any allocations. This blank state is different from
     * being simply filled with all 0 or all default values; the two sub-arrays receive different contents (the one that
     * stores brightness is all 0, the one that stores colors will be filled with the float that encodes white).
     *
     * @param original a 3d float array used for colored lighting; will be modified to be completely blank
     * @return original, after modification
     */
    public static float[][][] eraseColoredLighting(float[][][] original) {
        ArrayTools.fill(original[0], 0f);
        ArrayTools.fill(original[1], FLOAT_NEUTRAL);
        return original;
    }

    /**
     * Given a 2D double array that was probably produced by FOV and a packed color as a float, this gets a 3D float
     * array (really just two same-size 2D float arrays in one parent array) that stores the brightnesses in the first
     * 2D array element and the colors in the second 2D array element (using the given color if a cell has a value in
     * lights that is greater than 0, or defaulting to white if the cell is unlit).
     *
     * @param lights a 2D double array that should probably come from FOV
     * @param color  a packed float as produced by {@link Visuals#getYCwCmSat(float, float, float, float)}
     * @return a 3D float array containing two 2D sub-arrays, the first holding brightness and the second holding color
     */
    public static float[][][] colorLighting(double[][] lights, float color) {
        return colorLightingInto(new float[2][lights.length][lights[0].length], lights, color);
    }

    /**
     * Given a 2D double array that was probably produced by FOV and a packed color as a float, this assigns into reuse
     * a 3D float array (really just two same-size 2D float arrays in one parent array) that stores the brightnesses in
     * the first 2D array element and the colors in the second 2D array element (using the given color if a cell has a
     * value in lights that is greater than 0, or defaulting to white if the cell is unlit). This method requires that
     * reuse has length of at least 2, where elements 0 and 1 must be 2D arrays of identical dimensions; this is the
     * format that {@link #colorLighting(double[][], float)} produces.
     *
     * @param reuse  a 3D float array of the exact format produced by {@link #colorLighting(double[][], float)}; must
     *               have length 2; will be modified!
     * @param lights a 2D double array that should probably come from FOV
     * @param color  a packed float as produced by {@link Visuals#getYCwCmSat(float, float, float, float)}
     * @return reuse after modification
     */
    public static float[][][] colorLightingInto(float[][][] reuse, double[][] lights, float color) {
        for (int x = 0; x < lights.length; x++) {
            for (int y = 0; y < lights[0].length; y++) {
                reuse[1][x][y] = ((reuse[0][x][y] = (float) lights[x][y]) > 0f)
                        ? color
                        : FLOAT_NEUTRAL;
            }
        }
        return reuse;
    }
}
