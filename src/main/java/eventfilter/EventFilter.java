package eventfilter;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import eventfilter.patches.EventMenuButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@SpireInitializer
public class EventFilter implements
        PostInitializeSubscriber,
        EditStringsSubscriber {
    public static final Logger logger = LogManager.getLogger(EventFilter.class.getSimpleName());

    public static void initialize() {
        BaseMod.subscribe(new EventFilter());
    }

    public static String MOD_ID = "eventfilter";

    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MOD_ID + "/images/badge.png");
        ModPanel modPanel = new ModPanel();
        String[] text = CardCrawlGame.languagePack.getUIString(makeID(MOD_ID)).TEXT;
        BaseMod.registerModBadge(
                badgeTexture, text[0], text[1],
                text[2], modPanel);

        EventMenuButton.buttonText = text[3];

        loadConfigData();
    }

    @Override
    public void receiveEditStrings() {
        Settings.GameLanguage language = languageSupport();

        loadLocStrings(Settings.GameLanguage.ENG);
        if (!language.equals(Settings.GameLanguage.ENG)) {
            try {
                loadLocStrings(language);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private Settings.GameLanguage languageSupport() {
        switch (Settings.language) {
            default:
                return Settings.GameLanguage.ENG;
        }
    }
    private void loadLocStrings(Settings.GameLanguage language) {
        BaseMod.loadCustomStringsFile(UIStrings.class, MOD_ID + "/localization/" + language.name().toLowerCase() + "/UIStrings.json");
    }

    public static final Map<String, List<EventInfo>> modEvents = new HashMap<>(); //mod id -> event info (For display on event screen)
    public static final Map<String, String> IDToClassID = new HashMap<>(); //event String ID -> event class name
    public static final Map<String, Boolean> eventConfig = new HashMap<>(); //event class name -> enabled

    private static int id = 0; //used to match generated IDs

    static {
        modEvents.put("Slay the Spire", new ArrayList<>());
    }

    public static boolean enabled(String className) {
        if (eventConfig.getOrDefault(className, true))
        {
            return true;
        }
        logger.info(className + " is disabled.");
        return false;
    }

    //Used when events are registered with basemod
    public static void registerEvent(AddEventParams params) {
        String ID = params.eventID.replace(' ', '_');
        if (EventUtils.eventIDs.contains(ID)) {
            ID = ID + id++;
        }

        if (IDToClassID.containsKey(ID)) {
            if (!IDToClassID.get(ID).equals(params.eventClass.getName())) {
                logger.info("Second event with ID " + ID + " registered, with a different event class.");
            }
        }
        else {
            IDToClassID.put(ID, params.eventClass.getName());
        }

        String modID;

        try {
            modID = WhatMod.findModName(params.eventClass);
        } catch (Exception ignore) {
            modID = "???";
        }

        if (modID == null) { //Maybe Basegame?
            modID = "???";
            if (eventConfig.containsKey(params.eventClass.getName())) {
                for (EventInfo info : modEvents.get("Slay the Spire")) {
                    if (info.eventClass.equals(params.eventClass)) {
                        logger.info("Mod registers a basegame event. Adjusting existing properties.");
                        info.addInfo(params);

                        return;
                    }
                }
            }
            //if it hasn't been registered yet, it's not a basegame event (Probably.)
        }

        if (!modEvents.containsKey(modID))
            modEvents.put(modID, new ArrayList<>());

        modEvents.get(modID).add(new EventInfo(params.eventID, ID, params));
        eventConfig.putIfAbsent(params.eventClass.getName(), true);
    }

    private static void registerEvent(String ID, Class<? extends AbstractEvent> eventClass, EventUtils.EventType type) {
        modEvents.get("Slay the Spire").add(new EventInfo(ID, eventClass, type));
        IDToClassID.put(ID, eventClass.getName());
        eventConfig.putIfAbsent(eventClass.getName(), true);
    }
    private static void registerEvent(String ID, Class<? extends AbstractEvent> eventClass, EventUtils.EventType type, String... acts) {
        modEvents.get("Slay the Spire").add(new EventInfo(ID, eventClass, type, acts));
        IDToClassID.put(ID, eventClass.getName());
        eventConfig.putIfAbsent(eventClass.getName(), true);
    }
    private static void registerEvent(String ID, Class<? extends AbstractEvent> eventClass, String actID) {
        modEvents.get("Slay the Spire").add(new EventInfo(ID, eventClass, actID));
        IDToClassID.put(ID, eventClass.getName());
        eventConfig.putIfAbsent(eventClass.getName(), true);
    }
    public static void registerBasegameEvents() { //called in prefix patch on post initialize to ensure it happens first
        logger.info("Registering basegame events.");
        //Shrines
        registerEvent(GremlinMatchGame.ID, GremlinMatchGame.class, EventUtils.EventType.SHRINE);
        registerEvent(GoldShrine.ID, GoldShrine.class, EventUtils.EventType.SHRINE);
        registerEvent(Transmogrifier.ID, Transmogrifier.class, EventUtils.EventType.SHRINE);
        registerEvent(PurificationShrine.ID, PurificationShrine.class, EventUtils.EventType.SHRINE);
        registerEvent(UpgradeShrine.ID, UpgradeShrine.class, EventUtils.EventType.SHRINE);
        registerEvent(GremlinWheelGame.ID, GremlinWheelGame.class, EventUtils.EventType.SHRINE);

        //OneTimeEvents
        registerEvent(AccursedBlacksmith.ID, AccursedBlacksmith.class, EventUtils.EventType.ONE_TIME);
        registerEvent(Bonfire.ID, Bonfire.class, EventUtils.EventType.ONE_TIME);
        registerEvent(Designer.ID, Designer.class, EventUtils.EventType.ONE_TIME, TheCity.ID, TheBeyond.ID); //must have at least 75 gold
        registerEvent(Duplicator.ID, Duplicator.class, EventUtils.EventType.ONE_TIME, TheCity.ID, TheBeyond.ID);
        registerEvent(FaceTrader.ID, FaceTrader.class, EventUtils.EventType.ONE_TIME, Exordium.ID, TheCity.ID);
        registerEvent(FountainOfCurseRemoval.ID, FountainOfCurseRemoval.class, EventUtils.EventType.ONE_TIME); //must be cursed
        registerEvent(KnowingSkull.ID, KnowingSkull.class, EventUtils.EventType.ONE_TIME, TheCity.ID); //must have at least 12 HP
        registerEvent(Lab.ID, Lab.class, EventUtils.EventType.ONE_TIME);
        registerEvent(Nloth.ID, Nloth.class, EventUtils.EventType.ONE_TIME, TheCity.ID); //must have at least 2 relics
        registerEvent(NoteForYourself.ID, NoteForYourself.class, EventUtils.EventType.ONE_TIME);
        registerEvent(SecretPortal.ID, SecretPortal.class, EventUtils.EventType.ONE_TIME, TheBeyond.ID); //Must be at least 800 seconds in
        registerEvent(TheJoust.ID, TheJoust.class, EventUtils.EventType.ONE_TIME, TheCity.ID); //Must have at least 50 gold
        registerEvent(WeMeetAgain.ID, WeMeetAgain.class, EventUtils.EventType.ONE_TIME);
        registerEvent(WomanInBlue.ID, WomanInBlue.class, EventUtils.EventType.ONE_TIME); //Must have at least 50 gold

        //Act 1
        registerEvent(BigFish.ID, BigFish.class, Exordium.ID);
        registerEvent(Cleric.ID, Cleric.class, Exordium.ID);
        registerEvent(DeadAdventurer.ID, DeadAdventurer.class, Exordium.ID);
        registerEvent(GoldenIdolEvent.ID, GoldenIdolEvent.class, Exordium.ID);
        registerEvent(GoldenWing.ID, GoldenWing.class, Exordium.ID);
        registerEvent(GoopPuddle.ID, GoopPuddle.class, Exordium.ID);
        registerEvent(Sssserpent.ID, Sssserpent.class, Exordium.ID);
        registerEvent(LivingWall.ID, LivingWall.class, Exordium.ID);
        registerEvent(Mushrooms.ID, Mushrooms.class, Exordium.ID);
        registerEvent(ScrapOoze.ID, ScrapOoze.class, Exordium.ID);
        registerEvent(ShiningLight.ID, ShiningLight.class, Exordium.ID);

        //Act 2
        registerEvent(Addict.ID, Addict.class, TheCity.ID);
        registerEvent(BackToBasics.ID, BackToBasics.class, TheCity.ID);
        registerEvent(Beggar.ID, Beggar.class, TheCity.ID);
        registerEvent(Colosseum.ID, Colosseum.class, TheCity.ID);
        registerEvent(CursedTome.ID, CursedTome.class, TheCity.ID);
        registerEvent(DrugDealer.ID, DrugDealer.class, TheCity.ID);
        registerEvent(ForgottenAltar.ID, ForgottenAltar.class, TheCity.ID);
        registerEvent(Ghosts.ID, Ghosts.class, TheCity.ID);
        registerEvent(MaskedBandits.ID, MaskedBandits.class, TheCity.ID);
        registerEvent(Nest.ID, Nest.class, TheCity.ID);
        registerEvent(TheLibrary.ID, TheLibrary.class, TheCity.ID);
        registerEvent(TheMausoleum.ID, TheMausoleum.class, TheCity.ID);
        registerEvent(Vampires.ID, Vampires.class, TheCity.ID);

        //Act 3
        registerEvent(Falling.ID, Falling.class, TheBeyond.ID);
        registerEvent(MindBloom.ID, MindBloom.class, TheBeyond.ID);
        registerEvent(MoaiHead.ID, MoaiHead.class, TheBeyond.ID);
        registerEvent(MysteriousSphere.ID, MysteriousSphere.class, TheBeyond.ID);
        registerEvent(SensoryStone.ID, SensoryStone.class, TheBeyond.ID);
        registerEvent(TombRedMask.ID, TombRedMask.class, TheBeyond.ID);
        registerEvent(WindingHalls.ID, WindingHalls.class, TheBeyond.ID);
    }



    public static void clearConfigData() {
        try {
            SpireConfig config = new SpireConfig(MOD_ID, "config");
            config.clear();
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadConfigData() {
        try {
            logger.info("Loading event config...");

            SpireConfig config = new SpireConfig(MOD_ID, "config");

            for (Map.Entry<Object, Object> tmp : ((Properties) ReflectionHacks.getPrivate(config, SpireConfig.class, "properties")).entrySet()) {
                if (tmp.getKey() instanceof String) {
                    String eventClassName = (String) tmp.getKey();

                    logger.info(" - " + eventClassName + " is disabled.");
                    eventConfig.put(eventClassName, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveConfigData();
        }
    }

    public static void saveConfigData() {
        try {
            SpireConfig config = new SpireConfig(MOD_ID, "config");

            config.clear();
            for (Map.Entry<String, Boolean> event : eventConfig.entrySet()) {
                if (!event.getValue()) //if disabled
                    config.setString(event.getKey(), "disabled");
            }

            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}