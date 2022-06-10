package eventfilter.screen.labels;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eventfilter.screen.BlankTabBar;
import eventfilter.screen.EventScreen;

public abstract class ClickableLabel {
    public Hitbox hb;
    public String text;
    protected boolean active;

    public ClickableLabel(String text) {
        this.hb = new Hitbox(BlankTabBar.LEFT_EDGE, 0, BlankTabBar.BAR_W * Settings.scale, EventScreen.LINEHEIGHT);
        this.text = text;
    }

    public int update(float y, int offset) {
        y = this.calcY(y, offset);
        active = (y < BlankTabBar.BASELINE && y > -Settings.HEIGHT - EventScreen.LINEHEIGHT);
        this.hb.move(this.hb.cX, y);
        if (active) {
            this.hb.update();
            if (!EventScreen.canHover)
                this.hb.hovered = false;
            if (this.hb.hovered && InputHelper.justClickedLeft) {
                this.click();
            }
        }
        return offset;
    }

    protected int render(SpriteBatch sb, float x, float y, int offset) {
        if (active) {
            FontHelper.renderFontLeft(sb, FontHelper.topPanelInfoFont, text, x, this.hb.cY, this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
            this.hb.render(sb);
        }
        return offset;
    }

    protected float calcY(float y, int offset) {
        return -offset * EventScreen.LINEHEIGHT + y;
    }

    protected abstract void click();
}
