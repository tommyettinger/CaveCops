package com.github.tommyettinger;

import squidpony.squidmath.Coord;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;

import java.util.ArrayList;

/**
 * Created by Tommy Ettinger on 9/25/2019.
 */
public class Populace extends OrderedMap<Coord, Creature> {
    public DungeonLevel dl;
    public ArrayList<Coord> tempPath;
    public OrderedMap<String, OrderedSet<Coord>> factions;
    public Populace()
    {
        super(64, 0.25f, CrossHash.identityHasher);
        dl = new DungeonLevel();
        tempPath = new ArrayList<>(16);
        factions = new OrderedMap<>(8);
    }
    public Populace(DungeonLevel dungeon)
    {
        super((dungeon.width * dungeon.height >>> 5) + 4, 0.25f, CrossHash.identityHasher);
        this.dl = dungeon;
        tempPath = new ArrayList<>(16);
        factions = new OrderedMap<>(8);
    }

    public Creature place(Creature creature) {
        creature.configureMap(dl);
        dl.lighting.addLight(creature.moth.end, creature.glow);
        OrderedSet<Coord> faction = factions.get(creature.faction);
        if(faction == null)
            factions.put(creature.faction, faction = new OrderedSet<>(64, CrossHash.mildHasher));
        faction.add(creature.moth.end);
        return super.put(creature.moth.end, creature);
    }

    public Attack act(Coord startingPosition)
    {
        Creature creature = get(startingPosition);
        if(creature == null)
            return null;
        creature.lastTarget = null;
//        if(creature.rng.next(4) >= creature.activity)
//            return startingPosition;
//        g.refill(creature.dijkstraMap.costMap, 0.0, 10.0).remove(startingPosition);

        creature.dijkstraMap.clearGoals();
        creature.dijkstraMap.resetMap();
        for (int i = 0; i < factions.size(); i++) {
            if(!creature.faction.equals(factions.keyAt(i))) 
            {
                creature.dijkstraMap.setGoals(factions.getAt(i));
            }
        }

        OrderedSet<Coord> allies = factions.get(creature.faction);
//        creature.dijkstraMap.setGoal(startingPosition.translate(1, 0));
//        creature.dijkstraMap.setGoal(startingPosition.translate(-1, 0));
//        creature.dijkstraMap.setGoal(startingPosition.translate(0, 1));
//        creature.dijkstraMap.setGoal(startingPosition.translate(0, -1));
        creature.dijkstraMap.partialScan(startingPosition, 9, allies);
        tempPath.clear();
        creature.dijkstraMap.findPathPreScanned(tempPath, startingPosition);
        if(tempPath.size() < 2)
            return null;//creature.rng.getRandomElement(creature.archetype.attacks);
        final Coord goal = tempPath.get(tempPath.size() - 2);
        if(containsKey(goal) && !allies.contains(goal))
        {
            creature.lastTarget = goal;
            return creature.rng.getRandomElement(creature.archetype.attacks);
        }
        creature.moth.end = goal;
        creature.moth.alpha = 0f;
        alterCarefully(startingPosition, goal);
        dl.lighting.moveLight(startingPosition, goal);
        return null;
    }

    @Override
    public Creature alterCarefully(Coord original, Coord replacement) {
        if(!containsKey(replacement))
        {
            Creature creature = alter(original, replacement);
            if(creature != null) 
                factions.get(creature.faction).alter(original, replacement);
            return creature;
        }
        return defRetValue;
    }

    @Override
    public Creature remove(Object k) {
        Creature creature = super.remove(k);
        if(creature != null)
            factions.get(creature.faction).remove(k);
        dl.lighting.removeLight((Coord) k);
        
        return creature;
    }
}
