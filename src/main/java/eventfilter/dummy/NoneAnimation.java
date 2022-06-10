package eventfilter.dummy;

import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NoneAnimation extends AbstractAnimation {
    @Override
    public void renderSprite(SpriteBatch batch) {

    }

    @Override
    public Type type() {
        return Type.NONE;
    }
}
