package fuzs.strawstatues;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.strawstatues.data.ModEntityTypeLootProvider;
import fuzs.strawstatues.data.ModLanguageProvider;
import fuzs.strawstatues.data.ModModelProvider;
import fuzs.strawstatues.data.ModRecipeProvider;
import fuzs.strawstatues.init.ForgeModRegistry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(StrawStatues.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrawStatuesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(StrawStatues.MOD_ID, StrawStatues::new);
        ForgeModRegistry.touch();
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        evt.getGenerator().addProvider(true, new ModEntityTypeLootProvider(evt, StrawStatues.MOD_ID));
        evt.getGenerator().addProvider(true, new ModLanguageProvider(evt, StrawStatues.MOD_ID));
        evt.getGenerator().addProvider(true, new ModModelProvider(evt, StrawStatues.MOD_ID));
        evt.getGenerator().addProvider(true, new ModRecipeProvider(evt, StrawStatues.MOD_ID));
    }
}
