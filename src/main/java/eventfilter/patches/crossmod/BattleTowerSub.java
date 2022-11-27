package eventfilter.patches.crossmod;

import BattleTowers.towers.BattleTower;
import basemod.ReflectionHacks;
import eventfilter.EventFilter;

import java.util.List;

public class BattleTowerSub {
    public static void removeEvents(Object obj) {
        if (obj instanceof BattleTower.TowerContents) {
            List<String> events = ReflectionHacks.getPrivate(obj, BattleTower.TowerContents.class, "events");
            events.removeIf((id)->!EventFilter.eventIDEnabled(id));
        }
    }
}
