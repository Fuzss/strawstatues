package fuzs.strawstatues.client;

import fuzs.armorstatues.api.ArmorStatuesApi;
import fuzs.armorstatues.api.client.ArmorStatuesApiClient;
import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.strawstatues.StrawStatues;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = StrawStatues.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StrawStatuesForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        if (!CoreServices.ENVIRONMENT.isModLoaded(ArmorStatuesApi.MOD_ID)) {
            ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApiClient());
        }
        ClientCoreServices.FACTORIES.clientModConstructor(StrawStatues.MOD_ID).accept(new StrawStatuesClient());
    }
}
