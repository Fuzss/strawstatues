package fuzs.strawstatues.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.statuemenus.api.v1.world.inventory.StatueMenu;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.core.Proxy;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.item.StrawStatueItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(StrawStatues.MOD_ID);
    public static final Holder.Reference<DataComponentType<PlayerSkin.Patch>> SKIN_PATCH_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "skin_patch",
            (DataComponentType.Builder<PlayerSkin.Patch> builder) -> {
                return builder.persistent(PlayerSkin.Patch.MAP_CODEC.codec())
                        .networkSynchronized(PlayerSkin.Patch.STREAM_CODEC);
            });
    public static final Holder.Reference<Item> STRAW_STATUE_ITEM = REGISTRIES.registerItem("straw_statue",
            StrawStatueItem::new,
            () -> new Item.Properties().stacksTo(16));
    public static final Holder.Reference<EntityType<StrawStatue>> STRAW_STATUE_ENTITY_TYPE = REGISTRIES.registerEntityType(
            "straw_statue",
            () -> {
                return EntityType.Builder.of(Proxy.INSTANCE::createStrawStatue, MobCategory.MISC)
                        .sized(0.6F, 1.8F)
                        .eyeHeight(1.62F)
                        .vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT)
                        .clientTrackingRange(32)
                        .updateInterval(2);
            });
    public static final Holder.Reference<MenuType<StatueMenu>> STRAW_STATUE_MENU_TYPE = REGISTRIES.registerMenuType(
            "straw_statue",
            (int containerId, Inventory inventory, StatueMenu.Data data) -> {
                return new <StrawStatue>StatueMenu(ModRegistry.STRAW_STATUE_MENU_TYPE.value(),
                        containerId,
                        inventory,
                        data,
                        (StrawStatue strawStatue) -> strawStatue);
            },
            StatueMenu.Data.STREAM_CODEC);
    public static final Holder.Reference<EntityDataSerializer<PlayerSkin.Patch>> SKIN_PATCH_ENTITY_DATA_SERIALIZER = REGISTRIES.registerEntityDataSerializer(
            "skin_patch",
            () -> EntityDataSerializer.forValueType(PlayerSkin.Patch.STREAM_CODEC));

    public static void bootstrap() {
        // NO-OP
    }
}
