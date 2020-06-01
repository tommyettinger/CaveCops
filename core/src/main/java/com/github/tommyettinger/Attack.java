package com.github.tommyettinger;

/**
 * Created by Tommy Ettinger on 11/17/2019.
 */
public class Attack {

    /**
     * Created by Tommy Ettinger on 11/17/2019.
     */
    public enum AttackType {
        BITE,
        BLADE,
        BURST,
        CLAW,
        HORN,
        KICK,
        PECK,
        RAY,
        SHARD,
        SLAM,
        SPUR,
        STING,
        TAIL,
        TONGUE,
        WAVE,
        WRAP;
        
        public final String printName;

        AttackType()
        {
            printName = name().toLowerCase();
        }
        @Override
        public String toString() {
            return printName;
        }
    }

    /**
     * Created by Tommy Ettinger on 11/17/2019.
     */
    public enum DamageType {
        BURNING(true),
        CRUSHING(false),
        CURSING(true),
        DISGUSTING(false),
        FREEZING(true),
        GOUGING(false),
        GRABBING(false),
        MORPHING(true),
        PIERCING(false),
        QUAKING(false),
        RIPPING(false),
        SHINING(true),
        SHOCKING(true),
        SLICING(false),
        SOAKING(true),
        STUNNING(true),
        THRASHING(false),
        ZAPPING(true);

        public final String printName;
        public final boolean isMagical;

        DamageType(boolean magical)
        {
            isMagical = magical;
            printName = name().toLowerCase();
        }
        @Override
        public String toString() {
            return printName;
        }

    }
    
    public int attackCount;
    public DamageType damageType;
    public AttackType attackType;

    public Attack() {
        attackCount = 1;
        damageType = DamageType.THRASHING;
        attackType = AttackType.SLAM;
    }

    public Attack(int attackCount, DamageType damageType, AttackType attackType)
    {
        this.attackCount = attackCount;
        this.attackType = attackType;
        this.damageType = damageType;
    }
}
