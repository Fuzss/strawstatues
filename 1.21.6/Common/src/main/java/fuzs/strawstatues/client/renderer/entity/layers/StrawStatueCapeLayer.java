package fuzs.strawstatues.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueCapeModel;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

/**
 * @see CapeLayer
 */
public class StrawStatueCapeLayer extends RenderLayer<StrawStatueRenderState, StrawStatueModel> {
    private final HumanoidModel<StrawStatueRenderState> bigModel;
    private final HumanoidModel<StrawStatueRenderState> smallModel;
    private final EquipmentAssetManager equipmentAssets;

    public StrawStatueCapeLayer(RenderLayerParent<StrawStatueRenderState, StrawStatueModel> renderer, EntityModelSet entityModels, EquipmentAssetManager equipmentAssets) {
        super(renderer);
        this.bigModel = new StrawStatueCapeModel(entityModels.bakeLayer(ModClientRegistry.STRAW_STATUE_CAPE));
        this.smallModel = new StrawStatueCapeModel(entityModels.bakeLayer(ModClientRegistry.STRAW_STATUE_BABY_CAPE));
        this.equipmentAssets = equipmentAssets;
    }

    private boolean hasLayer(ItemStack itemStack, EquipmentClientInfo.LayerType layer) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && !equippable.assetId().isEmpty()) {
            EquipmentClientInfo equipmentClientInfo = this.equipmentAssets.get(equippable.assetId().get());
            return !equipmentClientInfo.getLayers(layer).isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, StrawStatueRenderState renderState, float yRot, float xRot) {
        if (!renderState.isInvisible && renderState.showCape) {
            PlayerSkin playerSkin = renderState.skin;
            if (playerSkin.capeTexture() != null) {
                if (!this.hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
                    poseStack.pushPose();
                    if (this.hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
                        poseStack.translate(0.0F, -0.053125F, 0.06875F);
                    }

                    HumanoidModel<StrawStatueRenderState> model = renderState.isBaby ? this.smallModel : this.bigModel;
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(playerSkin.capeTexture()));
                    this.getParentModel().copyPropertiesTo(model);
                    model.setupAnim(renderState);
                    model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            }
        }
    }
}
