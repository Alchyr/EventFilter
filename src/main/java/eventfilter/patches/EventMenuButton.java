package eventfilter.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import eventfilter.screen.EventScreen;

import java.lang.reflect.Field;

public class EventMenuButton {
    @SpireEnum
    static MenuButton.ClickResult EVENTSCREEN;

    static EventScreen eventScreen = null;

    public static String buttonText = "";

    @SpirePatch(
            clz=MenuButton.class,
            method="setLabel"
    )
    public static class SetLabel
    {
        @SpirePostfixPatch
        public static void Postfix(MenuButton __instance)
        {
            try {
                if (__instance.result == EVENTSCREEN) {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, buttonText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
            clz=MenuButton.class,
            method="buttonEffect"
    )
    public static class ButtonEffect
    {
        @SpirePostfixPatch
        public static void Postfix(MenuButton __instance)
        {
            if (__instance.result == EVENTSCREEN) {
                EventScreen.prep();
                if (eventScreen == null) {
                    eventScreen = new EventScreen();
                }
                eventScreen.open();
            }
        }
    }
}
