package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.IntFloatMap;
import squidpony.squidmath.*;

import java.util.LinkedHashMap;

import static com.github.tommyettinger.CreatureFactory.MoveType.*;

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
    private transient MoveType[] shuffle;
    public Populace populace;
    public IRNG rng;
    
    private CreatureFactory(){}
    public CreatureFactory(
            Populace populace,
            LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> mapping){
        this.populace = populace;
        rng = new GWTRNG(CrossHash.hash64(populace.map));
        this.mapping = mapping;
        shuffle = MoveType.values();
        regions = new LinkedHashMap<>(8, 0.25f);
        GreasedRegion all = new GreasedRegion(populace.map.length, populace.map[0].length);
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
            all.or(gr);
        }
        regions.put(null, all);
    }

    public Creature make(String name, MoveType move){
        Coord pt = regions.get(move).singleRandom(rng);
        Creature cr = new Creature(mapping.get(name), pt, move.moves);
        populace.place(cr);
        return cr;
    }

    public Creature place(){
        Coord pt;
        Creature cr;
        do {
            int idx = rng.nextSignedInt(KNOWN_CREATURES.size());
            MoveType move = KNOWN_CREATURES.getAt(idx);
            pt = regions.get(move).singleRandom(rng);
            cr = new Creature(mapping.get(KNOWN_CREATURES.keyAt(idx)), pt, move.moves);
        } while (populace.containsKey(pt));
        populace.place(cr);
        return cr;
    }
    
    public static final OrderedMap<String, MoveType> KNOWN_CREATURES = OrderedMap.makeMap(
            "lobster", AQUATIC,
            "sea tiger", AQUATIC,
            "frog", AMPHIBIOUS,
            "penguin", AMPHIBIOUS,
            "pelican", FLYING,
            "puffin", FLYING,
            "golden eagle", FLYING,
            "ostrich", WALKING,
            "leopard", WALKING,
            "lion", WALKING,
            "barn cat", WALKING,
            "barbed devil", WALKING,
            "balrog", WALKING,
            "ice devil", WALKING,
            "demogorgon", WALKING,
            "jackal", WALKING,
            "wolf", WALKING,
            "hound", WALKING,
            "paper golem", WALKING,
            "wood golem", WALKING,
            "flesh golem", WALKING,
            "clay golem", WALKING,
            "stone golem", WALKING,
            "iron golem", WALKING,
            "crystal golem", WALKING,
            "air elemental", FLYING,
            "fire elemental", WALKING,
            "earth elemental", WALKING,
            "water elemental", AMPHIBIOUS,
            "eye tyrant", FLYING,
            "giant", WALKING,
            "minotaur", WALKING,
            "cyclops", WALKING,
            "militant orc", WALKING,
            "orc shaman", WALKING,
            "soldier", WALKING,
            "captain", WALKING,
            "elf", WALKING,
            "troll", WALKING,
            "gnome", WALKING,
            "leprechaun", WALKING,
            "dwarf", WALKING,
            "satyr", WALKING,
            "forest centaur", WALKING,
            "ogre", WALKING,
            "quantum mechanic", WALKING,
            "angel", FLYING,
            "tengu", FLYING,
            "djinn", FLYING,
            "goblin", WALKING,
            "mind flayer", AMPHIBIOUS,
            "bugbear", WALKING,
            "yeti", WALKING,
            "medusa", WALKING,
            "owlbear", WALKING,
            "kirin", FLYING,
            "gargoyle", FLYING,
            "kobold", WALKING,
            "ape", WALKING,
            "jabberwock", WALKING,
            "umber hulk", WALKING,
            "zruty", WALKING,
            "giant tick", WALKING,
            "giant beetle", WALKING,
            "dung worm", AMPHIBIOUS,
            "dragonfly", FLYING,
            "killer bee", FLYING,
            "giant spider", WALKING,
            "scorpion", WALKING,
            "giant ant", WALKING,
            "locust", FLYING,
            "giant slug", WALKING,
            "giant snail", WALKING,
            "feral hog", WALKING,
            "bull", WALKING,
            "camel", WALKING,
            "llama", WALKING,
            "gray goat", WALKING,
            "white sheep", WALKING,
            "white unicorn", WALKING,
            "brown horse", WALKING,
            "deer", WALKING,
            "mastodon", WALKING,
            "wumpus", WALKING,
            "wyvern", FLYING,
            "dreadwyrm", FLYING,
            "glendrake", WALKING,
            "firedrake", WALKING,
            "icewyrm", FLYING,
            "sanddrake", WALKING,
            "storrmwyrm", FLYING,
            "darkwyrm", FLYING,
            "lightwyrm", FLYING,
            "bogwyrm", FLYING,
            "sheenwyrm", FLYING,
            "kingwyrm", FLYING,
            "hydra", WALKING,
            "python", AMPHIBIOUS,
            "cobra", AMPHIBIOUS,
            "golden naga", WALKING,
            "cockatrice", WALKING,
            "chameleon", WALKING,
            "crocodile", AMPHIBIOUS,
            "red squirrel", WALKING,
            "rabbit", WALKING,
            "giant rat", WALKING,
            "rust monster", WALKING,
            "kobold zombie", WALKING,
            "gnome zombie", WALKING,
            "orc zombie", WALKING,
            "dwarf zombie", WALKING,
            "elf zombie", WALKING,
            "human zombie", WALKING,
            "ettin zombie", WALKING,
            "giant zombie", WALKING,
            "kobold mummy", WALKING,
            "gnome mummy", WALKING,
            "orc mummy", WALKING,
            "dwarf mummy", WALKING,
            "elf mummy", WALKING,
            "human mummy", WALKING,
            "ettin mummy", WALKING,
            "giant mummy", WALKING,
            "skeleton", WALKING,
            "lich", WALKING,
            "vampire", WALKING,
            "spirit", FLYING,
            "ghoul", WALKING
    );
}
