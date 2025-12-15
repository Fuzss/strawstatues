package fuzs.strawstatues.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ItemTooltipRegistry;
import fuzs.statuemenus.api.v1.client.gui.screens.AbstractStatueScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.StatueRotationsScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.StatueScreenFactory;
import fuzs.statuemenus.api.v1.client.gui.screens.StatueStyleScreen;
import fuzs.statuemenus.api.v1.helper.ArmorStandInteractHelper;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.StatueHolder;
import fuzs.statuemenus.api.v1.world.inventory.StatueMenu;
import fuzs.statuemenus.api.v1.world.inventory.data.PosePartMutator;
import fuzs.statuemenus.api.v1.world.inventory.data.StatuePose;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueScreenType;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueStyleOption;
import fuzs.strawstatues.client.entity.ClientStrawStatue;
import fuzs.strawstatues.client.gui.screens.StrawStatueModelPartsScreen;
import fuzs.strawstatues.client.gui.screens.StrawStatuePositionScreen;
import fuzs.strawstatues.client.gui.screens.StrawStatueTexturesScreen;
import fuzs.strawstatues.client.model.geom.ModModelLayers;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.inventory.data.StrawStatuePosePartMutators;
import fuzs.strawstatues.world.inventory.data.StrawStatueScreenTypes;
import fuzs.strawstatues.world.inventory.data.StrawStatueStyleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerEarsModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

import java.util.List;

/**
 * Some lambdas / method references in this class are problematic for javac and / or Mercury. They need to be kept as
 * they are.
 */
public class StrawStatuesClient implements ClientModConstructor {

    @SuppressWarnings("Convert2Lambda")
    @Override
    public void onClientSetup() {
        ItemTooltipRegistry.ITEM.registerItemTooltip(ModRegistry.STRAW_STATUE_ITEM.value(),
                ArmorStandInteractHelper.getArmorStandHoverText());
        StatueScreenFactory.register(StrawStatueScreenTypes.MODEL_PARTS, StrawStatueModelPartsScreen::new);
        StatueScreenFactory.register(StrawStatueScreenTypes.POSITION, StrawStatuePositionScreen::new);
        StatueScreenFactory.register(StrawStatueScreenTypes.TEXTURES, StrawStatueTexturesScreen::new);
        StatueScreenFactory.register(StrawStatueScreenTypes.ROTATIONS, new StatueScreenFactory<AbstractStatueScreen>() {
            @Override
            public AbstractStatueScreen create(StatueHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
                return new StatueRotationsScreen(holder, inventory, component, dataSyncHandler) {
                    @Override
                    public List<PosePartMutator> getPosePartMutators() {
                        return StrawStatuePosePartMutators.TYPES;
                    }

                    @Override
                    protected StatuePose setupRandomPose(StatuePose statuePose) {
                        return statuePose;
                    }

                    @Override
                    protected boolean isPosePartMutatorActive(PosePartMutator posePartMutator, LivingEntity livingEntity) {
                        if (posePartMutator == StrawStatuePosePartMutators.CAPE) {
                            if (livingEntity instanceof ClientStrawStatue strawStatue && strawStatue.isModelPartShown(
                                    PlayerModelPart.CAPE) && strawStatue.getSkin().cape() != null) {
                                ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
                                return !this.hasLayer(itemStack, EquipmentClientInfo.LayerType.WINGS);
                            } else {
                                return false;
                            }
                        } else {
                            return super.isPosePartMutatorActive(posePartMutator, livingEntity);
                        }
                    }

                    /**
                     * @see net.minecraft.client.renderer.entity.layers.CapeLayer#hasLayer(ItemStack, EquipmentClientInfo.LayerType)
                     */
                    private boolean hasLayer(ItemStack itemStack, EquipmentClientInfo.LayerType layer) {
                        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
                        if (equippable != null && !equippable.assetId().isEmpty()) {
                            EquipmentAssetManager equipmentAssets = Minecraft.getInstance()
                                    .getEntityRenderDispatcher().equipmentAssets;
                            EquipmentClientInfo equipmentClientInfo = equipmentAssets.get(equippable.assetId().get());
                            return !equipmentClientInfo.getLayers(layer).isEmpty();
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public StatueScreenType getScreenType() {
                        return StrawStatueScreenTypes.ROTATIONS;
                    }
                };
            }
        });
        StatueScreenFactory.register(StrawStatueScreenTypes.STYLE, new StatueScreenFactory<AbstractStatueScreen>() {
            @Override
            public AbstractStatueScreen create(StatueHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
                return new StatueStyleScreen<StrawStatue>(holder, inventory, component, dataSyncHandler) {
                    @Override
                    protected List<StatueStyleOption<? super StrawStatue>> getStyleOptions() {
                        return StrawStatueStyleOptions.TYPES;
                    }

                    @Override
                    public StatueScreenType getScreenType() {
                        return StrawStatueScreenTypes.STYLE;
                    }
                };
            }
        });
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.STRAW_STATUE_MENU_TYPE.value(),
                (StatueMenu menu, Inventory inventory, Component component) -> StatueScreenFactory.createLastScreenType(
                        menu,
                        inventory,
                        component));
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer((EntityType<ClientStrawStatue>) (EntityType<?>) ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(),
                StrawStatueRenderer::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE, () -> {
            return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64);
        });
        context.registerArmorDefinition(ModModelLayers.STRAW_STATUE_ARMOR,
                () -> PlayerModel.createArmorMeshSet(LayerDefinitions.INNER_ARMOR_DEFORMATION,
                        LayerDefinitions.OUTER_ARMOR_DEFORMATION).map((MeshDefinition meshDefinition) -> {
                    return LayerDefinition.create(meshDefinition, 64, 32);
                }));
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_SLIM, () -> {
            return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64);
        });
        context.registerArmorDefinition(ModModelLayers.STRAW_STATUE_SLIM_ARMOR,
                () -> PlayerModel.createArmorMeshSet(LayerDefinitions.INNER_ARMOR_DEFORMATION,
                        LayerDefinitions.OUTER_ARMOR_DEFORMATION).map((MeshDefinition meshDefinition) -> {
                    return LayerDefinition.create(meshDefinition, 64, 32);
                }));
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_CAPE, PlayerCapeModel::createCapeLayer);
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_EARS, PlayerEarsModel::createEarsLayer);
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_BABY, () -> {
            return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64)
                    .apply(HumanoidModel.BABY_TRANSFORMER);
        });
        context.registerArmorDefinition(ModModelLayers.STRAW_STATUE_BABY_ARMOR,
                () -> PlayerModel.createArmorMeshSet(LayerDefinitions.INNER_ARMOR_DEFORMATION,
                        LayerDefinitions.OUTER_ARMOR_DEFORMATION).map((MeshDefinition meshDefinition) -> {
                    return LayerDefinition.create(meshDefinition, 64, 32);
                }).map((LayerDefinition layerDefinition) -> {
                    return layerDefinition.apply(HumanoidModel.BABY_TRANSFORMER);
                }));
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_BABY_SLIM, () -> {
            return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64)
                    .apply(HumanoidModel.BABY_TRANSFORMER);
        });
        context.registerArmorDefinition(ModModelLayers.STRAW_STATUE_BABY_SLIM_ARMOR,
                () -> PlayerModel.createArmorMeshSet(LayerDefinitions.INNER_ARMOR_DEFORMATION,
                        LayerDefinitions.OUTER_ARMOR_DEFORMATION).map((MeshDefinition meshDefinition) -> {
                    return LayerDefinition.create(meshDefinition, 64, 32);
                }).map((LayerDefinition layerDefinition) -> {
                    return layerDefinition.apply(HumanoidModel.BABY_TRANSFORMER);
                }));
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_BABY_CAPE, () -> {
            return PlayerCapeModel.createCapeLayer().apply(HumanoidModel.BABY_TRANSFORMER);
        });
        context.registerLayerDefinition(ModModelLayers.STRAW_STATUE_BABY_EARS, () -> {
            return PlayerEarsModel.createEarsLayer().apply(HumanoidModel.BABY_TRANSFORMER);
        });
    }
}
