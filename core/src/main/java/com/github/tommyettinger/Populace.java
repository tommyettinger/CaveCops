package com.github.tommyettinger;

import squidpony.squidmath.Coord;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.OrderedMap;

import java.util.ArrayList;

/**
 * Created by Tommy Ettinger on 9/25/2019.
 */
public class Populace extends OrderedMap<Coord, Creature> {
    public DungeonLevel dl;
    public ArrayList<Coord> tempPath;
    public Populace()
    {
        super(64, 0.25f, CrossHash.identityHasher);
        dl = new DungeonLevel();
        tempPath = new ArrayList<>(16);
    }
    public Populace(DungeonLevel dungeon)
    {
        super((dungeon.width * dungeon.height >>> 5) + 4, 0.25f, CrossHash.identityHasher);
        this.dl = dungeon;
        tempPath = new ArrayList<>(16);
    }

    public Creature place(Creature creature) {
        creature.configureMap(dl);
        dl.lighting.addLight(creature.moth.end, creature.glow);
        return super.put(creature.moth.end, creature);
    }

    public Coord act(Coord startingPosition)
    {
        Coord playerPosition = keyAt(0);
        Creature creature = get(startingPosition);
        if(creature == null)
            return startingPosition;
//        if(creature.rng.next(4) >= creature.activity)
//            return startingPosition;
//        g.refill(creature.dijkstraMap.costMap, 0.0, 10.0).remove(startingPosition);

        creature.dijkstraMap.clearGoals();
        creature.dijkstraMap.resetMap();
        creature.dijkstraMap.setGoal(playerPosition);
        Creature player = removeAt(0);

//        creature.dijkstraMap.setGoal(startingPosition.translate(1, 0));
//        creature.dijkstraMap.setGoal(startingPosition.translate(-1, 0));
//        creature.dijkstraMap.setGoal(startingPosition.translate(0, 1));
//        creature.dijkstraMap.setGoal(startingPosition.translate(0, -1));
        creature.dijkstraMap.partialScan(startingPosition, 9, keys);
        tempPath.clear();
        creature.dijkstraMap.findPathPreScanned(tempPath, startingPosition);
        putAt(playerPosition, player, 0);
        if(tempPath.size() < 2)
            return startingPosition;
        Coord goal = tempPath.get(tempPath.size() - 2);
        if(goal.equals(playerPosition))
            return startingPosition;
        creature.moth.end = goal;
        creature.moth.alpha = 0f;
        alterCarefully(startingPosition, creature.moth.end);
        dl.lighting.moveLight(startingPosition, creature.moth.end);
        return creature.moth.end;
    }
}
