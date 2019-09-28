package com.github.tommyettinger;

import squidpony.squidmath.Coord;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.OrderedMap;

import java.util.ArrayList;

/**
 * Created by Tommy Ettinger on 9/25/2019.
 */
public class Populace extends OrderedMap<Coord, Creature> {
    public char[][] map;
    public ArrayList<Coord> tempPath;
    public Populace()
    {
        super(64, 0.25f, CrossHash.identityHasher);
        map = new char[][]{{'#', '#', '#', '#'}, {'#', '.', '.', '#'}, {'#', '.', '.', '#'}, {'#', '#', '#', '#'}};
        tempPath = new ArrayList<>(16);
    }
    public Populace(char[][] map)
    {
        super((map.length * map[0].length >>> 5) + 4, 0.25f, CrossHash.identityHasher);
        this.map = map;
        tempPath = new ArrayList<>(16);
    }

    public Creature place(Creature creature) {
        creature.configureMap(map);
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

//        creature.dijkstraMap.setGoal(startingPosition.translate(1, 0));
//        creature.dijkstraMap.setGoal(startingPosition.translate(-1, 0));
//        creature.dijkstraMap.setGoal(startingPosition.translate(0, 1));
//        creature.dijkstraMap.setGoal(startingPosition.translate(0, -1));
        creature.dijkstraMap.partialScan(startingPosition, 9, keys);
        tempPath.clear();
        creature.dijkstraMap.findPathPreScanned(tempPath, startingPosition);
        if(tempPath.size() < 2)
            return startingPosition;
        creature.moth.end = tempPath.get(tempPath.size() - 2);
        creature.moth.alpha = 0f;
        alterCarefully(startingPosition, creature.moth.end);
        return creature.moth.end;
    }
}
