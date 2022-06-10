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
    @SpirePostfixPatch
    public static void Postfix(AddEventParams params) {
        EventFilter.registerEvent(params);
    }
}
