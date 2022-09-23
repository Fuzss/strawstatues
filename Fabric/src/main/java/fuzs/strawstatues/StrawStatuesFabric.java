package fuzs.strawstatues;

import fuzs.armorstatues.api.ArmorStatuesApi;
import fuzs.puzzleslib.core.CoreServices;
import net.fabricmc.api.ModInitializer;

public class StrawStatuesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        if (!CoreServices.ENVIRONMENT.isModLoaded(ArmorStatuesApi.MOD_ID)) {
            CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApi());
        }
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new StrawStatues());
    }
}
