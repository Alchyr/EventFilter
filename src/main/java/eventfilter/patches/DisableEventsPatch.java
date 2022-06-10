package eventfilter.patches;

import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.eventUtil.util.Condition;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import eventfilter.EventFilter;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Iterator;

import static eventfilter.EventFilter.logger;

public class DisableEventsPatch {
    //Disable custom events
    @SpirePatch(
            clz = AddEventParams.Builder.class,
            method = "create"
    )
    public static class FilterCondition {
        @SpirePostfixPatch
        public static AddEventParams adjust(AddEventParams __result, AddEventParams.Builder __instance) {
            //Spawn condition affects all events other than override events
            String className = __result.eventClass.getName();
            if (__result.overrideEventID != null && __result.eventType != EventUtils.EventType.FULL_REPLACE) {
                //This is an override event.
                if (__result.bonusCondition != null) {
                    Condition original = __result.bonusCondition;
                    __result.bonusCondition = () -> original.test() && EventFilter.enabled(className);
                }
                else {
                    __result.bonusCondition = () -> EventFilter.enabled(className);
                }
            }
            else
            {
                if (__result.spawnCondition != null) {
                    Condition original = __result.spawnCondition;
                    __result.spawnCondition = () -> original.test() && EventFilter.enabled(className);
                }
                else {
                    __result.spawnCondition = () -> EventFilter.enabled(className);
                }
            }
            return __result;
        }
    }


    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, String.class, AbstractPlayer.class, ArrayList.class }
    )
    public static class NormalAndShrineEvents {
        @SpireInsertPatch(
                locator = EventLocator.class
        )
        public static void insert(AbstractDungeon __instance, String a, String b, AbstractPlayer c, ArrayList<String> bup) {
            logger.info("Filtering normal events and shrines on Dungeon Instantiation");

            String ID;
            Iterator<String> eventIterator = AbstractDungeon.eventList.iterator();
            while (eventIterator.hasNext()) {
                ID = eventIterator.next();
                String className = EventFilter.IDToClassID.get(ID);

                if (!EventFilter.enabled(className)) {
                    logger.info(" - " + ID + " removed from list.");
                    eventIterator.remove();
                }
            }

            eventIterator = AbstractDungeon.shrineList.iterator();
            while (eventIterator.hasNext()) {
                ID = eventIterator.next();
                String className = EventFilter.IDToClassID.get(ID);

                if (!EventFilter.enabled(className)) {
                    logger.info(" - " + ID + " removed from list.");
                    eventIterator.remove();
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, AbstractPlayer.class, SaveFile.class}
    )
    public static class SaveAndLoadShrineEvents {
        @SpireInsertPatch(
                locator = EventLocator.class
        )
        public static void insert(AbstractDungeon __instance, String a, AbstractPlayer who, SaveFile why) {
            logger.info("Filtering Shrine List on load.");

            String ID;
            Iterator<String> eventIterator = AbstractDungeon.shrineList.iterator();
            while (eventIterator.hasNext()) {
                ID = eventIterator.next();
                String className = EventFilter.IDToClassID.get(ID);

                if (!EventFilter.enabled(className)) {
                    logger.info(" - " + ID + " removed from list.");
                    eventIterator.remove();
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeSpecialOneTimeEventList"
    )
    public static class OneTimeEvents {
        @SpirePostfixPatch
        public static void postfix(AbstractDungeon __instance) {
            logger.info("Filtering SpecialOneTimeEvents.");

            String ID;
            Iterator<String> eventIterator = AbstractDungeon.specialOneTimeEventList.iterator();
            while (eventIterator.hasNext()) {
                ID = eventIterator.next();
                String className = EventFilter.IDToClassID.get(ID);

                if (!EventFilter.enabled(className)) {
                    logger.info(" - " + ID + " removed from list.");
                    eventIterator.remove();
                }
            }
        }
    }

    private static class EventLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "initializeCardPools");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
