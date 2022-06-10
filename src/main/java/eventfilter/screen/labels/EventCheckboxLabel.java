package eventfilter.screen.labels;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import eventfilter.EventFilter;
import eventfilter.EventInfo;
import eventfilter.screen.BlankTabBar;
import eventfilter.screen.EventScreen;

import java.util.ArrayList;
import java.util.List;

import static eventfilter.EventFilter.eventConfig;

public class EventCheckboxLabel {
    public Hitbox hb;
    public String text;
    public boolean checked;
    protected boolean active;

    public EventInfo info;

    public EventCheckboxLabel(String name, boolean checked, EventInfo info) {
        this.hb = new Hitbox(BlankTabBar.LEFT_EDGE, 0, BlankTabBar.BAR_W * Settings.scale, EventScreen.LINEHEIGHT * 2);
        this.text = name;

        this.info = info;
        this.checked = checked;
    }

    public int update(float y, int offset, EventScreen parent) {
        offset += 2; //height of 2
        y = this.calcY(y, offset);
        active = (y < BlankTabBar.BASELINE && y > -(EventScreen.LINEHEIGHT * 2));
        this.hb.move(this.hb.cX, y);
        if (active || this.hb.hovered) {
            this.hb.update();
            if (!EventScreen.canHover)
                this.hb.hovered = false;

            if (this.hb.hovered && InputHelper.justClickedLeft && this.click()) {
                //save
                EventFilter.saveConfigData();
            }
        }

        if (!this.hb.hovered) {
            if (parent.eventPreview != null && parent.eventPreview.parent == this) {
                parent.eventPreview.ending = true;
            }
        } else if (parent.topBar.viewPreviewChecked && (parent.eventPreview == null || parent.eventPreview.parent != this) && !parent.renderBlacklist.contains(this.info.ID)) {
            parent.previewEvent(this);
        }

        return offset;
    }

    public int render(SpriteBatch sb, float x, float y, int offset) {
        offset += 2; //height of 2
        if (active) {
            sb.setColor(this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
            sb.draw(this.checked ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED, x + BlankTabBar.TICKBOX_W / 6F, EventScreen.LINEHEIGHT + hb.y - BlankTabBar.TICKBOX_W / 6F, BlankTabBar.TICKBOX_W / 2F, BlankTabBar.TICKBOX_W / 2F, BlankTabBar.TICKBOX_W, BlankTabBar.TICKBOX_W, Settings.scale, Settings.scale, 0.0F, 0, 0, BlankTabBar.TICKBOX_W, BlankTabBar.TICKBOX_W, false, false);
            FontHelper.renderFontLeft(sb, FontHelper.topPanelInfoFont, text, x + BlankTabBar.TICKBOX_W * Settings.scale, this.hb.y + (EventScreen.LINEHEIGHT * 1.5f), this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);

            x += (BlankTabBar.TICKBOX_W * Settings.scale) + EventScreen.INDENT;
            FontHelper.renderFontLeft(sb, FontHelper.topPanelInfoFont, info.toString(), x, this.hb.y + (EventScreen.LINEHEIGHT * 0.5f), this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
            this.hb.render(sb);
        }

        return offset;
    }

    protected float calcY(float y, int offset) {
        return -offset * EventScreen.LINEHEIGHT + y;
    }

    protected boolean click() {
        this.checked = !this.checked;
        this.updateEntry();
        return true;
    }

    public void updateEntry() {
        eventConfig.put(info.eventClass.getName(), checked);
    }


    public static List<EventCheckboxLabel> fromList(List<EventInfo> source) {
        List<EventCheckboxLabel> result = new ArrayList<>();
        for(EventInfo event : source) {
            String name = event.originalID;
            try {
                EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(event.originalID);
                if (eventStrings != null && eventStrings.NAME != null) {
                    name = eventStrings.NAME;
                }

                AbstractEvent e = event.eventClass.getConstructor().newInstance();

                if (e instanceof AbstractImageEvent) {
                    String title = ReflectionHacks.getPrivate(e, AbstractImageEvent.class, "title");
                    if (title != null && !title.equals(""))
                        name = title;
                }
            }
            catch (Exception ignored) { }

            result.add(new EventCheckboxLabel(name, eventConfig.get(event.eventClass.getName()), event));
        }
        return result;
    }
}
