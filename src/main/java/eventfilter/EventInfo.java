package eventfilter;

import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventInfo {
    public final String originalID;
    public final String ID;
    public final Class<? extends AbstractEvent> eventClass;
    public EventUtils.EventType type;
    public String[] appearance;
    public Set<String> characters = null;

    public EventInfo(String originalID, String ID, AddEventParams params) {
        this.originalID = originalID;
        this.ID = ID;
        this.eventClass = params.eventClass;

        if (params.playerClasses != null && !params.playerClasses.isEmpty()) {
            characters = new HashSet<>();

            Set<AbstractPlayer.PlayerClass> playerClassSet = new HashSet<>(params.playerClasses);

            for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
                if (playerClassSet.remove(p.chosenClass)) {
                    String name = p.getLocalizedCharacterName();
                    if (name != null)
                        characters.add(name);

                }
            }

            for (AbstractPlayer.PlayerClass leftover : playerClassSet) {
                characters.add(leftover.name());
            }
        }

        if (params.overrideEventID != null && params.eventType != EventUtils.EventType.FULL_REPLACE)
            this.type = EventUtils.EventType.OVERRIDE;
        else
            this.type = params.eventType;

        switch (type) {
            case ONE_TIME:
                appearance = new String[] { "Any" };
                break;
            case OVERRIDE:
            case FULL_REPLACE:
                if (params.overrideEventID != null) {
                    String overrideName = EventHelper.getEventName(params.overrideEventID);
                    if (overrideName.equals(""))
                        overrideName = params.overrideEventID;
                    appearance = new String[] { overrideName };
                    break;
                } //Treated as a normal event if override is not defined properly
                type = EventUtils.EventType.NORMAL;
            default:
                if (params.dungeonIDs.isEmpty())
                    appearance = new String[] { "Any" };
                else {
                    this.appearance = new String[params.dungeonIDs.size()];
                    for (int i = 0; i < params.dungeonIDs.size(); ++i) {
                        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(params.dungeonIDs.get(i));

                        if (uiStrings != null && uiStrings.TEXT != null && uiStrings.TEXT.length > 0)
                            this.appearance[i] = uiStrings.TEXT[0];
                        else
                            this.appearance[i] = params.dungeonIDs.get(i);
                    }
                }
                break;
        }
    }

    //Basegame normal events
    public EventInfo(String ID, Class<? extends AbstractEvent> eventClass, String act) {
        this.originalID = this.ID = ID;
        this.eventClass = eventClass;
        this.type = EventUtils.EventType.NORMAL;

        this.appearance = new String[1];
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(act);

        if (uiStrings != null && uiStrings.TEXT != null && uiStrings.TEXT.length > 0)
            this.appearance[0] = uiStrings.TEXT[0];
        else
            this.appearance[0] = act;
    }
    //Basegame shrines/onetime events
    public EventInfo(String ID, Class<? extends AbstractEvent> eventClass, EventUtils.EventType type) {
        this(ID, eventClass, type, "Any");
    }
    public EventInfo(String ID, Class<? extends AbstractEvent> eventClass, EventUtils.EventType type, String... acts) {
        this.originalID = this.ID = ID;
        this.eventClass = eventClass;
        this.type = EventUtils.EventType.NORMAL;

        this.appearance = new String[acts.length];
        for (int i = 0; i < acts.length; ++i) {
            UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(acts[i]);

            if (uiStrings != null && uiStrings.TEXT != null && uiStrings.TEXT.length > 0)
                this.appearance[i] = uiStrings.TEXT[0];
            else
                this.appearance[i] = acts[i];
        }
    }

    public void addInfo(AddEventParams params) {
        //event class should be the same
        switch (type) {
            case SHRINE:
            case NORMAL:
                if (params.dungeonIDs.isEmpty())
                    //having it registered this way will make it be added to any act
                    appearance = new String[] { "Any" };
                else {
                    if (this.appearance.length == 1 && this.appearance[0].equals("Any")) {
                        return;
                    }
                    else {
                        HashSet<String> dungeons = new HashSet<>(Arrays.asList(appearance));

                        for (int i = 0; i < params.dungeonIDs.size(); ++i) {
                            UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(params.dungeonIDs.get(i));

                            if (uiStrings != null && uiStrings.TEXT != null && uiStrings.TEXT.length > 0)
                                dungeons.add(uiStrings.TEXT[0]);
                            else
                                dungeons.add(params.dungeonIDs.get(i));
                        }

                        appearance = new String[dungeons.size()];
                        int i = 0;
                        for (String s : dungeons) {
                            appearance[i++] = s;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getTypeName()).append(" | ");
        int i = 0 ;
        for (; i < appearance.length - 1; ++i) {
            sb.append(appearance[i]).append(", ");
        }
        if (i < appearance.length)
            sb.append(appearance[i]);

        if (characters != null) {
            sb.append(" | ");

            i = 0;
            for (String s : characters) {
                ++i;
                sb.append(s);
                if (i < characters.size())
                    sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String getTypeName() {
        switch (type) {
            case NORMAL:
                return "Normal";
            case SHRINE:
                return "Shrine";
            case ONE_TIME:
                return "One Time";
            case OVERRIDE:
                return "Override";
            case FULL_REPLACE:
                return "Replace";
            default:
                return type.name();
        }
    }
}
