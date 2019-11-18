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
        SHRAPNEL,
        SLAM,
        SPUR,
        STING,
        TAIL,
        TONGUE,
        WAVE,
    }

    /**
     * Created by Tommy Ettinger on 11/17/2019.
     */
    public enum DamageType {
        BURNING,
        CRUSHING,
        CURSING,
        DISGUSTING,
        FREEZING,
        GOUGING,
        GRABBING,
        MORPHING,
        PIERCING,
        QUAKING,
        RIPPING,
        SHINING,
        SHOCKING,
        SLICING,
        SOAKING,
        STUNNING,
        THRASHING,
        ZAPPING;
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
