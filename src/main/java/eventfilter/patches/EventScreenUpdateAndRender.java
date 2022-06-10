package eventfilter.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import eventfilter.screen.EventScreen;

public class EventScreenUpdateAndRender {
    @SpirePatch(
            clz=MainMenuScreen.class,
            method="update"
    )
    public static class Update {
        public static void Postfix(MainMenuScreen __instance) {
            if (__instance.screen == EventScreen.Enum.EVENT_SCREEN) {
                EventMenuButton.eventScreen.update();
            }
        }
    }

    @SpirePatch(
            clz=MainMenuScreen.class,
            method="render"
    )
    public static class Render {
        public static void Postfix(MainMenuScreen __instance, SpriteBatch sb) {
            if (__instance.screen == EventScreen.Enum.EVENT_SCREEN) {
                EventMenuButton.eventScreen.render(sb);
            }
        }
    }
}
