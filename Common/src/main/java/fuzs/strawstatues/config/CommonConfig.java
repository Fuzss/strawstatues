package fuzs.strawstatues.config;

import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ValueCallback;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.core.AbstractConfigBuilder;
import fuzs.puzzleslib.config.core.AbstractConfigValue;
import fuzs.puzzleslib.config.serialization.ConfigDataSet;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class CommonConfig implements ConfigCore {
    public AbstractConfigValue<Boolean> darkTheme;
    @Config(description = "The item required to be held to open the configuration screen when shift-interacting.")
    String useItemRaw = ConfigDataSet.toString(Registry.ITEM_REGISTRY, Items.STICK).get(0);

    public Item useItem;

    @Override
    public void addToBuilder(AbstractConfigBuilder builder, ValueCallback callback) {
        this.darkTheme = builder.comment("Use a dark theme for the configuration screens.").define("dark_theme", false);
        callback.accept(this.darkTheme, v -> {});
    }

    @Override
    public void afterConfigReload() {
        this.useItem = ConfigDataSet.of(Registry.ITEM_REGISTRY, List.of(this.useItemRaw)).toMap().keySet().stream().findAny().orElse(Items.STICK);
    }
}
