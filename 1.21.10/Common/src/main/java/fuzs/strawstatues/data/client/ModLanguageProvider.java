package fuzs.strawstatues.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.strawstatues.client.gui.screens.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.StrawStatueScaleScreen;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.inventory.data.StrawStatuePosePartMutators;
import fuzs.strawstatues.world.inventory.data.StrawStatueScreenTypes;
import fuzs.strawstatues.world.inventory.data.StrawStatueStyleOptions;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(ModRegistry.STRAW_STATUE_ITEM.value(), "Straw Statue");
        builder.add(ModRegistry.STRAW_STATUE_ITEM.value(), "named", "%s's Statue");
        builder.add(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), "Straw Statue");
        builder.add(StrawStatueScreenTypes.ROTATIONS.getTranslationKey(), "Rotations");
        builder.add(StrawStatueScreenTypes.STYLE.getTranslationKey(), "Style");
        builder.add(StrawStatueScreenTypes.MODEL_PARTS.getTranslationKey(), "Model Parts");
        builder.add(StrawStatueScreenTypes.POSITION.getTranslationKey(), "Position");
        builder.add(StrawStatueScreenTypes.SCALE.getTranslationKey(), "Scale & Rotations");
        builder.add(StrawStatueModelPartsScreen.TEXT_BOX_HINT_TRANSLATION_KEY, "Player Skin");
        builder.add(StrawStatueModelPartsScreen.TEXT_BOX_TOOLTIP_TRANSLATION_KEY,
                "Set a player name to take the statue skin from.");
        builder.add(StrawStatueStyleOptions.SMALL.getTranslationKey(), "Small");
        builder.add(StrawStatueStyleOptions.SMALL.getDescriptionKey(),
                "Makes the statue half it's size like a baby mob.");
        builder.add(StrawStatueStyleOptions.PUSHABLE.getTranslationKey(), "Pushable");
        builder.add(StrawStatueStyleOptions.PUSHABLE.getDescriptionKey(),
                "Allows other entities to push the statue around. This enables it to enter vehicles such as minecarts and boats.");
        builder.add(StrawStatueStyleOptions.DYNAMIC_PROFILE.getTranslationKey(), "Dynamic Profile");
        builder.add(StrawStatueStyleOptions.DYNAMIC_PROFILE.getDescriptionKey(),
                "Have the statue automatically resolve to the current skin of the targeted player. Otherwise the player skin at the time it is set is kept forever.");
        builder.add(StrawStatueStyleOptions.IMMOVABLE.getTranslationKey(), "Immovable");
        builder.add(StrawStatueStyleOptions.IMMOVABLE.getDescriptionKey(),
                "Makes the statue have no physics in the world.");
        builder.add(StrawStatueStyleOptions.CROUCHING.getTranslationKey(), "Crouching");
        builder.add(StrawStatueStyleOptions.CROUCHING.getDescriptionKey(),
                "Makes the statue show as if it were crouching.");
        builder.add(StrawStatueStyleOptions.SEALED.getTranslationKey(), "Sealed");
        builder.add(StrawStatueStyleOptions.SEALED.getDescriptionKey(),
                "Disallows changing equipment and opening this menu in survival mode.");
        builder.add(StrawStatuePosePartMutators.CAPE.getTranslationKey(), "Cape");
        builder.add(StrawStatueScaleScreen.ROTATION_X_TRANSLATION_KEY, "X-Rotation:");
        builder.add(StrawStatueScaleScreen.ROTATION_Y_TRANSLATION_KEY, "Y-Rotation:");
        builder.add(StrawStatueScaleScreen.ROTATION_Z_TRANSLATION_KEY, "Z-Rotation:");
    }
}
