package fuzs.strawstatues;

import fuzs.puzzleslib.core.CommonFactories;
import fuzs.strawstatues.api.StatuesApi;
import fuzs.strawstatues.data.ModItemModelProvider;
import fuzs.strawstatues.data.ModLanguageProvider;
import fuzs.strawstatues.data.ModLootTableProvider;
import fuzs.strawstatues.data.ModRecipeProvider;
import fuzs.strawstatues.init.ForgeModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(StrawStatues.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrawStatuesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        CommonFactories.INSTANCE.modConstructor(StrawStatues.MOD_ID).accept(new StatuesApi());
        CommonFactories.INSTANCE.modConstructor(StrawStatues.MOD_ID).accept(new StrawStatues());
        registerHandlers();
    }

    private static void registerHandlers() {
        // high priority so we run before the Quark mod
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, (final PlayerInteractEvent.EntityInteractSpecific evt) -> {
            StrawStatue.onUseEntityAt(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), evt.getLocalPos()).ifPresent(result -> {
                evt.setCancellationResult(result);
                evt.setCanceled(true);
            });
        });
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
