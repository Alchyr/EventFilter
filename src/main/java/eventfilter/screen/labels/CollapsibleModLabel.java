package eventfilter.screen.labels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import eventfilter.screen.EventScreen;

import java.util.List;

import static eventfilter.EventFilter.modEvents;

public class CollapsibleModLabel extends ClickableLabel {
    private static final int ARROW_SIZE = 32;

    public List<EventCheckboxLabel> events;
    private boolean checked;
    private String ID;

    public CollapsibleModLabel(String ID) {
        super(ID);

        this.checked = true;
        this.ID = ID;
        events = EventCheckboxLabel.fromList(modEvents.get(ID));
    }

    public int update(float y, int offset, EventScreen parent) {
        offset += 2;
        offset = super.update(y, offset);

        if (checked) {
            for (EventCheckboxLabel event : events) {
                offset = event.update(y, offset, parent);
            }
        }
        return offset;
    }

    public int render(SpriteBatch sb, float x, float y, int offset) {
        offset += 2;
        if(active) {
            sb.draw(ImageMaster.FILTER_ARROW, x - ARROW_SIZE / 2F, this.hb.cY - ARROW_SIZE / 2F, ARROW_SIZE / 2F, ARROW_SIZE / 2F, ARROW_SIZE, ARROW_SIZE, Settings.scale, Settings.scale, checked ? 0 : 90, 0, 0, ARROW_SIZE, ARROW_SIZE, false, false);
        }
        super.render(sb, x + ARROW_SIZE * Settings.scale / 2F, y, offset);

        if (checked) {
            for (EventCheckboxLabel event : events) {
                offset = event.render(sb, x + EventScreen.INDENT, y, offset);
            }
        }
        return offset;
    }

    @Override
    protected void click() {
        checked = !checked;
    }

    public void clear() {
        events.clear();
    }
}
