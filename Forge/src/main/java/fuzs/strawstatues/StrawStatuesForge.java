package fuzs.strawstatues;

import fuzs.armorstatues.api.ArmorStatuesApi;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.strawstatues.data.ModItemModelProvider;
import fuzs.strawstatues.data.ModLanguageProvider;
import fuzs.strawstatues.data.ModLootTableProvider;
import fuzs.strawstatues.data.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(StrawStatues.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrawStatuesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        if (!CoreServices.ENVIRONMENT.isModLoaded(ArmorStatuesApi.MOD_ID)) {
            CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApi());
        }
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new StrawStatues());
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator generator = evt.getGenerator();
        final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
        generator.addProvider(true, new ModRecipeProvider(generator));
        generator.addProvider(true, new ModLanguageProvider(generator, StrawStatues.MOD_ID));
        generator.addProvider(true, new ModLootTableProvider(generator, StrawStatues.MOD_ID));
        generator.addProvider(true, new ModItemModelProvider(generator, StrawStatues.MOD_ID, existingFileHelper));
    }
}
