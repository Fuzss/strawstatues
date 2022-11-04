package fuzs.strawstatues.api.world.inventory.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;

public class ArmorStandScreenType {
    public static final ArmorStandScreenType EQUIPMENT = new ArmorStandScreenType("equipment", new ItemStack(Items.IRON_CHESTPLATE), true);
    public static final ArmorStandScreenType ROTATIONS = new ArmorStandScreenType("rotations", new ItemStack(Items.COMPASS));
    public static final ArmorStandScreenType STYLE = new ArmorStandScreenType("style", new ItemStack(Items.PAINTING));
    public static final ArmorStandScreenType POSES = new ArmorStandScreenType("poses", new ItemStack(Items.SPYGLASS));
    public static final ArmorStandScreenType POSITION = new ArmorStandScreenType("position", new ItemStack(Items.GRASS_BLOCK));
    public static final ArmorStandScreenType ALIGNMENTS = new ArmorStandScreenType("alignments", new ItemStack(Items.DIAMOND_PICKAXE));

    private final String translationId;
    private final ItemStack icon;
    private final boolean requiresServer;

    public ArmorStandScreenType(String translationId, ItemStack icon) {
        this(translationId, icon, false);
    }

    public ArmorStandScreenType(String translationId, ItemStack icon, boolean requiresServer) {
        this.translationId = translationId;
        this.icon = icon;
        this.requiresServer = requiresServer;
    }

    @Override
    public String toString() {
        return this.translationId.toUpperCase(Locale.ROOT);
    }

    public Component getComponent() {
        return new TranslatableComponent("armorstatues.screen.type." + this.translationId);
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public boolean requiresServer() {
        return this.requiresServer;
    }
}
