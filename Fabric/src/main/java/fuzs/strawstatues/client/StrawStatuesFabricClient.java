package fuzs.strawstatues.client;

import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.client.StatuesApiClient;
import net.fabricmc.api.ClientModInitializer;

public class StrawStatuesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new StatuesApiClient());
        ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new StrawStatuesClient());
    }
}
