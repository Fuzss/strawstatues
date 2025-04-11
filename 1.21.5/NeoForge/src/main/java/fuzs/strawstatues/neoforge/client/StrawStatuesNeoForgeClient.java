package fuzs.strawstatues.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.client.StrawStatuesClient;
import fuzs.strawstatues.data.client.ModLanguageProvider;
import fuzs.strawstatues.data.client.ModModelProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = StrawStatues.MOD_ID, dist = Dist.CLIENT)
public class StrawStatuesNeoForgeClient {

    public StrawStatuesNeoForgeClient() {
        ClientModConstructor.construct(StrawStatues.MOD_ID, StrawStatuesClient::new);
        DataProviderHelper.registerDataProviders(StrawStatues.MOD_ID, ModLanguageProvider::new, ModModelProvider::new);
    }
}
