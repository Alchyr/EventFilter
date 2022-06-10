package eventfilter.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import eventfilter.EventFilter;
import eventfilter.dummy.DummyPlayer;
import eventfilter.dummy.DummyScene;
import eventfilter.screen.labels.CollapsibleModLabel;
import eventfilter.screen.labels.EventCheckboxLabel;

import java.util.*;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.eventBackgroundImg;
import static eventfilter.EventFilter.makeID;

public class EventScreen implements ScrollBarListener {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("EventScreen"));

    public static final String[] TEXT = uiStrings.TEXT;

    public static final float INDENT = 30F * Settings.scale;
    public static final float LINEHEIGHT = 32F * Settings.scale;

    private static float drawStartX;
    private static float drawStartY = Settings.HEIGHT * 0.66F;

    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F, currentDiffY = 0.0F;

    private final float scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
    private float scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;

    public BlankTabBar topBar;

    public List<String> mods = new ArrayList<>();

    public MenuCancelButton button = new MenuCancelButton();

    private final ScrollBar scrollBar;
    private final ModSidebar sidebar;
    private boolean clickedGroup = false;
    private float[] modPositions;

    private final List<CollapsibleModLabel> eventList;
    public EventPreview eventPreview = null;
    public Set<String> renderBlacklist = new HashSet<>();

    public static class Enum {
        @SpireEnum
        public static MainMenuScreen.CurScreen EVENT_SCREEN;
    }

    public EventScreen() {
        drawStartX = Settings.WIDTH / 4F;

        this.topBar = new BlankTabBar();
        this.scrollBar = new ScrollBar(this);

        mods.addAll(EventFilter.modEvents.keySet());
        mods.remove("Slay the Spire");
        mods.sort(String::compareTo);
        mods.add(0, "Slay the Spire");
        this.sidebar = new ModSidebar(this, mods);

        eventList = new ArrayList<>();
        for (String m : mods)
            eventList.add(new CollapsibleModLabel(m));

        calculateScrollBounds();
    }

    public static void prep() {
        if (EventPreview.scene != null) {
            EventPreview.scene.dispose();
            EventPreview.scene = null;
        }
        if (eventBackgroundImg != null) {
            eventBackgroundImg.dispose();
            eventBackgroundImg = null;
        }

        if (AbstractDungeon.miscRng == null) {
            Settings.seed = 0L;
            AbstractDungeon.generateSeeds();
        }

        EventPreview.scene = new DummyScene();
        AbstractDungeon.player = new DummyPlayer();
        AbstractDungeon.overlayMenu = new OverlayMenu(AbstractDungeon.player);
        AbstractDungeon.currMapNode = new MapRoomNode(1, 1);
        AbstractDungeon.currMapNode.room = new EventRoom();

        eventBackgroundImg = ImageMaster.loadImage("images/ui/event/panel.png");
    }

    public void open() {
        this.button.show(CardLibraryScreen.TEXT[0]);
        this.currentDiffY = this.scrollLowerBound;
        CardCrawlGame.mainMenuScreen.screen = Enum.EVENT_SCREEN;
        CardCrawlGame.mainMenuScreen.darken();

        if (eventPreview != null) {
            eventPreview.dispose();
            eventPreview = null;
        }
    }
    private void close() {
        AbstractDungeon.player = null;
        AbstractDungeon.overlayMenu = null;
        AbstractDungeon.currMapNode = null;
        if (EventPreview.scene != null) {
            EventPreview.scene.dispose();
            EventPreview.scene = null;
        }
        if (eventPreview != null) {
            eventPreview.dispose();
            eventPreview = null;
        }
        if (eventBackgroundImg != null) {
            eventBackgroundImg.dispose();
            eventBackgroundImg = null;
        }
    }

    public static boolean canHover;
    public void update() {
        this.topBar.update();
        canHover = !topBar.viewPreview.hovered;

        boolean isScrollBarScrolling = this.scrollBar.update();
        if (!isScrollBarScrolling)
            updateScrolling();

        this.button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            this.button.hb.clicked = false;
            this.button.hide();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
            close();
        }

        int offset = 0;
        for (CollapsibleModLabel cl : eventList) {
            if (!cl.events.isEmpty()) {
                offset = cl.update(this.currentDiffY + BlankTabBar.BASELINE, offset, this);
            }
        }
        this.sidebar.update(MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY));

        if (eventPreview != null) {
            try {
                if (eventPreview.update()) {
                    eventPreview.dispose();
                    eventPreview = null;
                }
            } catch (Exception e) {
                previewError();
            }
        }
    }

    private void updateScrolling() {
        if (clickedGroup)
            this.grabbedScreen = false;

        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.currentDiffY += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                this.currentDiffY -= Settings.SCROLL_SPEED;
            }
            if (InputHelper.justClickedLeft && !clickedGroup) {
                this.grabbedScreen = true;
                this.grabStartY = y - this.currentDiffY;
            }
        } else if (InputHelper.isMouseDown) {
            this.currentDiffY = y - this.grabStartY;
        } else {
            this.grabbedScreen = false;
        }
        resetScrolling();
        updateBarPosition();
        clickedGroup = false;
    }

    private void calculateScrollBounds() {
        int lines = 0;
        modPositions = new float[eventList.size()];

        for (int index = 0; index < eventList.size(); ++index) {
            modPositions[index] = scrollLowerBound + lines * LINEHEIGHT;
            lines += 2 + eventList.get(index).events.size() * 2;
        }
        if (lines > 20) {
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + lines * LINEHEIGHT;
        } else {
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        }

    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
        } else if (this.currentDiffY > this.scrollUpperBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
        }
    }

    public void render(SpriteBatch sb) {
        this.scrollBar.render(sb);

        renderGroup(sb);
        this.topBar.render(sb);
        this.sidebar.render(sb);

        this.button.render(sb);

        if (eventPreview != null) {
            try {
                eventPreview.render(sb);
            } catch (Exception e) {
                previewError();
            }
        }
    }

    private void renderGroup(SpriteBatch sb) {
        int offset = 0;
        for (CollapsibleModLabel cl : eventList) {
            if (!cl.events.isEmpty()) {
                offset = cl.render(sb, drawStartX, drawStartY + this.currentDiffY, offset);
            }
        }
    }

    public void didChangeMod(int index) {
        clickedGroup = true;
        this.currentDiffY = modPositions[index];
        updateBarPosition();
    }

    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    public void previewEvent(EventCheckboxLabel ecl) {
        float alpha = 0F;
        if (eventPreview != null) {
            if (eventPreview.parent == ecl) {
                eventPreview.ending = false;
                return;
            }
            eventPreview.dispose();
            alpha = eventPreview.alpha;
        }
        eventPreview = new EventPreview(ecl, alpha);

        if (eventPreview.fail)
            previewError();
    }

    private void previewError() {
        renderBlacklist.add(eventPreview.parent.info.ID);
        eventPreview.dispose();
        eventPreview = null;
    }
}