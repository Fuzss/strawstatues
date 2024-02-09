package fuzs.strawstatues.forge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.strawstatues.StrawStatues;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(StrawStatues.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrawStatuesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(StrawStatues.MOD_ID, StrawStatues::new);
    }
}
