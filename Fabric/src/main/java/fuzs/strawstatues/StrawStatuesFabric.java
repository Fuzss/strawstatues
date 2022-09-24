package fuzs.strawstatues;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.strawstatues.api.ArmorStatuesApi;
import net.fabricmc.api.ModInitializer;

public class StrawStatuesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApi());
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new StrawStatues());
    }
}
