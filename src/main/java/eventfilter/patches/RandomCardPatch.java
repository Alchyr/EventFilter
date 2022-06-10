package eventfilter.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.GoodInstincts;
import com.megacrit.cardcrawl.cards.colorless.SwiftStrike;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import eventfilter.screen.EventScreen;

public class RandomCardPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getCard",
            paramtypez = { AbstractCard.CardRarity.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getCard",
            paramtypez = { AbstractCard.CardRarity.class, Random.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getCardWithoutRng",
            paramtypez = { AbstractCard.CardRarity.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getCardFromPool",
            paramtypez = { AbstractCard.CardRarity.class, AbstractCard.CardType.class, boolean.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getColorlessCardFromPool",
            paramtypez = { AbstractCard.CardRarity.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnColorlessCard",
            paramtypez = {}
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnColorlessCard",
            paramtypez = { AbstractCard.CardRarity.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomColorlessCardFromAvailable",
            paramtypez = { AbstractCard.class, Random.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomColorlessCardInCombat",
            paramtypez = { Random.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomColorlessCardFromAvailable",
            paramtypez = { String.class, Random.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomCardFromAvailable",
            paramtypez = { AbstractCard.class, Random.class }
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnRandomCard",
            paramtypez = {}
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomCard",
            paramtypez = {}
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomCardInCombat",
            paramtypez = {}
    )
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomCardInCombat",
            paramtypez = { AbstractCard.CardType.class }
    )
    public static class Colorless {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> eventScreen() {
            if (CardCrawlGame.mainMenuScreen.screen == EventScreen.Enum.EVENT_SCREEN) {
                return SpireReturn.Return(MathUtils.randomBoolean() ? new SwiftStrike() : new GoodInstincts());
            }
            return SpireReturn.Continue();
        }
    }
}
