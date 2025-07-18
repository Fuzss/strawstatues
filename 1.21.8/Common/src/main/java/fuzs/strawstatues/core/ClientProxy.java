package fuzs.strawstatues.core;

import fuzs.strawstatues.client.gui.screens.StrawStatueModelPartsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Optional;

public class ClientProxy extends ServerProxy {

    @Override
    public void setModelPartsScreenGameProfile(Optional<ResolvableProfile> profile) {
        if (Minecraft.getInstance().screen instanceof StrawStatueModelPartsScreen screen) {
            profile.map(ResolvableProfile::gameProfile).ifPresent(screen::setGameProfile);
        }
    }
}
