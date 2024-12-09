package fuzs.strawstatues.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandInInventoryRenderer;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandRotationsScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandScreenFactory;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.client.gui.screens.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.StrawStatuePositionScreen;
import fuzs.strawstatues.client.gui.screens.StrawStatueScaleScreen;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;

public class StrawStatuesClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        ArmorStandScreenFactory.register(ModRegistry.MODEL_PARTS_SCREEN_TYPE, StrawStatueModelPartsScreen::new);
        ArmorStandScreenFactory.register(ModRegistry.STRAW_STATUE_POSITION_SCREEN_TYPE, StrawStatuePositionScreen::new);
        ArmorStandScreenFactory.register(ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE, StrawStatueScaleScreen::new);
        ArmorStandRotationsScreen.registerPosePartMutatorFilter(ModRegistry.CAPE_POSE_PART_MUTATOR, (ArmorStand armorStand) -> {
            StrawStatue strawStatue = (StrawStatue) armorStand;
            if (strawStatue.isModelPartShown(PlayerModelPart.CAPE)) {
                return StrawStatueRenderer.getPlayerProfileTexture(strawStatue).map(PlayerSkin::capeTexture).isPresent();
            } else {
                return false;
            }
        });
        ArmorStandInInventoryRenderer.setArmorStandRenderer((guiGraphics, posX, posY, scale, mouseX, mouseY, livingEntity) -> {
            float modelScale = 0.0F;
            Rotations entityRotations = null;
            if (livingEntity instanceof StrawStatue strawStatue) {
                modelScale = strawStatue.getEntityScale();
                entityRotations = strawStatue.getEntityRotations();
                strawStatue.setEntityScale(StrawStatue.DEFAULT_ENTITY_SCALE);
                strawStatue.setEntityRotations(StrawStatue.DEFAULT_ENTITY_ROTATIONS.getX(), StrawStatue.DEFAULT_ENTITY_ROTATIONS.getZ());
            }
            ArmorStandInInventoryRenderer.SIMPLE.renderEntityInInventory(guiGraphics, posX, posY, scale, mouseX, mouseY, livingEntity);
            if (livingEntity instanceof StrawStatue strawStatue) {
                strawStatue.setEntityScale(modelScale);
                strawStatue.setEntityRotations(entityRotations.getX(), entityRotations.getZ());
            }
        });
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        // compiler doesn't like method reference :(
        context.registerMenuScreen(ModRegistry.STRAW_STATUE_MENU_TYPE.value(), (ArmorStandMenu menu, Inventory inventory, Component component) -> {
            return ArmorStandScreenFactory.createLastScreenType(menu, inventory, component);
        });
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), StrawStatueRenderer::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE, StrawStatueModel::createBodyLayer);
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_INNER_ARMOR, () -> ArmorStandArmorModel.createBodyLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION));
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR, () -> ArmorStandArmorModel.createBodyLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_CAPE, PlayerCapeModel::createCapeLayer);
    }
}
