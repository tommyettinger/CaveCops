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
    public int get(Stat stat)
    {
        return values[stat.ordinal()];
    }
    public void set(Stat stat, int value)
    {
        final int ord = stat.ordinal();
        switch (ord)
        {
            case 0: values[0] = Math.max(Math.min(value, maxHealth), 0);
                break;
            case 1: values[1] = Math.max(Math.min(value, maxEnergy), 0);
                break;
            default: values[ord] = value;
        }
    }
    public void inc(Stat stat, int value)
    {
        final int ord = stat.ordinal();
        switch (ord)
        {
            case 0: values[0] = Math.max(Math.min(values[0] + value, maxHealth), 0);
                break;
            case 1: values[1] = Math.max(Math.min(values[1] + value, maxEnergy), 0);
                break;
            default: values[ord] += value;
        }
    }
}
