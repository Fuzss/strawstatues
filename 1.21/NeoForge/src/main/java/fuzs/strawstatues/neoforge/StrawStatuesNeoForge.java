package fuzs.strawstatues.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.data.ModEntityTypeLootProvider;
import fuzs.strawstatues.data.ModRecipeProvider;
import fuzs.strawstatues.data.client.ModLanguageProvider;
import fuzs.strawstatues.data.client.ModModelProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(StrawStatues.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrawStatuesNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(StrawStatues.MOD_ID, StrawStatues::new);
        DataProviderHelper.registerDataProviders(StrawStatues.MOD_ID, ModEntityTypeLootProvider::new,
                ModLanguageProvider::new, ModModelProvider::new, ModRecipeProvider::new
        );
    }
}
