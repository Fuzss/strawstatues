package fuzs.strawstatues.mixin.client;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderDispatcherMixin {
    @Shadow
    private Map<EntityType<?>, EntityRenderer<?, ?>> renderers;

    @Inject(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
            at = @At("HEAD"),
            cancellable = true)
    public <S extends EntityRenderState> void getRenderer(S renderState, CallbackInfoReturnable<EntityRenderer<?, ? super S>> callback) {
        if (renderState instanceof StrawStatueRenderState) {
            callback.setReturnValue((EntityRenderer<?, ? super S>) this.renderers.get(renderState.entityType));
        }
    }
}
