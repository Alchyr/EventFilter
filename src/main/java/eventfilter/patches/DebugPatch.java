package eventfilter.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;

public class DebugPatch {
    private static final StringBuilder sb = new StringBuilder();
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getShrine"
    )
    public static class ShrineLog {
        @SpirePrefixPatch
        public static void whatIsThere(Random rng) {
            sb.setLength(0);
            System.out.println("Getting shrine/special one time event:");
            if (!AbstractDungeon.shrineList.isEmpty()) {
                sb.append(AbstractDungeon.shrineList.size()).append(" SHRINE(S): ");
                for (String s : AbstractDungeon.shrineList) {
                    sb.append('[').append(s).append("] ");
                }
                System.out.println(sb);
            }
            else {
                System.out.println("No shrines available.");
            }
            sb.setLength(0);
            if (!AbstractDungeon.specialOneTimeEventList.isEmpty()) {
                sb.append(AbstractDungeon.specialOneTimeEventList.size()).append(" ONE TIME EVENT(S): ");
                for (String s : AbstractDungeon.specialOneTimeEventList) {
                    sb.append('[').append(s).append("] ");
                }
                System.out.println(sb);
            }
            else {
                System.out.println("No one time events available.");
            }
        }
    }
}
