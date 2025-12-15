package fuzs.strawstatues.fabric.services;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import fuzs.strawstatues.services.ClientAbstractions;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

public final class FabricClientAbstractions implements ClientAbstractions {
    @Override
    public StrawStatueRenderer createStrawStatueRenderer(EntityRendererProvider.Context context) {
        return new StrawStatueRenderer(context) {
            @Override
            public void submit(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
                this.model = this.models.get(renderState.skin.model()).getModel(renderState.isBaby);
                super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
            }
        };
    }
}
