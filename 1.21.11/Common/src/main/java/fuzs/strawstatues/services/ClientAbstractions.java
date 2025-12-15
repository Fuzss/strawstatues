package fuzs.strawstatues.services;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.strawstatues.client.renderer.entity.StrawStatueRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public interface ClientAbstractions {
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    StrawStatueRenderer createStrawStatueRenderer(EntityRendererProvider.Context context);
}
