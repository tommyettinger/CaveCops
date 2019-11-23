package com.github.tommyettinger;

/**
 * Created by Tommy Ettinger on 11/14/2019.
 */
public class StatHolder {
    public int[] values;
    public int maxHealth, maxEnergy;
    public StatHolder()
    {
        this(8, 4);
    }
    public StatHolder(int maxHealth, int maxEnergy)
    {
        values = new int[Stat.all.length];
        values[0] = this.maxHealth = maxHealth;
        values[1] = this.maxEnergy = maxEnergy;
    }
    public StatHolder(int maxHealth, int maxEnergy, int... statValues)
    {
        values = new int[Stat.all.length];
        values[0] = this.maxHealth = maxHealth;
        values[1] = this.maxEnergy = maxEnergy;
        for (int i = 0, v = 2; i < statValues.length && v < values.length; i++, v++) {
            values[v] = statValues[i];
        }
    }
    public int get(Stat stat)
    {
        return values[stat.ordinal()];
    }
    public int set(Stat stat, int value)
    {
        final int ord = stat.ordinal();
        switch (ord)
        {
            case 0: return values[0] = Math.max(Math.min(value, maxHealth), 0);
            case 1: return values[1] = Math.max(Math.min(value, maxEnergy), 0);
            default: return values[ord] = value;
        }
    }
    public int inc(Stat stat, int value)
    {
        final int ord = stat.ordinal();
        switch (ord)
        {
            case 0: return values[0] = Math.max(Math.min(values[0] + value, maxHealth), 0);
            case 1: return values[1] = Math.max(Math.min(values[1] + value, maxEnergy), 0);
            default: return values[ord] += value;
        }
    }
}
