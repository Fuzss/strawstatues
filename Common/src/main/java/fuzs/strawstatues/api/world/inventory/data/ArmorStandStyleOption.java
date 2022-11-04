package fuzs.strawstatues.api.world.inventory.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public interface ArmorStandStyleOption {
    int ARMOR_STAND_ALL_SLOTS_DISABLED = 4144959;
    BiMap<ResourceLocation, ArmorStandStyleOption> OPTIONS_REGISTRY = HashBiMap.create();

    String getTranslationId();

    default Component getComponent() {
        return new TranslatableComponent("armorstatues.screen.style." + this.getTranslationId());
    }

    default Component getDescriptionComponent() {
        return new TranslatableComponent("armorstatues.screen.style." + this.getTranslationId() + ".description");
    }

    void setOption(ArmorStand armorStand, boolean setting);

    boolean getOption(ArmorStand armorStand);

    void toTag(CompoundTag tag, boolean currentValue);

    default boolean allowChanges(Player player) {
        return true;
    }

    default ResourceLocation getId() {
        return Objects.requireNonNull(OPTIONS_REGISTRY.inverse().get(this), "Armor stand style option %s has not been registered".formatted(this.getTranslationId()));
    }

    static void register(ResourceLocation id, ArmorStandStyleOption styleOption) {
        if (OPTIONS_REGISTRY.containsValue(styleOption) || OPTIONS_REGISTRY.put(id, styleOption) != null) throw new IllegalStateException("Attempted to register duplicate armor stand style option for id %s".formatted(id));
    }

    static ArmorStandStyleOption get(ResourceLocation id) {
        return Objects.requireNonNull(OPTIONS_REGISTRY.get(id), "No armor stand style option registered for id %s".formatted(id));
    }

    static boolean getArmorStandData(ArmorStand armorStand, int offset) {
        return (armorStand.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS) & offset) != 0;
    }

    static void setArmorStandData(ArmorStand armorStand, boolean setting, int offset) {
        armorStand.getEntityData().set(ArmorStand.DATA_CLIENT_FLAGS, setBit(armorStand.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS), offset, setting));
    }

    private static byte setBit(byte oldBit, int offset, boolean value) {
        if (value) {
            oldBit = (byte) (oldBit | offset);
        } else {
            oldBit = (byte) (oldBit & ~offset);
        }
        return oldBit;
    }
}
