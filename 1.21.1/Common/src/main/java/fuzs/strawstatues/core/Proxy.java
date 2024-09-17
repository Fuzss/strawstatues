package fuzs.strawstatues.core;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Optional;

public interface Proxy {
    Proxy INSTANCE = ModLoaderEnvironment.INSTANCE.isClient() ? new ClientProxy() : new ServerProxy();

    void setModelPartsScreenGameProfile(Optional<ResolvableProfile> profile);
}
