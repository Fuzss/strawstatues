package fuzs.strawstatues.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.strawstatues.StrawStatues;
import net.fabricmc.api.ClientModInitializer;

public class StrawStatuesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(StrawStatues.MOD_ID, StrawStatuesClient::new);
    }
}
