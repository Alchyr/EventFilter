package eventfilter.patches.crossmod;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.random.Random;

@SpirePatch(
        optional = true,
        cls = "BattleTowers.towers.BattleTower$TowerContents",
        method = "getEvent"
)
public class BattleTower {
    @SpirePrefixPatch
    public static void removeDisabledEvents(Object __instance, Random rng) {
        //Removes all disabled events before rolling the event.
        //This means if all events are disabled, all events will immediately be added back to the pool resulting in no difference.
        BattleTowerSub.removeEvents(__instance);
    }
}
