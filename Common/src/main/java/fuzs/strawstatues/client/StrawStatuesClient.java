package fuzs.strawstatues.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandRotationsScreen;
import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandScreenFactory;
import fuzs.strawstatues.api.world.inventory.ArmorStandMenu;
import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatueStyleScreen;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;

public class StrawStatuesClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        ArmorStandScreenFactory.register(StrawStatue.MODEL_PARTS_SCREEN_TYPE, StrawStatueModelPartsScreen::new);
        ArmorStandScreenFactory.register(StrawStatue.STRAW_STATUE_STYLE_SCREEN_TYPE, StrawStatueStyleScreen::new);
        ArmorStandRotationsScreen.registerPosePartMutatorFilter(StrawStatue.CAPE_POSE_PART_MUTATOR, armorStand -> {
            StrawStatue strawStatue = (StrawStatue) armorStand;
            if (strawStatue.isModelPartShown(PlayerModelPart.CAPE)) {
                return StrawStatueRenderer.getPlayerProfileTexture(strawStatue, MinecraftProfileTexture.Type.CAPE).isPresent();
            }
            return false;
        });
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModRegistry.STRAW_STATUE_ENTITY_TYPE.get(), StrawStatueRenderer::new);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        // compiler doesn't like method reference :(
        context.registerMenuScreen(ModRegistry.STRAW_STATUE_MENU_TYPE.get(), (ArmorStandMenu menu, Inventory inventory, Component component) -> {
            return ArmorStandScreenFactory.createLastScreenType(menu, inventory, component);
        });
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE, StrawStatueModel::createBodyLayer);
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_INNER_ARMOR, () -> ArmorStandArmorModel.createBodyLayer(new CubeDeformation(0.5F)));
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR, () -> ArmorStandArmorModel.createBodyLayer(new CubeDeformation(1.0F)));
    }
}
