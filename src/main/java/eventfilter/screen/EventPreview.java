package eventfilter.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import eventfilter.EventFilter;
import eventfilter.screen.labels.EventCheckboxLabel;

public class EventPreview {
    private static final FrameBuffer eventBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);

    private static final int PREVIEW_X = (int) (Settings.WIDTH / 5.0f * 3.0f);
    private static final int PREVIEW_Y = (int) (Settings.HEIGHT / 2.0f);
    private static final int PREVIEW_WIDTH = (int) (Settings.WIDTH / 5.0f);
    private static final int PREVIEW_HEIGHT = (int) (Settings.HEIGHT / 5.0f);

    public static AbstractScene scene;

    public EventCheckboxLabel parent;
    private AbstractEvent event;
    private AbstractImageEvent imageEvent;
    public float alpha;
    public boolean ending;
    public boolean fail;

    public EventPreview(EventCheckboxLabel ecl, float alpha) {
        EventFilter.logger.info("Loading event: " + ecl.info.originalID);
        this.parent = ecl;
        this.alpha = alpha;
        this.ending = false;
        fail = false;

        try {
            AbstractEvent e = ecl.info.eventClass.getConstructor().newInstance();

            e.waitTimer = 0.05f;

            if (e instanceof AbstractImageEvent)
            {
                imageEvent = (AbstractImageEvent) e;
                /*((Color) ReflectionHacks.getPrivate(e.imageEventText, GenericEventDialog.class, "panelColor")).a = 1;
                ((Color) ReflectionHacks.getPrivate(e.imageEventText, GenericEventDialog.class, "borderColor")).a = 1;
                ((Color) ReflectionHacks.getPrivate(e.imageEventText, GenericEventDialog.class, "titleColor")).a = 1;
                ((Color) ReflectionHacks.getPrivate(e.imageEventText, GenericEventDialog.class, "imgColor")).a = 1;*/
            }
            else {
                this.event = e;
            }

            scene.randomizeScene();
        } catch(Exception e) {
            e.printStackTrace();
            fail = true;
        }
    }

    public boolean update() {
        if (event != null) {
            AbstractDungeon.isFadingOut = true; //Disables hitboxes without a patch that will run a billion times
            event.update();
            event.updateDialog();
        }
        else if (imageEvent != null) {
            AbstractDungeon.isFadingOut = true;
            imageEvent.update();
            imageEvent.updateDialog(); //Double update speed so the image appears faster :)
            imageEvent.updateDialog();
        }
        else {
            return true;
        }

        AbstractDungeon.isFadingOut = false;

        if (ending) {
            if(alpha > 0F) {
                alpha -= Gdx.graphics.getDeltaTime() * 3.0f;
                if(alpha < 0F) {
                    dispose();
                    return true;
                }
            }
        } else if(alpha < 1F) {
            alpha += Gdx.graphics.getDeltaTime() * 2.0f;
            if(alpha > 1F) {
                alpha = 1F;
            }
        }
        return false;
    }

    public void render(SpriteBatch sb) {
        boolean fail = false;
        sb.end();

        eventBuffer.begin();

        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glColorMask(true,true,true,true);

        sb.begin();
        try {
            scene.renderEventRoom(sb); //uses Color.WHITE for rendering

            if (event != null) {
                event.render(sb);
                event.panelAlpha = alpha;
                event.renderRoomEventPanel(sb);
                event.renderText(sb);
            }
            else if (imageEvent != null) {
                imageEvent.render(sb);
                imageEvent.renderText(sb);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            fail = true;
        }
        sb.end();
        eventBuffer.end();

        if (!fail) {
            TextureRegion eventRegion = new TextureRegion(eventBuffer.getColorBufferTexture());
            eventRegion.flip(false, true);

            sb.begin();
            sb.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
            sb.draw(eventRegion, PREVIEW_X, PREVIEW_Y, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        }
        else {
            sb.begin();
        }
    }

    public void dispose() {
        if (event != null)
            event.dispose();

        if (imageEvent != null)
            imageEvent.dispose();

        event = null;
        imageEvent = null;
    }
}
