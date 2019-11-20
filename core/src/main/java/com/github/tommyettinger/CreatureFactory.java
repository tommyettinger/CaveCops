package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.IntFloatMap;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.github.tommyettinger.Attack.AttackType.*;
import static com.github.tommyettinger.Attack.DamageType.*;
import static com.github.tommyettinger.Creature.MoveType.*;
import static com.github.tommyettinger.HandType.*;

/**
 * Created by Tommy Ettinger on 9/27/2019.
 */
public class CreatureFactory {

    public LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> mapping;
    public LinkedHashMap<Creature.MoveType, Coord[]> regions;
    public Populace populace;
    public IRNG rng;
    
    private CreatureFactory(){}
    public CreatureFactory(
            Populace populace,
            LinkedHashMap<String, Animation<TextureAtlas.AtlasRegion>> mapping){
        this.populace = populace;
        rng = new SilkRNG(CrossHash.hash64(populace.dl.lineDungeon));
        this.mapping = mapping;
        regions = new LinkedHashMap<>(8, 0.25f);
        GreasedRegion all = new GreasedRegion(populace.dl.width, populace.dl.height);
        for(Creature.MoveType move : Creature.MoveType.ALL)
        {
            GreasedRegion gr = new GreasedRegion(populace.dl.width, populace.dl.height), temp = gr.copy();
            IntFloatMap.Keys grounds = move.moves.keys();
            while (grounds.hasNext)
            {
                char g = (char)grounds.next();
                gr.or(temp.refill(populace.dl.decoDungeon, g));
            }
            regions.put(move, gr.asCoords());
            all.or(gr);
        }
        regions.put(null, all.asCoords());
    }

    public Creature make(String name){
        CreatureArchetype arch = KNOWN_CREATURES.get(name);
        Creature cr = new Creature(mapping.get(name),
                rng.getRandomElement(regions.get(arch.move)),
                arch);
        populace.place(cr);
        return cr;
    }

    public Creature place(){
        Coord pt;
        Creature cr;
        do {
            int idx = rng.nextSignedInt(KNOWN_CREATURES.size());
            CreatureArchetype arch = KNOWN_CREATURES.getAt(idx);
            Creature.MoveType move = arch.move;
            pt = rng.getRandomElement(regions.get(move));
            cr = new Creature(mapping.get(arch.name), pt, arch);
        } while (populace.containsKey(pt));
        populace.place(cr);
        return cr;
    }
    
    public static class CreatureArchetype {
        public String name;
        public Creature.MoveType move;
        public ArrayList<Attack> attacks;
        public int hands;
        public HandType handType;
        public CreatureArchetype(String creatureName, Creature.MoveType moveType, int attackCountA, Attack.DamageType damageTypeA, Attack.AttackType attackTypeA) {
            name = creatureName;
            move = moveType;
            attacks = new ArrayList<>(4);
            attacks.add(new Attack(attackCountA, damageTypeA, attackTypeA));
            hands = 0;
            handType = NONE;
        }

        public CreatureArchetype(String creatureName, Creature.MoveType moveType, int handCount, HandType handType) {
            name = creatureName;
            move = moveType;
            attacks = new ArrayList<>(4);
            attacks.add(new Attack(1, THRASHING, SLAM));
            hands = handCount;
            this.handType = handType;
        }

        public CreatureArchetype(String creatureName, Creature.MoveType moveType, int handCount, HandType handType, int attackCountA, Attack.DamageType damageTypeA, Attack.AttackType attackTypeA) {
            name = creatureName;
            move = moveType;
            attacks = new ArrayList<>(4);
            attacks.add(new Attack(attackCountA, damageTypeA, attackTypeA));
            hands = handCount;
            this.handType = handType;
        }

        public CreatureArchetype(String creatureName, Creature.MoveType moveType, int attackCountA, Attack.DamageType damageTypeA, Attack.AttackType attackTypeA, int attackCountB, Attack.DamageType damageTypeB, Attack.AttackType attackTypeB) {
            name = creatureName;
            move = moveType;
            attacks = new ArrayList<>(4);
            attacks.add(new Attack(attackCountA, damageTypeA, attackTypeA));
            attacks.add(new Attack(attackCountB, damageTypeB, attackTypeB));
            hands = 0;
            handType = NONE;
        }


    }
    
    public static final OrderedMap<String, CreatureArchetype> KNOWN_CREATURES = OrderedMap.makeMap(
            "lobster", new CreatureArchetype("lobster", AQUATIC, 2, CRUSHING, CLAW),
            "sea tiger", new CreatureArchetype("sea tiger", AQUATIC, 1, RIPPING, BITE, 1, THRASHING, TAIL),
            "frog", new CreatureArchetype("frog", AMPHIBIOUS, 1, GRABBING, TONGUE, 1, DISGUSTING, BITE),
            "penguin", new CreatureArchetype("penguin", AMPHIBIOUS, 1, GOUGING, PECK),
            "pelican", new CreatureArchetype("pelican", FLYING, 1, GRABBING, PECK),
            "puffin", new CreatureArchetype("puffin", FLYING, 1, CRUSHING, PECK),
            "golden eagle", new CreatureArchetype("golden eagle", FLYING, 1, GOUGING, PECK, 1, CRUSHING, CLAW),
            "ostrich", new CreatureArchetype("ostrich", WALKING, 1, GOUGING, PECK, 1, CRUSHING, KICK),
            "leopard", new CreatureArchetype("leopard", WALKING, 2, RIPPING, CLAW, 1, RIPPING, BITE),
            "lion", new CreatureArchetype("lion", WALKING, 2, RIPPING, CLAW, 1, CRUSHING, BITE),
            "barn cat", new CreatureArchetype("barn cat", WALKING, 2, GRABBING, CLAW, 1, RIPPING, BITE),
            "bone devil", new CreatureArchetype("bone devil", WALKING, 4, GOUGING, SPUR, 2, RIPPING, CLAW),
            "ice devil", new CreatureArchetype("ice devil", WALKING, 2, FREEZING, SPUR, 2, GOUGING, CLAW),
            "fire devil", new CreatureArchetype("fire devil", WALKING, 2, BURNING, CLAW, 1, BURNING, KICK),
            "jackal", new CreatureArchetype("jackal", WALKING, 1, GRABBING, BITE),
            "wolf", new CreatureArchetype("wolf", WALKING, 1, CRUSHING, BITE, 1, STUNNING, BURST),
            "hound", new CreatureArchetype("hound", WALKING, 1, CRUSHING, BITE),
            "paper golem", new CreatureArchetype("paper golem", WALKING, 4, SLICING, BLADE),
            "wood golem", new CreatureArchetype("wood golem", WALKING, 2, CRUSHING, SLAM, 2, SLICING, SHARD),
            "flesh golem", new CreatureArchetype("flesh golem", WALKING, 2, THRASHING, SLAM),
            "clay golem", new CreatureArchetype("clay golem", WALKING, 1, CRUSHING, SLAM),
            "stone golem", new CreatureArchetype("stone golem", WALKING, 1, CRUSHING, SLAM, 1, QUAKING, BURST),
            "iron golem", new CreatureArchetype("iron golem", WALKING, 1, CRUSHING, SLAM, 1, SLICING, BLADE),
            "crystal golem", new CreatureArchetype("crystal golem", WALKING, 2, SLICING, BLADE, 4, SHINING, RAY),
            "air elemental", new CreatureArchetype("air elemental", FLYING, 3, SLICING, SHARD),
            "fire elemental", new CreatureArchetype("fire elemental", WALKING, 4, BURNING, SHARD),
            "earth elemental", new CreatureArchetype("earth elemental", WALKING, 2, QUAKING, BURST),
            "water elemental", new CreatureArchetype("water elemental", AMPHIBIOUS, 1, SOAKING, WAVE),
            "eye tyrant", new CreatureArchetype("eye tyrant", FLYING, 4, ZAPPING, RAY, 1, RIPPING, BITE),
            "giant", new CreatureArchetype("giant", WALKING, 2, HUGE_HAND),
            "minotaur", new CreatureArchetype("minotaur", WALKING, 2, LARGE_HAND, 1, GOUGING, HORN),
            "cyclops", new CreatureArchetype("cyclops", WALKING, 2, HUGE_HAND, 1, THRASHING, SLAM),
            "militant orc", new CreatureArchetype("militant orc", WALKING, 2, NORMAL_HAND),
            "orc shaman", new CreatureArchetype("orc shaman", WALKING, 2, NORMAL_HAND, 1, CURSING, BURST),
            "soldier", new CreatureArchetype("soldier", WALKING, 2, NORMAL_HAND),
            "captain", new CreatureArchetype("captain", WALKING, 2, NORMAL_HAND),
            "elf", new CreatureArchetype("elf", WALKING, 2, NORMAL_HAND),
            "troll", new CreatureArchetype("troll", WALKING, 2, LARGE_HAND, 1, THRASHING, SLAM),
            "gnome", new CreatureArchetype("gnome", WALKING, 2, SMALL_HAND),
            "leprechaun", new CreatureArchetype("leprechaun", WALKING, 2, SMALL_HAND, 1, ZAPPING, WAVE),
            "dwarf", new CreatureArchetype("dwarf", WALKING, 2, NORMAL_HAND),
            "satyr", new CreatureArchetype("satyr", WALKING, 2, NORMAL_HAND, 1, CRUSHING, KICK),
            "forest centaur", new CreatureArchetype("forest centaur", WALKING, 2, NORMAL_HAND, 2, CRUSHING, KICK),
            "ogre", new CreatureArchetype("ogre", WALKING, 2, LARGE_HAND, 2, THRASHING, SLAM),
            "quantum mechanic", new CreatureArchetype("quantum mechanic", WALKING, 2, NORMAL_HAND, 1, ZAPPING, RAY),
            "angel", new CreatureArchetype("angel", FLYING, 2, NORMAL_HAND, 1, SHINING, BURST),
            "tengu", new CreatureArchetype("tengu", FLYING, 2, NORMAL_HAND, 1, GOUGING, PECK),
            "djinn", new CreatureArchetype("djinn", FLYING, 2, LARGE_HAND, 2, SLICING, SHARD),
            "goblin", new CreatureArchetype("goblin", WALKING, 2, SMALL_HAND, 1, DISGUSTING, SLAM),
            "mind flayer", new CreatureArchetype("mind flayer", AMPHIBIOUS, 2, NORMAL_HAND, 1, STUNNING, WAVE),
            "bugbear", new CreatureArchetype("bugbear", WALKING, 2, LARGE_HAND),
            "yeti", new CreatureArchetype("yeti", WALKING, 2, LARGE_HAND, 1, FREEZING, BURST),
            "medusa", new CreatureArchetype("medusa", WALKING, 2, NORMAL_HAND, 1, MORPHING, RAY),
            "owlbear", new CreatureArchetype("owlbear", WALKING, 2, THRASHING, CLAW, 1, GOUGING, PECK),
            "kirin", new CreatureArchetype("kirin", FLYING, 2, SHINING, KICK, 1, STUNNING, HORN),
            "gargoyle", new CreatureArchetype("gargoyle", FLYING, 2, GOUGING, CLAW, 2, CRUSHING, KICK),
            "kobold", new CreatureArchetype("kobold", WALKING, 2, SMALL_HAND, 1, THRASHING, BITE),
            "ape", new CreatureArchetype("ape", WALKING, 2, CRUSHING, SLAM, 1, RIPPING, BITE),
            "jabberwock", new CreatureArchetype("jabberwock", WALKING, 1, RIPPING, BITE, 2, GOUGING, CLAW),
            "umber hulk", new CreatureArchetype("umber hulk", WALKING, 2, CRUSHING, CLAW, 1, STUNNING, WAVE),
            "zruty", new CreatureArchetype("zruty", WALKING, 3, CRUSHING, BITE),
            "giant tick", new CreatureArchetype("giant tick", WALKING, 1, GRABBING, BITE),
            "giant beetle", new CreatureArchetype("giant beetle", WALKING, 1, SLICING, BITE),
            "dung worm", new CreatureArchetype("dung worm", AMPHIBIOUS, 1, DISGUSTING, BITE),
            "dragonfly", new CreatureArchetype("dragonfly", FLYING, 1, CRUSHING, BITE),
            "killer bee", new CreatureArchetype("killer bee", FLYING, 1, PIERCING, STING),
            "giant spider", new CreatureArchetype("giant spider", WALKING, 1, PIERCING, BITE),
            "scorpion", new CreatureArchetype("scorpion", WALKING, 2, CRUSHING, CLAW, 1, PIERCING, STING),
            "giant ant", new CreatureArchetype("giant ant", WALKING, 1, RIPPING, BITE),
            "locust", new CreatureArchetype("locust", FLYING, 1, SLICING, BITE),
            "giant slug", new CreatureArchetype("giant slug", WALKING, 1, DISGUSTING, TONGUE),
            "giant snail", new CreatureArchetype("giant snail", WALKING, 1, DISGUSTING, TONGUE),
            "feral hog", new CreatureArchetype("feral hog", WALKING, 1, THRASHING, BITE),
            "bull", new CreatureArchetype("bull", WALKING, 1, THRASHING, HORN, 2, CRUSHING, KICK),
            "camel", new CreatureArchetype("camel", WALKING, 1, CRUSHING, BITE, 2, CRUSHING, KICK),
            "llama", new CreatureArchetype("llama", WALKING, 1, CRUSHING, BITE, 2, CRUSHING, KICK),
            "gray goat", new CreatureArchetype("gray goat", WALKING, 1, CRUSHING, BITE, 1, PIERCING, HORN),
            "white sheep", new CreatureArchetype("white sheep", WALKING, 1, CRUSHING, BITE, 2, CRUSHING, KICK),
            "white unicorn", new CreatureArchetype("white unicorn", WALKING, 1, SHINING, HORN, 2, CRUSHING, KICK),
            "brown horse", new CreatureArchetype("brown horse", WALKING, 1, THRASHING, BITE, 2, CRUSHING, KICK),
            "deer", new CreatureArchetype("deer", WALKING, 2, THRASHING, KICK, 1, RIPPING, HORN),
            "mastodon", new CreatureArchetype("mastodon", WALKING, 2, CRUSHING, KICK, 1, PIERCING, BITE),
            "wumpus", new CreatureArchetype("wumpus", WALKING, 1, CRUSHING, BITE),
            "wyvern", new CreatureArchetype("wyvern", FLYING, 1, RIPPING, BITE, 2, GRABBING, CLAW),
            "dreadwyrm", new CreatureArchetype("dreadwyrm", FLYING, 1, CRUSHING, BITE, 1, CURSING, WAVE),
            "glendrake", new CreatureArchetype("glendrake", WALKING, 1, RIPPING, BITE, 1, SLICING, WAVE),
            "firedrake", new CreatureArchetype("firedrake", WALKING, 1, RIPPING, BITE, 1, BURNING, WAVE),
            "icewyrm", new CreatureArchetype("icewyrm", FLYING, 1, CRUSHING, BITE, 1, FREEZING, BURST),
            "sanddrake", new CreatureArchetype("sanddrake", WALKING, 1, RIPPING, BITE, 1, QUAKING, BURST),
            "storrmwyrm", new CreatureArchetype("storrmwyrm", FLYING, 1, CRUSHING, BITE, 1, SHOCKING, WAVE),
            "darkwyrm", new CreatureArchetype("darkwyrm", FLYING, 1, CRUSHING, BITE, 1, STUNNING, WAVE),
            "lightwyrm", new CreatureArchetype("lightwyrm", FLYING, 1, CRUSHING, BITE, 1, SHINING, BURST),
            "bogwyrm", new CreatureArchetype("bogwyrm", FLYING, 1, CRUSHING, BITE, 1, SOAKING, BURST),
            "sheenwyrm", new CreatureArchetype("sheenwyrm", FLYING, 1, CRUSHING, BITE, 1, ZAPPING, WAVE),
            "kingwyrm", new CreatureArchetype("kingwyrm", FLYING, 1, CRUSHING, BITE, 1, MORPHING, WAVE),
            "hydra", new CreatureArchetype("hydra", AMPHIBIOUS, 3, RIPPING, BITE),
            "python", new CreatureArchetype("python", AMPHIBIOUS, 1, GRABBING, BITE, 1, CRUSHING, SLAM),
            "cobra", new CreatureArchetype("cobra", AMPHIBIOUS, 1, PIERCING, BITE),
            "golden naga", new CreatureArchetype("golden naga", AMPHIBIOUS, 1, CRUSHING, SLAM, 1, ZAPPING, BURST),
            "cockatrice", new CreatureArchetype("cockatrice", AMPHIBIOUS, 1, GOUGING, PECK, 1, MORPHING, RAY),
            "chameleon", new CreatureArchetype("chameleon", WALKING, 1, GRABBING, TONGUE, 1, CRUSHING, BITE),
            "crocodile", new CreatureArchetype("crocodile", AMPHIBIOUS, 1, RIPPING, BITE, 1, SOAKING, SLAM),
            "red squirrel", new CreatureArchetype("red squirrel", WALKING, 1, CRUSHING, BITE),
            "rabbit", new CreatureArchetype("rabbit", WALKING, 1, CRUSHING, BITE, 1, THRASHING, KICK),
            "giant rat", new CreatureArchetype("giant rat", WALKING, 1, THRASHING, BITE),
            "rust monster", new CreatureArchetype("rust monster", WALKING, 1, MORPHING, BITE),
            "human zombie", new CreatureArchetype("human zombie", WALKING, 1, DISGUSTING, BITE, 1, THRASHING, SLAM),
            "human mummy", new CreatureArchetype("human mummy", WALKING, 2, CURSING, SLAM, 1, CURSING, WAVE),
            "skeleton", new CreatureArchetype("skeleton", WALKING, 2, NORMAL_HAND, 1, CRUSHING, BITE),
            "lich", new CreatureArchetype("lich", WALKING, 2, NORMAL_HAND, 1, CURSING, RAY),
            "vampire", new CreatureArchetype("vampire", WALKING, 2, NORMAL_HAND, 1, CURSING, BITE),
            "spirit", new CreatureArchetype("spirit", FLYING, 1, CURSING, BURST),
            "ghoul", new CreatureArchetype("ghoul", WALKING, 2, DISGUSTING, CLAW, 1, DISGUSTING, BITE)
    );
}
