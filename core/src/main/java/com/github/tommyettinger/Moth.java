package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import squidpony.squidmath.Coord;

/**
 * Like a {@link com.badlogic.gdx.graphics.g2d.Sprite}, but lighter-weight and customized to the conventions of this
 * game. Supports a packed float color that should be interpreted as a YCwCmSat value.
 * <br>
 * Created by Tommy Ettinger on 9/16/2019.
 */
public class Moth extends TextureRegion {
    public Animation<TextureAtlas.AtlasRegion> animation;
    public float alpha;
    public Coord start = Coord.get(0, 0);
    public Coord end = Coord.get(0, 0);
    public float color;

    private Moth()
    {
        super();
        animation = new Animation<TextureAtlas.AtlasRegion>(0.375f);
        this.color = Visuals.FLOAT_NEUTRAL;
    }
    public Moth(Animation<TextureAtlas.AtlasRegion> animation) {
        super();
        this.animation = animation;
        setRegion(animation.getKeyFrame(0f));
        this.color = Visuals.FLOAT_NEUTRAL;
    }

    public Moth(Animation<TextureAtlas.AtlasRegion> animation, Coord coord) {
        this(animation, coord, coord);
    }

    public Moth(Animation<TextureAtlas.AtlasRegion> animation, Coord start, Coord end) {
        super();
        this.animation = animation;
        setRegion(animation.getKeyFrame(0f));
        this.color = Visuals.FLOAT_NEUTRAL;
        this.start = start;
        this.end = end;
    }
    
    public TextureRegion animate(final float stateTime)
    {
        setRegion(animation.getKeyFrame(stateTime));
        return this;
    }
    
    public float getX()
    {
        if(alpha >= 1f)
            return (start = end).x;
        return MathUtils.lerp(start.x, end.x, alpha);
    }

    public float getY()
    {
        if(alpha >= 1f)
            return (start = end).y;
        return MathUtils.lerp(start.y, end.y, alpha);
    }
}
