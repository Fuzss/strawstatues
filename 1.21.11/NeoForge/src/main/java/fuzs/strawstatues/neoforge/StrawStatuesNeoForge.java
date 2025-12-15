package fuzs.strawstatues.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.data.loot.ModEntityTypeLootProvider;
import fuzs.strawstatues.data.ModRecipeProvider;
import net.neoforged.fml.common.Mod;

@Mod(StrawStatues.MOD_ID)
public class StrawStatuesNeoForge {

    public StrawStatuesNeoForge() {
        ModConstructor.construct(StrawStatues.MOD_ID, StrawStatues::new);
        DataProviderHelper.registerDataProviders(StrawStatues.MOD_ID,
                ModEntityTypeLootProvider::new,
                ModRecipeProvider::new
        );
    }
}
