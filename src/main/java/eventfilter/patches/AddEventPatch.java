package eventfilter.patches;

import basemod.BaseMod;
import basemod.eventUtil.AddEventParams;
import com.evacipated.cardcrawl.modthespire.lib.*;
import eventfilter.EventFilter;

@SpirePatch(
        clz = BaseMod.class,
        method = "addEvent",
        paramtypez = { AddEventParams.class }
)
public class AddEventPatch {
    @SpirePrefixPatch
    public static void Prefix(AddEventParams params) {
        EventFilter.registerEvent(params);
    }
}
