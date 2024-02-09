package fuzs.strawstatues.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandInInventoryRenderer;
import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandRotationsScreen;
import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandScreenFactory;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandMenu;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatuePositionScreen;
import fuzs.strawstatues.client.gui.screens.strawstatue.StrawStatueScaleScreen;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;

public class StrawStatuesClient implements ClientModConstructor {

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void onClientSetup() {
        // compiler doesn't like method reference :(
        MenuScreens.register(ModRegistry.STRAW_STATUE_MENU_TYPE.get(), (ArmorStandMenu menu, Inventory inventory, Component component) -> {
            return ArmorStandScreenFactory.createLastScreenType(menu, inventory, component);
        });
        ArmorStandScreenFactory.register(ModRegistry.MODEL_PARTS_SCREEN_TYPE, StrawStatueModelPartsScreen::new);
        ArmorStandScreenFactory.register(ModRegistry.STRAW_STATUE_POSITION_SCREEN_TYPE, StrawStatuePositionScreen::new);
        ArmorStandScreenFactory.register(ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE, StrawStatueScaleScreen::new);
        ArmorStandRotationsScreen.registerPosePartMutatorFilter(ModRegistry.CAPE_POSE_PART_MUTATOR, armorStand -> {
            StrawStatue strawStatue = (StrawStatue) armorStand;
            if (strawStatue.isModelPartShown(PlayerModelPart.CAPE)) {
                return StrawStatueRenderer.getPlayerProfileTexture(strawStatue, MinecraftProfileTexture.Type.CAPE).isPresent();
            }
            return false;
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

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModRegistry.STRAW_STATUE_ENTITY_TYPE.get(), StrawStatueRenderer::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE, StrawStatueModel::createBodyLayer);
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_INNER_ARMOR, () -> ArmorStandArmorModel.createBodyLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION));
        context.registerLayerDefinition(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR, () -> ArmorStandArmorModel.createBodyLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
    }
}
