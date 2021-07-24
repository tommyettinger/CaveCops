package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntFloatMap;
import squidpony.StringKit;
import squidpony.squidmath.*;

import java.util.ArrayList;

import static com.github.tommyettinger.HandType.NONE;

/**
 * Created by Tommy Ettinger on 9/27/2019.
 */
public class CreatureFactory {

    public OrderedMap<String, Animation<TextureRegion>> mapping;
    public OrderedMap<Creature.MoveType, Coord[]> regions;
    public Populace populace;
    public IRNG rng;
    
    private CreatureFactory(){}
    public CreatureFactory(
            Populace populace,
            OrderedMap<String, Animation<TextureRegion>> mapping){
        this.populace = populace;
        rng = new SilkRNG(CrossHash.hash64(populace.dl.lineDungeon));
        this.mapping = mapping;
        regions = new OrderedMap<>(8, 0.25f);
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

    public Creature place(String faction){
        Coord pt;
        Creature cr;
        do {
            int idx = rng.nextSignedInt(KNOWN_CREATURES.size());
            CreatureArchetype arch = KNOWN_CREATURES.getAt(idx);
            Creature.MoveType move = arch.move;
            pt = rng.getRandomElement(regions.get(move));
            Animation<TextureRegion> anim = mapping.get(arch.name);
            if(anim == null || anim.getKeyFrames() == null || anim.getKeyFrames().length == 0)
                System.out.println("UH OH " + arch.name);
            cr = new Creature(anim, pt, arch);
            cr.faction = faction;
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
        
        public CreatureArchetype(RawCreatureArchetype raw) {
            name = raw.name;
            move = Creature.MoveType.valueOf(raw.move);
            attacks = new ArrayList<>(raw.attacks.length);
            hands = 0;
            handType = NONE;
            for(String attack : raw.attacks)
            {
                int firstSpace = attack.indexOf(' '), lastSpace = attack.indexOf(' ', firstSpace + 1);
                int count = StringKit.intFromDec(attack, 0, firstSpace);
                String damageType = attack.substring(firstSpace+1, lastSpace);
                String attackType = attack.substring(lastSpace+1);
                if("HAND".equals(attackType))
                {
                    hands += count;
                    handType = HandType.valueOf(damageType);
                }
                else 
                {
                    attacks.add(new Attack(count, Attack.DamageType.valueOf(damageType), Attack.AttackType.valueOf(attackType)));
                }
            }
        }
    }
    
    public static final OrderedMap<String, CreatureArchetype> KNOWN_CREATURES = new OrderedMap<>(RawCreatureArchetype.ENTRIES.length);
    static {
        for (int i = 0; i < RawCreatureArchetype.ENTRIES.length; i++) {
            KNOWN_CREATURES.put(RawCreatureArchetype.ENTRIES[i].name, new CreatureArchetype(RawCreatureArchetype.ENTRIES[i]));
        }
    }
}
