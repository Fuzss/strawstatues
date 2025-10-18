package fuzs.strawstatues.neoforge.services;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import fuzs.strawstatues.services.ClientAbstractions;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

public final class NeoForgeClientAbstractions implements ClientAbstractions {
    @Override
    public StrawStatueRenderer createStrawStatueRenderer(EntityRendererProvider.Context context) {
        // The renderer class must be compiled on NeoForge, as the AvatarRenderer::submit method is patched in there.
        // When compiling for Common, it compiles against the vanilla class, which leads to an incorrect super call.
        // https://github.com/neoforged/NeoForge/issues/2731#issuecomment-3414863112
        return new StrawStatueRenderer(context) {
            @Override
            public void submit(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
                this.model = this.models.get(renderState.skin.model()).getModel(renderState.isBaby);
                super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
            }
        };
    }
}
