package eventfilter.dummy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.scenes.AbstractScene;

public class DummyScene extends AbstractScene {
    private boolean renderLeftWall;
    private boolean renderSolidMid;
    private boolean renderHollowMid;
    private TextureAtlas.AtlasRegion fg;
    private TextureAtlas.AtlasRegion mg;
    private TextureAtlas.AtlasRegion leftWall;
    private TextureAtlas.AtlasRegion hollowWall;
    private TextureAtlas.AtlasRegion solidWall;
    private boolean renderCeilingMod1;
    private boolean renderCeilingMod2;
    private boolean renderCeilingMod3;
    private boolean renderCeilingMod4;
    private boolean renderCeilingMod5;
    private boolean renderCeilingMod6;
    private TextureAtlas.AtlasRegion ceiling;
    private TextureAtlas.AtlasRegion ceilingMod1;
    private TextureAtlas.AtlasRegion ceilingMod2;
    private TextureAtlas.AtlasRegion ceilingMod3;
    private TextureAtlas.AtlasRegion ceilingMod4;
    private TextureAtlas.AtlasRegion ceilingMod5;
    private TextureAtlas.AtlasRegion ceilingMod6;
    private Color overlayColor;
    private Color whiteColor;

    public DummyScene() {
        super("bottomScene/scene.atlas");
        this.overlayColor = Color.WHITE.cpy();
        this.whiteColor = Color.WHITE.cpy();
        this.fg = this.atlas.findRegion("mod/fg");
        this.mg = this.atlas.findRegion("mod/mg");
        this.leftWall = this.atlas.findRegion("mod/mod1");
        this.hollowWall = this.atlas.findRegion("mod/mod2");
        this.solidWall = this.atlas.findRegion("mod/midWall");
        this.ceiling = this.atlas.findRegion("mod/ceiling");
        this.ceilingMod1 = this.atlas.findRegion("mod/ceilingMod1");
        this.ceilingMod2 = this.atlas.findRegion("mod/ceilingMod2");
        this.ceilingMod3 = this.atlas.findRegion("mod/ceilingMod3");
        this.ceilingMod4 = this.atlas.findRegion("mod/ceilingMod4");
        this.ceilingMod5 = this.atlas.findRegion("mod/ceilingMod5");
        this.ceilingMod6 = this.atlas.findRegion("mod/ceilingMod6");
    }

    public void update() {
        super.update();
    }

    public void nextRoom(AbstractRoom room) {
        super.nextRoom(room);
        this.randomizeScene();
    }

    public void randomizeScene() {
        if (MathUtils.randomBoolean()) {
            this.renderSolidMid = false;
            this.renderLeftWall = false;
            this.renderHollowMid = true;
            if (MathUtils.randomBoolean()) {
                this.renderSolidMid = true;
                if (MathUtils.randomBoolean()) {
                    this.renderLeftWall = true;
                }
            }
        } else {
            this.renderLeftWall = false;
            this.renderHollowMid = false;
            this.renderSolidMid = true;
            if (MathUtils.randomBoolean()) {
                this.renderLeftWall = true;
            }
        }

        this.renderCeilingMod1 = MathUtils.randomBoolean();
        this.renderCeilingMod2 = MathUtils.randomBoolean();
        this.renderCeilingMod3 = MathUtils.randomBoolean();
        this.renderCeilingMod4 = MathUtils.randomBoolean();
        this.renderCeilingMod5 = MathUtils.randomBoolean();
        this.renderCeilingMod6 = MathUtils.randomBoolean();
        this.overlayColor.r = MathUtils.random(0.0F, 0.05F);
        this.overlayColor.g = MathUtils.random(0.0F, 0.2F);
        this.overlayColor.b = MathUtils.random(0.0F, 0.2F);
    }

    public void renderCombatRoomBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.bg, true);

        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.mg, true);
        if (this.renderHollowMid && (this.renderSolidMid || this.renderLeftWall)) {
            sb.setColor(Color.GRAY);
        }

        this.renderAtlasRegionIf(sb, this.solidWall, this.renderSolidMid);
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.hollowWall, this.renderHollowMid);
        this.renderAtlasRegionIf(sb, this.leftWall, this.renderLeftWall);
        this.renderAtlasRegionIf(sb, this.ceiling, true);
        this.renderAtlasRegionIf(sb, this.ceilingMod1, this.renderCeilingMod1);
        this.renderAtlasRegionIf(sb, this.ceilingMod2, this.renderCeilingMod2);
        this.renderAtlasRegionIf(sb, this.ceilingMod3, this.renderCeilingMod3);
        this.renderAtlasRegionIf(sb, this.ceilingMod4, this.renderCeilingMod4);
        this.renderAtlasRegionIf(sb, this.ceilingMod5, this.renderCeilingMod5);
        this.renderAtlasRegionIf(sb, this.ceilingMod6, this.renderCeilingMod6);

        sb.setBlendFunction(768, 1);
        sb.setColor(this.overlayColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
        sb.setBlendFunction(770, 771);
    }

    public void renderCombatRoomFg(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.fg, true);
    }

    public void renderCampfireRoom(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireBg, true);
        sb.setBlendFunction(770, 1);
        this.whiteColor.a = MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) / 10.0F + 0.8F;
        sb.setColor(this.whiteColor);
        this.renderQuadrupleSize(sb, this.campfireGlow, !CampfireUI.hidden);
        sb.setBlendFunction(770, 771);
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireKindling, true);
    }
}