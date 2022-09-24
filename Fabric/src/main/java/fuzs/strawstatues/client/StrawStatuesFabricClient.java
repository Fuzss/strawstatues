package fuzs.strawstatues.client;

import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.client.ArmorStatuesApiClient;
import net.fabricmc.api.ClientModInitializer;

public class StrawStatuesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApiClient());
        ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new StrawStatuesClient());
    }
}
