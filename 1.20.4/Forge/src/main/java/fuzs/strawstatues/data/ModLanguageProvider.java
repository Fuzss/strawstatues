package fuzs.strawstatues.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatueScaleScreen;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        // Straw Statues
        this.add(ModRegistry.STRAW_STATUE_ITEM.get(), "Straw Statue");
        this.add(ModRegistry.STRAW_STATUE_ENTITY_TYPE.get(), "Straw Statue");
        this.add(ModRegistry.MODEL_PARTS_SCREEN_TYPE.getTranslationKey(), "Model Parts");
        this.add(ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE.getTranslationKey(), "Scale & Rotations");
        this.add(StrawStatueModelPartsScreen.TEXT_BOX_TRANSLATION_KEY, "Set a player name to take the statue skin from.");
        this.add(ModRegistry.SLIM_ARMS_STYLE_OPTION.getTranslationKey(), "Slim Arms");
        this.add(ModRegistry.SLIM_ARMS_STYLE_OPTION.getDescriptionKey(), "Makes the statue's arms a few pixels less wide, like Minecraft's Alex skin.");
        this.add(ModRegistry.CROUCHING_STYLE_OPTION.getTranslationKey(), "Crouching");
        this.add(ModRegistry.CROUCHING_STYLE_OPTION.getDescriptionKey(), "Makes the statue show as if it were crouching.");
        this.add(ModRegistry.CAPE_POSE_PART_MUTATOR.getTranslationKey(), "Cape");
        this.add(StrawStatueScaleScreen.SCALE_TRANSLATION_KEY, "Scale:");
        this.add(StrawStatueScaleScreen.ROTATION_X_TRANSLATION_KEY, "X-Rotation:");
        this.add(StrawStatueScaleScreen.ROTATION_Y_TRANSLATION_KEY, "Y-Rotation:");
        this.add(StrawStatueScaleScreen.ROTATION_Z_TRANSLATION_KEY, "Z-Rotation:");
    }
}
