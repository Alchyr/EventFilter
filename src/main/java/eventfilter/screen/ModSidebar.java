package eventfilter.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModSidebar {
    private static final float SPACING = 64.0f;
    private List<ModEntry> entries = new ArrayList<>();

    private EventScreen parent;

    private final float startY = BlankTabBar.BASELINE;
    private float endY = BlankTabBar.BASELINE;

    public ModSidebar(EventScreen parent, List<String> mods) {
        this.parent = parent;
        for (String mod : mods)
            entries.add(new ModEntry(mod));

        calculateBounds();
    }

    private void calculateBounds() {
        float height = SPACING * Settings.scale * entries.size() - (Settings.HEIGHT * 0.66f);

        if (height < 0)
            height = 0;

        endY = BlankTabBar.BASELINE;
        endY += height;
    }

    public void update(float percent) {
        float y = MathHelper.valueFromPercentBetween(this.startY, this.endY, percent);
        int index = 0;
        for (ModEntry entry : entries) {
            entry.hb.move(157.0f * Settings.scale, y - SPACING * index * Settings.scale - 14.0f * Settings.scale);
            entry.active = entry.hb.y < Settings.HEIGHT && entry.hb.y + entry.hb.height > 0;
            if (entry.active) {
                entry.hb.update();
                if (entry.hb.hovered && InputHelper.justClickedLeft) {
                    parent.didChangeMod(index);
                }
            }
            index++;
        }
    }

    public void render(SpriteBatch sb) {
        for (ModEntry entry : entries) {
            if (entry.active) {
                sb.setColor(entry.color);
                sb.draw(ImageMaster.COLOR_TAB_BAR, 40.0f * Settings.scale, entry.hb.cY - 51F, 0, 0, 235.0F, 102.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);

                Color textcolor = Settings.CREAM_COLOR;
                if (entry.hb.hovered)  {
                    textcolor = Settings.GOLD_COLOR;
                }
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, entry.key, 157.0f * Settings.scale, entry.hb.cY, textcolor, 0.85f);
            }
        }

        for (ModEntry entry : entries) {
            entry.hb.render(sb);
        }
    }

    public static class ModEntry {
        private static final Random random = new Random();

        public String key;
        public Color color;
        public Hitbox hb;
        public boolean active; //visible on screen

        public ModEntry(String key) {
            if (key.equals("Slay the Spire")) {
                this.color = Color.LIGHT_GRAY.cpy();
            }
            else {
                this.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1F);
            }

            this.key = key;
            this.hb = new Hitbox(235.0f * Settings.scale, 51.0f * Settings.scale);
        }
    }
}
