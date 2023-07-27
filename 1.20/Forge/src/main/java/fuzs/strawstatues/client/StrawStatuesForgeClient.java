package fuzs.strawstatues.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.strawstatues.StrawStatues;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = StrawStatues.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StrawStatuesForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(StrawStatues.MOD_ID, StrawStatuesClient::new);
    }
}
