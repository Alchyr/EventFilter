package eventfilter.dummy;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Equilibrium;
import com.megacrit.cardcrawl.cards.green.Backflip;
import com.megacrit.cardcrawl.cards.green.CalculatedGamble;
import com.megacrit.cardcrawl.cards.green.Footwork;
import com.megacrit.cardcrawl.cards.purple.EmptyFist;
import com.megacrit.cardcrawl.cards.purple.TalkToTheHand;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbRed;

import java.util.ArrayList;

public class DummyPlayer extends CustomPlayer {
    public DummyPlayer() {
        super("", PlayerClass.IRONCLAD, new EnergyOrbRed(), new NoneAnimation());

        initializeClass(null, // required call to load textures and setup energy/loadout
                "eventfilter/images/blank.png", // campfire pose
                "eventfilter/images/blank.png", // another campfire pose
                "eventfilter/images/blank.png", // dead corpse
                getLoadout(), 0.0F, 0.0F, 1.0F, 1.0F, new EnergyManager(3));

        this.dialogX = 0;
        this.dialogY = 0;

        this.masterDeck.group.add(new Bash());
        this.masterDeck.group.add(new Strike_Red());
        this.masterDeck.group.add(new Defend_Red());
        this.masterDeck.group.add(new Bludgeon());
        this.masterDeck.group.add(new DemonForm());
        this.masterDeck.group.add(new CalculatedGamble());
        this.masterDeck.group.add(new TalkToTheHand());
        this.masterDeck.group.add(new Footwork());
        this.masterDeck.group.add(new Equilibrium());
        this.masterDeck.group.add(new EmptyFist());
        this.masterDeck.group.add(new Backflip());

        this.gold = 500;
        this.currentHealth = 50;
        this.maxHealth = 100;
        this.relics.add(new BurningBlood());
        this.relics.add(new Abacus());
        this.relics.add(new BlackStar());
        this.relics.add(new IncenseBurner());
        this.relics.add(new FossilizedHelix());
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo("", "",
                2, 2, 0, 500, 5, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> startDeck = new ArrayList<>();
        startDeck.add(Strike_Red.ID);
        startDeck.add(Defend_Red.ID);
        startDeck.add(DemonForm.ID);
        startDeck.add(Carnage.ID);
        return startDeck;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> startingRelics = new ArrayList<>();

        startingRelics.add(BurningBlood.ID);

        return startingRelics;
    }

    @Override
    public Texture getEnergyImage() {
        return ImageMaster.RED_ORB_FLASH_VFX;
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return "";
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return AbstractCard.CardColor.RED;
    }

    @Override
    public Color getCardRenderColor() {
        return Color.WHITE.cpy();
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Bash();
    }

    @Override
    public Color getCardTrailColor() {
        return Color.WHITE.cpy();
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 1;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {

    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_FIRE";
    }

    @Override
    public String getLocalizedCharacterName() {
        return "";
    }

    @Override
    public AbstractPlayer newInstance() {
        return new DummyPlayer();
    }

    @Override
    public String getSpireHeartText() {
        return "";
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.WHITE.cpy();
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.SMASH
        };
    }

    @Override
    public String getVampireText() {
        return "";
    }
}
