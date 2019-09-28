package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.IntFloatMap;
import squidpony.squidmath.*;

import java.util.LinkedHashMap;

/**
 * Created by Tommy Ettinger on 9/27/2019.
 */
public class CreatureFactory {
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
    
    public LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> mapping;
    public LinkedHashMap<MoveType, GreasedRegion> regions;
    public Populace populace;
    public IRNG rng;
    
    private CreatureFactory(){}
    public CreatureFactory(
            Populace populace,
            LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> mapping){
        this.populace = populace;
        rng = new GWTRNG(CrossHash.hash64(populace.map));
        this.mapping = mapping;
        regions = new LinkedHashMap<>(8, 0.25f);
        for(MoveType move : MoveType.ALL)
        {
            GreasedRegion gr = new GreasedRegion(populace.map.length, populace.map[0].length), temp = gr.copy();
            IntFloatMap.Keys grounds = move.moves.keys();
            while (grounds.hasNext)
            {
                char g = (char)grounds.next();
                gr.or(temp.refill(populace.map, g));
            }
            regions.put(move, gr);
        }
    }
    
    public Creature make(String name, MoveType move){
        Coord pt = regions.get(move).singleRandom(rng);
        Creature cr = new Creature(mapping.get(name), pt, move.moves);
        populace.place(cr);
        return cr;
    }
}
