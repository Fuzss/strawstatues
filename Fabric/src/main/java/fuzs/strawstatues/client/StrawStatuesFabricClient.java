package fuzs.strawstatues.client;

import fuzs.armorstatues.api.ArmorStatuesApi;
import fuzs.armorstatues.api.client.ArmorStatuesApiClient;
import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.strawstatues.StrawStatues;
import net.fabricmc.api.ClientModInitializer;

public class StrawStatuesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (!CoreServices.ENVIRONMENT.isModLoaded(ArmorStatuesApi.MOD_ID)) {
            ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApiClient());
        }
        ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new StrawStatuesClient());
    }
}
