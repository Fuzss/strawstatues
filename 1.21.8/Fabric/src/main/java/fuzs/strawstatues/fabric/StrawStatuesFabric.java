package fuzs.strawstatues.fabric;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.strawstatues.StrawStatues;
import net.fabricmc.api.ModInitializer;

public class StrawStatuesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(StrawStatues.MOD_ID, StrawStatues::new);
    }
}
