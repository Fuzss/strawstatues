package fuzs.strawstatues;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.strawstatues.api.ArmorStatuesApi;
import fuzs.strawstatues.data.ModItemModelProvider;
import fuzs.strawstatues.data.ModLanguageProvider;
import fuzs.strawstatues.data.ModLootTableProvider;
import fuzs.strawstatues.data.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(StrawStatues.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrawStatuesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApi());
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new StrawStatues());
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator generator = evt.getGenerator();
        final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
        generator.addProvider(new ModRecipeProvider(generator));
        generator.addProvider(new ModLanguageProvider(generator, StrawStatues.MOD_ID));
        generator.addProvider(new ModLootTableProvider(generator, StrawStatues.MOD_ID));
        generator.addProvider(new ModItemModelProvider(generator, StrawStatues.MOD_ID, existingFileHelper));
    }
}
