package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.colorful.oklab.ColorfulSprite;
import com.github.tommyettinger.colorful.oklab.Palette;
import squidpony.squidmath.Coord;

import static com.github.tommyettinger.colorful.oklab.ColorTools.oklab;

/**
 * Like a {@link com.badlogic.gdx.graphics.g2d.Sprite}, but lighter-weight and customized to the conventions of this
 * demo. Has a start and end position that it is expected to move between as its {@link #change} field changes.
 * Supports a packed float color and a tweak color, as per {@link ColorfulSprite}.
 * <br>
 * Created by Tommy Ettinger on 12/20/2019.
 */
public class Moth extends ColorfulSprite {
    public Animation<TextureRegion> animation;
    public float change;
    public Coord start;
    public Coord end;

    private Moth ()
    {
        super();
        setColor(Palette.GRAY);
        start = Coord.get(1, 1);
        end = Coord.get(1, 1);
    }
    public Moth (Animation<TextureRegion> animation) {
        this(animation, Coord.get(1, 1));
    }

    public Moth (Animation<TextureRegion> animation, Coord coord) {
        this(animation, coord, coord);
    }

    public Moth (Animation<TextureRegion> animation, Coord start, Coord end) {
        super();
        this.animation = animation;
        setSize(1, 1);
        setRegion(animation.getKeyFrame(0f));
        setTweakedColor(Palette.GRAY, oklab(0.5f, 0.5f, 0.5f, 0.8f));
        this.start = start;
        this.end = end;
    }

    public Moth animate(final float stateTime)
    {
        setRegion(animation.getKeyFrame(stateTime));
        return this;
    }
    
    public float getX()
    {
        return MathUtils.lerp(start.x, end.x, change);
    }

    public float getY()
    {
        return MathUtils.lerp(start.y, end.y, change);
    }

    @Override
    public float[] getVertices() {
        if(change >= 1f)
        {
            start = end;
            super.setPosition(start.x, start.y);
        }
        else {
            super.setPosition(MathUtils.lerp(start.x, end.x, change), MathUtils.lerp(start.y, end.y, change));
        }
        return super.getVertices();
    }
}
