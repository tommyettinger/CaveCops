package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Like a {@link com.badlogic.gdx.graphics.g2d.Sprite}, but lighter-weight and customized to the conventions of this
 * game. Supports a packed float color that should be interpreted as a YCwCmSat value.
 * <br>
 * Created by Tommy Ettinger on 9/16/2019.
 */
public class Moth extends TextureRegion {
    public Animation<TextureAtlas.AtlasRegion> animation;
    public float startX, endX, startY, endY, alpha;
    public float color;

    private Moth()
    {
        super();
        animation = new Animation<TextureAtlas.AtlasRegion>(0.375f);
        this.color = CaveCops.FLOAT_NEUTRAL;
    }
    public Moth(Animation<TextureAtlas.AtlasRegion> animation) {
        super();
        this.animation = animation;
        setRegion(animation.getKeyFrame(0f));
        this.color = CaveCops.FLOAT_NEUTRAL;
    }

    public Moth(Animation<TextureAtlas.AtlasRegion> animation, float x, float y) {
        this(animation, x, y, x, y);
    }

    public Moth(Animation<TextureAtlas.AtlasRegion> animation, float startX, float startY, float endX, float endY) {
        super();
        this.animation = animation;
        setRegion(animation.getKeyFrame(0f));
        this.color = CaveCops.FLOAT_NEUTRAL;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }
    
    public TextureRegion animate(final float stateTime)
    {
        setRegion(animation.getKeyFrame(stateTime));
        return this;
    }
    
    public float getX()
    {
        if(alpha >= 1f)
            return (startX = endX);
        return MathUtils.lerp(startX, endX, alpha);
    }

    public float getY()
    {
        if(alpha >= 1f)
            return (startY = endY);
        return MathUtils.lerp(startY, endY, alpha);
    }
}
