package fuzs.strawstatues.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.strawstatues.client.gui.screens.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.StrawStatueScaleScreen;
import fuzs.strawstatues.init.ModRegistry;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(ModRegistry.STRAW_STATUE_ITEM.value(), "Straw Statue");
        builder.add(ModRegistry.STRAW_STATUE_ITEM.value(), "named", "%s's Statue");
        builder.add(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), "Straw Statue");
        builder.add(ModRegistry.MODEL_PARTS_SCREEN_TYPE.getTranslationKey(), "Model Parts");
        builder.add(ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE.getTranslationKey(), "Scale & Rotations");
        builder.add(StrawStatueModelPartsScreen.TEXT_BOX_TRANSLATION_KEY, "Set a player name to take the statue skin from.");
        builder.add(StrawStatueModelPartsScreen.REFRESH_SKIN_TRANSLATION_KEY, "No statue skin was found. Click to try again.");
        builder.add(ModRegistry.SLIM_ARMS_STYLE_OPTION.getTranslationKey(), "Slim Arms");
        builder.add(ModRegistry.SLIM_ARMS_STYLE_OPTION.getDescriptionKey(), "Makes the statue's arms a few pixels less wide, like Minecraft's Alex skin.");
        builder.add(ModRegistry.CROUCHING_STYLE_OPTION.getTranslationKey(), "Crouching");
        builder.add(ModRegistry.CROUCHING_STYLE_OPTION.getDescriptionKey(), "Makes the statue show as if it were crouching.");
        builder.add(ModRegistry.CAPE_POSE_PART_MUTATOR.getTranslationKey(), "Cape");
        builder.add(StrawStatueScaleScreen.SCALE_TRANSLATION_KEY, "Scale:");
        builder.add(StrawStatueScaleScreen.ROTATION_X_TRANSLATION_KEY, "X-Rotation:");
        builder.add(StrawStatueScaleScreen.ROTATION_Y_TRANSLATION_KEY, "Y-Rotation:");
        builder.add(StrawStatueScaleScreen.ROTATION_Z_TRANSLATION_KEY, "Z-Rotation:");
    }
}
