package fuzs.strawstatues.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueCapeModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;

public class StrawStatueCapeLayer extends CapeLayer {
    private final HumanoidModel<PlayerRenderState> bigModel;
    private final HumanoidModel<PlayerRenderState> smallModel;

    public StrawStatueCapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderer, EntityModelSet entityModels, EquipmentAssetManager equipmentAssets) {
        super(renderer, entityModels, equipmentAssets);
        this.bigModel = new StrawStatueCapeModel(entityModels.bakeLayer(ModClientRegistry.STRAW_STATUE_CAPE));
        this.smallModel = new StrawStatueCapeModel(entityModels.bakeLayer(ModClientRegistry.STRAW_STATUE_BABY_CAPE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, PlayerRenderState renderState, float yRot, float xRot) {
        this.model = renderState.isBaby ? this.smallModel : this.bigModel;
        super.render(poseStack, bufferSource, packedLight, renderState, yRot, xRot);
    }
}
