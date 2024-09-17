package fuzs.strawstatues.core;

import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Optional;

public class ServerProxy implements Proxy {

    @Override
    public void setModelPartsScreenGameProfile(Optional<ResolvableProfile> profile) {
        // NO-OP
    }
}
