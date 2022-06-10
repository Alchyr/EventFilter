package eventfilter.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class BlankTabBar {
    public static final float BASELINE = Settings.HEIGHT * 9F / 11F;

    public static final int BAR_W = 1334;

    private static final int BAR_H = 102;

    public static final int TICKBOX_W = 48;

    public static float LEFT_EDGE = Settings.WIDTH / 2.0F - BAR_W / 2F * Settings.scale;

    public Hitbox viewPreview;
    public boolean viewPreviewChecked;

    public BlankTabBar() {
        this.viewPreview = new Hitbox(360.0F * Settings.scale, 48.0F * Settings.scale);
        this.viewPreview.move(1410.0F * Settings.xScale, BASELINE);

        this.viewPreviewChecked = false;
    }

    public void update() {
        this.viewPreview.update();
        if (this.viewPreview.hovered && InputHelper.justClickedLeft) {
            this.viewPreviewChecked = !this.viewPreviewChecked;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.GOLDENROD);
        sb.draw(ImageMaster.COLOR_TAB_BAR, Settings.WIDTH / 2.0F - BAR_W / 2F, BASELINE - BAR_H / 2F, BAR_W / 2F, BAR_H / 2F, BAR_W, BAR_H, Settings.xScale, Settings.scale, 0.0F, 0, 0, BAR_W, BAR_H, false, false);

        sb.setColor(Color.WHITE);
        renderViewPreview(sb);
    }

    private void renderViewPreview(SpriteBatch sb) {
        Color c = Settings.CREAM_COLOR;
        if (this.viewPreview.hovered)
            c = Settings.GOLD_COLOR;
        FontHelper.renderFontRightAligned(sb, FontHelper.topPanelInfoFont, EventScreen.TEXT[0], 1546.0F * Settings.xScale, BASELINE, c);
        sb.setColor(c);
        sb.draw(this.viewPreviewChecked ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED, 1532.0F * Settings.xScale -
                FontHelper.getSmartWidth(FontHelper.topPanelInfoFont, EventScreen.TEXT[0], 9999.0F, 0.0F) - TICKBOX_W / 2F, BASELINE - TICKBOX_W / 2F, TICKBOX_W / 2F, TICKBOX_W / 2F, TICKBOX_W, TICKBOX_W, Settings.scale, Settings.scale, 0.0F, 0, 0, TICKBOX_W, TICKBOX_W, false, false);

        this.viewPreview.render(sb);
    }
}
