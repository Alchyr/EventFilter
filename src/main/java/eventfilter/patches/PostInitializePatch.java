package eventfilter.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import eventfilter.EventFilter;
import eventfilter.dummy.DummyPlayer;

import static eventfilter.EventFilter.registerBasegameEvents;

@SpirePatch(
        clz = BaseMod.class,
        method = "publishPostInitialize"
)
public class PostInitializePatch {
    @SpirePrefixPatch
    public static void setupDummyPlayer() {
        registerBasegameEvents();
        EventFilter.logger.info("Setting up dummmy player.");
        AbstractDungeon.player = new DummyPlayer();
    }

    @SpirePostfixPatch
    public static void clearDummyPlayer() {
        if (AbstractDungeon.player instanceof DummyPlayer) {
            EventFilter.logger.info("Clearing dummmy player.");
            AbstractDungeon.player = null;
        }
        else {
            EventFilter.logger.info("Player is no longer a dummy?");
        }
    }
}
