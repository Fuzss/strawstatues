package fuzs.strawstatues.client;

import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.client.StatuesApiClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = StrawStatues.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StrawStatuesForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientFactories.INSTANCE.clientModConstructor(StrawStatues.MOD_ID).accept(new StatuesApiClient());
        ClientFactories.INSTANCE.clientModConstructor(StrawStatues.MOD_ID).accept(new StrawStatuesClient());
    }
}
